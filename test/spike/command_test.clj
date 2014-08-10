(ns spike.command-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [spike.command :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(defn remove-first-word
  [word-string]
  (string/join " " (rest (string/split word-string #" " 2))))

(defspec test-text->command-text
  100
  (let [tuple-of-strings-gen (gen/such-that (comp #(>= (count %) 2) not-empty)
                                            (gen/vector (gen/not-empty gen/string-alpha-numeric)))]
    (prop/for-all [v (gen/fmap #(vector (first %) (string/join " " %)) tuple-of-strings-gen)]
      (= (apply text->command-text v) (remove-first-word (second v))))))
