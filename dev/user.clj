(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [spike.server :as server]
            [ring.adapter.jetty :as jetty]))

(def server (atom nil))

(defn init
  []
  ; placeholder)
  )

(defn start
  []
  (reset! server (jetty/run-jetty server/handler {:port 3001 :join? false})))

(defn stop
  []
  (.stop (deref server)))

(defn go
  []
  (init)
  (start))

(defn reload
  []
  (when (deref server)
    (stop))
  (refresh :after 'user/go))
