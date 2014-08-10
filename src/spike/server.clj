(ns spike.server
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [polaris.core :refer :all]
            [ring.middleware
              [reload :refer [wrap-reload]]
              [params :refer [wrap-params]]
              [keyword-params :refer [wrap-keyword-params]]]
            [ring.util.response :refer [response]]
            [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]]
            [spike.command :as command]
            [spike.commands
              [gis :as gis]
              [google :as google]
              [ping :as ping]]
            [spike.config :as config]))

(defn webhook
  [request]
  (let [params (:params request)
        dispatcher (:dispatcher request)]
    (if-let [command-response (dispatcher params)]
      {:status 200 :body (json/generate-string command-response)}
      {:status 400 :body "unable to execute requested command"})))

(defn index
  [request]
  (response "Just a humble bot, ma'am"))

(def routes
  [["/" :index index]
   ["/webhook" :webhook webhook]])

(declare handler)

(defn init
  []

  ;; FIXME: find a better way to initialize the commands
  (let [commands [(gis/gis) (ping/ping) (google/google)]
        command-dispatcher (command/dispatcher commands)]

    (def handler
      (-> (router (build-routes routes))
          ((fn [handler]
            (fn [request]
              (handler (assoc request :dispatcher command-dispatcher)))))
          (wrap-keyword-params)
          (wrap-params)
          (wrap-reload)))))
