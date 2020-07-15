(ns dodecagon.components.routes
  (:require [com.stuartsierra.component :as component]
            [dodecagon.service :as service]))

(defrecord Routes [routes]
  component/Lifecycle
  (start [this]
    (assoc this :routes routes))
  (stop [this]
    (dissoc this :routes)))

(defn new-routes
  "The routes param needs to be passed to the `var` function or the `#'`"
  ([]
   (new-routes #'service/routes))
  ([routes]
   (->Routes routes)))
