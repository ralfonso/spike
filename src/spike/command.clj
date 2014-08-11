(ns spike.command
  (:require [clojure.string :as string]
            [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]]))

(defprotocol Command
  (match [this command-text])
  (help [this])
  (syntax [this])
  (exec [this request-params matches]))

(defn text->command-text
  [trigger-word text]
  (when (not-any? empty? [trigger-word text])
    (string/replace text (re-pattern (str "^" trigger-word "\\W+")) "")))

;; FIXME: is there a way to do this inline rather than having this named fn?
(defn match-command
  [command-text command]
  (let [matches (.match command command-text)]
    (when matches
      [matches command])))

(defn dispatcher
  [commands]
  (fn [request-params]
    (let [trigger-word (:trigger_word request-params)
          text (:text request-params)
          command-text (text->command-text trigger-word text)]
      (when-let [[matches command] (some (partial match-command command-text) commands)]
        (.exec command request-params matches)))))
