(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
            [spike.server :as server]
            [ring.adapter.jetty :as jetty]))

(def jetty-server (atom nil))

(defn init
  []
  (server/init))

(defn start
  []
  (reset! jetty-server (jetty/run-jetty server/handler {:port 3001 :join? false})))

(defn stop
  []
  (.stop (deref jetty-server)))

(defn go
  []
  (init)
  (start))

(defn reload
  []
  (when (deref jetty-server)
    (stop))
  (set-refresh-dirs "./src" "./dev")
  (refresh :after 'user/go))
