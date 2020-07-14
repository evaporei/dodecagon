(ns dodecagon.components.config
  (:require [com.stuartsierra.component :as component]))

(defrecord Config [config]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(def default-config
  {:environment :prod
   :dev-port    8080})

(defn new-config
  ([]
    (new-config default-config))
  ([config]
    (->Config config)))
