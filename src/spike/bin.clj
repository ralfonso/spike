(ns spike.bin
  (:require [ring.adapter.jetty :as jetty]
            [spike.server :as server]))

(defn -main
  [& argv]
  (let [port (try (Integer/parseInt (first argv)) (catch NumberFormatException e 3002))]
    (server/init)
    (jetty/run-jetty server/handler {:port port})))
