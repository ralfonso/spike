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
            [spike.command :refer [defcommands]]
            [spike.commands
              [gis :as gis]
              [google :as google]
              [ping :as ping]]
            [spike.config :as config]))

(defn text->arguments
  [trigger-word text]
  (when (not-any? empty? [trigger-word text])
    (string/replace text (re-pattern (str "^" trigger-word "\\W+")) "")))

;;;TODO: this needs improvement, ahehn
(def conf {})

(defcommands dispatcher
  conf
  [[#"^gis (.+)" :gis gis/gis]
   [#"^google (.+)" :google google/google]
   [#"^ping$" :ping ping/ping]])

(defn webhook
  [request]
  (let [params (:params request)
        trigger-word (:trigger_word params)
        text (:text params)]
    (if-let [arguments (text->arguments trigger-word text)]
      (if-let [command-response (dispatcher params arguments)]
        {:status 200 :body (json/generate-string command-response)}
        {:status 400 :body "unable to execute requested command"})
      (response "no"))))

(defn index
  [request]
  (response "Just a humble bot, ma'am"))

(def routes
  [["/" :index index]
   ["/webhook" :webhook webhook]])

(def handler
  (-> (router (build-routes routes))
      (wrap-keyword-params)
      (wrap-params)
      (wrap-reload)))
