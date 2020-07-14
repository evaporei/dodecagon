(ns dodecagon.components
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as pedestal]
            [io.pedestal.http.route :as route]
            [dodecagon.components.config :refer [new-config]]
            [dodecagon.service :as service]))

(defrecord Routes [routes]
  component/Lifecycle
  (start [this]
    (assoc this :routes routes))
  (stop [this]
    (dissoc this :routes)))

(def prod-service
  {:env :prod})

(def dev-service
  (pedestal/dev-interceptors
    {:env                        :dev
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

(defrecord Servlet [service]
  component/Lifecycle
  (start [this]
    (assoc
      this
      :instance
      (-> service
          :runnable-service
          (assoc ::pedestal/join? false)
          pedestal/create-server
          pedestal/start)))
  (stop [this]
    (pedestal/stop (:instance this))
    (dissoc this :instance)))

(def base-system
  (component/system-map
    :config (new-config)
    :routes (->Routes #'service/routes)
    :service (component/using (map->Service {}) [:config :routes])
    :servlet (component/using (->Servlet {}) [:service])))

(def systems-map
  {:base base-system})

(defn create-and-start-system!
  ([]
    (create-and-start-system! :base))
  ([env]
    (component/start (env systems-map))))
