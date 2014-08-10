(ns spike.commands.ping
  (:require [clj-http.client :as client])
  (:import  [spike.command Command]))

(defn execute
  [params arguments]
  (let [channel-name (str "#" (:channel_name params))]
    {:text "PONG!"}))

(defrecord Ping [identifier pattern]
  Command
  (match [this command-text]
    (re-matches pattern command-text))

  (help [this]
    "Replies with \"PONG\" to see if the bot is running")

  (syntax [this]
    "ping")

  (exec [this request-params matches]
    (let [arguments (second matches)]
      (execute request-params arguments))))

(defn ping
  []
  (Ping. :ping #"(?i)^ping$"))
