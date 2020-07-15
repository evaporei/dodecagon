(ns dodecagon.components
  (:require [com.stuartsierra.component :as component]
            [dodecagon.components.config :refer [new-config]]
            [dodecagon.components.routes :refer [new-routes]]
            [dodecagon.components.service :refer [new-service]]
            [dodecagon.components.servlet :refer [new-servlet]]))

(def base-system
  (component/system-map
    :config (new-config)
    :routes (new-routes)
    :service (component/using (new-service) [:config :routes])
    :servlet (component/using (new-servlet) [:service])))

;; TODO: add test/dev key
(def systems-map
  {:base base-system})

(def system (atom nil))

(defn create-and-start-system!
  ([]
   (create-and-start-system! :base))
  ([env]
   (->> systems-map
        env
        component/start
        (reset! system))))

(defn stop-system! []
  (swap! system component/stop))

(defn restart-system! []
  (stop-system!)
  (create-and-start-system!))
