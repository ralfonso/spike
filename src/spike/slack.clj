(ns spike.slack
  (:require [clj-http.client :as client]
            [spike.config :refer [*conf*]]
            [cheshire.core :as json]))

(defn post-message
  [payload]
  (let [payload (assoc payload :username "Spike" :icon_emoji ":spike:")]
    (client/post (:slack-webhook-url *conf*)
                 {:form-params {:payload (json/generate-string payload)}})))
