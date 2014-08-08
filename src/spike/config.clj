(ns spike.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def ^:dynamic *conf*)

(defn configure
  [config-file]
  (with-open [rdr (-> (io/reader config-file)
                      java.io.PushbackReader.)]
    (edn/read rdr)))
