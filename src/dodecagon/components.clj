(ns dodecagon.components
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as pedestal]
            [dodecagon.components.config :refer [new-config]]
            [dodecagon.components.routes :refer [new-routes]]
            [dodecagon.components.service :refer [new-service]]
            [dodecagon.service :as service]))


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
    :routes (new-routes)
    :service (component/using (new-service) [:config :routes])
    :servlet (component/using (->Servlet {}) [:service])))

(def systems-map
  {:base base-system})

(defn create-and-start-system!
  ([]
    (create-and-start-system! :base))
  ([env]
    (component/start (env systems-map))))
