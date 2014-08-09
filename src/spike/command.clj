(ns spike.command
  (:require [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]]
            [spike.config :as config]))

(defn match
  [patterns text]
  (first (filter (comp not nil?) (map #(re-find % text) patterns))))

(defn compile-command
  [{identifier :identifier patterns :patterns handler :handler :as command}]
  (let [patterns (if (coll? patterns) patterns [patterns])]
    (fn [config params text]
      (when-let [match (match patterns text)]
        (let [arguments (if (string? match) match (second match))]
          (debugf "dispatching text to command: %s" (name identifier))
          (binding [config/*conf* config]
            (handler params arguments)))))))

(defn command-router
  [conf compiled-commands]
  (fn [params text]
    (some #(% conf params text) compiled-commands)))

(defn command->map
  [v]
  (assoc (apply hash-map (rest v)) :identifier (first v)))

; inspired by compojure's defroutes macro
(defmacro defcommands
  [name conf & commands]
  `(def ~name (command-router ~conf (map (comp compile-command command->map) ~@commands))))
