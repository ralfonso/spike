(ns spike.commands.gis
  (:require [clj-http.client :as client]
            [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]])
  (:import  [spike.command Command]))

(defn execute
  [request-params query-params search-terms]
  (let [params {:rsz "8" :v "1.0"}
        params (merge params query-params)
        params (assoc params :q search-terms)
        response (client/get "http://ajax.googleapis.com/ajax/services/search/images"
                             {:query-params params
                              :as :json})]
    (when (= (:status response) 200)
      (when-let [first-image (-> response :body :responseData :results first :url)]
        (let [at-name (str "@" (:user_name request-params))
              text (str at-name ": " first-image)]
          {:text text})))))

(defrecord GIS [identifier pattern]
  Command
  (match [this command-text]
    (re-matches pattern command-text))

  (help [this]
    "Submits a search to Google Image Search and returns the first result.")

  (syntax [this]
    "gis <search-params>")

  (exec [this request-params matches]
    (let [invoker (second matches)
          imgtype (condp = invoker
                    "gif" "animated"
                    "animate" "animated"
                    nil)
          query-params (if imgtype {:imgtype imgtype} {})
          search-terms (last matches)]
      (execute request-params query-params search-terms))))

(defn gis
  []
  (GIS. :gis #"(?i)^(gis|image|gif) (.+)"))
