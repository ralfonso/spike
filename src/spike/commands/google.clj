(ns spike.commands.google
  (:require [clj-http.client :as client]
            [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]]))

(defn google
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
