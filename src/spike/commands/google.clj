(ns spike.commands.google
  (:require [clj-http.client :as client]
            [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]])
  (:import  [spike.command Command]))

(defn execute
  [params arguments]
  (let [response (client/get "http://ajax.googleapis.com/ajax/services/search/web"
                             {:query-params {:rsz "8" :v "1.0" :q arguments}
                              :as :json})]
    (when (= (:status response) 200)
      (when-let [first-result (-> response :body :responseData :results first :url)]
        (let [at-name (str "@" (:user_name params))
              google-url (str "https://www.google.com/#q=" arguments)
              text (str at-name ": " first-result " more: " google-url)]
          {:text text :parse "full"})))))

(defrecord Google [identifier pattern]
  Command
  (match [this command-text]
    (re-matches pattern command-text))

  (help [this]
    "Submits a search to Google Web Search and returns the first result.")

  (syntax [this]
    "google <search-params>")

  (exec [this request-params matches]
    (let [arguments (second matches)]
      (execute request-params arguments))))

(defn google
  []
  (Google. :google #"(?i)^google (.+)"))
