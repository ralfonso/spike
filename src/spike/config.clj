(ns spike.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [spike.command Command]))

(def core (atom {:commands {}}))

(defn command
  [identifier constructor]
  (println identifier)
  (when (not (fn? constructor))
    (throw (IllegalArgumentException. "command constructor must be no-arg function")))
  (let [command (constructor)]
    (when (not (instance? Command constructor))
      (throw (IllegalArgumentException. "command constructor must return an implementation of Command")))

    (swap! core update-in [:commands] assoc identifier command)))

(defn configure!
  [& args]
  (doseq [config-file args]
    (load config-file)))
