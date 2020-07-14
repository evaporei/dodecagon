(ns dodecagon.server
  (:gen-class) ; for -main method in uberjar
  (:require [dodecagon.components :as components]))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (components/create-and-start-system! :base))
