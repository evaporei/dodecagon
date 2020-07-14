(ns dodecagon.components.servlet
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as pedestal]))

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

;; TODO: maybe make variadic fn

(defn new-servlet []
  (->Servlet {}))
