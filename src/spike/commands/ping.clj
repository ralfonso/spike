(ns spike.commands.ping
  (:require [clj-http.client :as client]
            [spike.slack :as slack]))

(defn ping
  [params text]
  (let [channel-name (str "#" (:channel_name params))]
    (slack/post-message {:channel channel-name :text "PONG!"})))
