(ns spike.command
  (:require [taoensso.timbre :as timbre :refer [infof errorf debugf tracef]]
            [spike.config :as config]))

(defn match
  [patterns text]
  (first (filter (comp not nil?) (map #(re-find % text) patterns))))

(defn compile-command
  [[patterns identifier func :as command]]
  (let [patterns (if (vector? patterns) patterns [patterns])]
    (fn [config params text]
      (when-let [match (match patterns text)]
        (let [arguments (if (string? match) match (second match))]
          (do
            (debugf "dispatching text to command: %s" (name identifier))
            (binding [config/*conf* config]
              (func params arguments))
              identifier))))))

(defn command-router
  [conf compiled-commands]
  (fn [params text]
    (some #(% conf params text) compiled-commands)))

; inspired by compojure's defroutes macro
(defmacro defcommands
  [name conf & commands]
  `(def ~name (command-router ~conf (map compile-command ~@commands))))
