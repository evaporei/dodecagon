(ns dodecagon.components.service
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.interceptor.helpers :refer [before]]
            [io.pedestal.http :as pedestal]
            [io.pedestal.http.route :as route]))

(def prod-service
  {:env :prod})

(def dev-service
  (pedestal/dev-interceptors
    {:env                       :dev
     ::pedestal/join?           false
     ::pedestal/secure-headers  {:content-security-policy-settings {:object-src "none"}}
     ::pedestal/allowed-origins {:creds true :allowed-origins (constantly true)}}))

(defn add-components-interceptor [service]
  (before (fn [context] (assoc-in context [:request :components] service))))

(defn add-custom-interceptors [service-map service]
  (let [custom-interceptors [(add-components-interceptor service)]]
    (update service-map ::pedestal/interceptors concat custom-interceptors)))

(defn runnable-service [service config routes]
  (let [base-service (if (= (:environment config) :prod)
                       prod-service
                       dev-service)]
    (-> base-service
        (merge {::pedestal/router          :prefix-tree
                ::pedestal/routes          #(route/expand-routes (deref routes))
                ::pedestal/resource-path   "/public"
                ::pedestal/type            :jetty
                ::pedestal/port            (:dev-port config)})
        pedestal/default-interceptors
        (add-custom-interceptors service))))

(defrecord Service [config routes]
  component/Lifecycle
  (start [this]
    (assoc
      this
      :runnable-service
      (runnable-service this (:config config) (:routes routes))))
  (stop [this]
    (dissoc this :runnable-service)))

(defn new-service []
  (map->Service {}))
