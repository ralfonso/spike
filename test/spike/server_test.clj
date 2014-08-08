(ns spike.server-test
  (:require [clojure.test :refer :all]
            [spike.server :refer :all]))

(deftest test-text->arguments
  (is (nil? (text->arguments nil nil)))
  (is (nil? (text->arguments "" nil)))
  (is (nil? (text->arguments nil "")))
  (is (nil? (text->arguments "" "")))
  (is (= "test" (text->arguments "a" "a test")))
  (is (= "b test" (text->arguments "a" "b test")))
  (is (= "test" (text->arguments "!spike" "!spike test"))))
