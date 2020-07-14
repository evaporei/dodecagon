(ns dodecagon.components.routes
  (:require [com.stuartsierra.component :as component]
            [dodecagon.service :as service]))

(defrecord Routes [routes]
  component/Lifecycle
  (start [this]
    (assoc this :routes routes))
  (stop [this]
    (dissoc this :routes)))

(defn new-routes []
  (->Routes #'service/routes))
