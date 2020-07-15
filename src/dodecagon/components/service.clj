(ns dodecagon.components.service
  (:require [com.stuartsierra.component :as component]
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

(defn runnable-service [service-map config routes]
  (let [base-service (if (= (:environment config) :prod)
                       prod-service
                       dev-service)]
    (-> base-service
        (merge {::pedestal/router          :prefix-tree
                ::pedestal/routes          #(route/expand-routes (deref routes))
                ::pedestal/resource-path   "/public"
                ::pedestal/type            :jetty
                ::pedestal/port            (:dev-port config)
                ;; TODO: add system-interceptor
                ;; ::pedestal/interceptors    (update )
                })
        pedestal/default-interceptors)))

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
