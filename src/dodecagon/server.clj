(ns dodecagon.server
  (:gen-class) ; for -main method in uberjar
  (:require [dodecagon.system :as system]))

;; TODO: add run-dev main

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (system/create-and-start-system! :base))
