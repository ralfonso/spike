(ns spike.commands.gis
  (:require [clj-http.client :as client]
            [spike.slack :as slack]
            [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]]))

(defn gis
  [params arguments]
  (let [response (client/get "http://ajax.googleapis.com/ajax/services/search/images"
                             {:query-params {:rsz "8" :v "1.0" :q arguments}
                              :as :json})]
    (when (= (:status response) 200)
      (when-let [first-image (-> response :body :responseData :results first :url)]
        (let [at-name (str "@" (:user_name params))
              channel-name (str "#" (:channel_name params))
              text (str at-name ": " first-image)]
          {:text text})))))
