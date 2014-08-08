(ns spike.command-test
  (:require [clojure.test :refer :all]
            [spike.command :refer :all]
            [spike.config :refer [*conf*]]))

(deftest test-match
  (testing "success"
    (testing "single pattern"
      (is (match [#"^hey"] "hey you guise")))
    (testing "multiple pattern"
      (is (match [#"^hey" #"^hey you"] "hey you guise"))))
  (testing "failure"
    (testing "single pattern"
      (is (not (match [#"^hey"] "no you guise"))))
    (testing "multiple pattern"
      (is (not (match [#"^hey" #"^hey you"] "no you guise"))))))

(deftest test-compile-command
  (let [check (promise)
        command-fn (fn [params text] (deliver check true))]
    (testing "success"
      (let [compiled-command (compile-command [#"deliver" :check command-fn])]
        (compiled-command {} {} "deliver me from PHP")
        (is (realized? check))))
    (testing "failure"
      (let [check (promise)
            compiled-command (compile-command [#"deliver" :check command-fn])]
        (compiled-command {} {} "save me from PHP")
        (is (not (realized? check)))))))

(deftest test-command-router
  (let [deliver-check (atom nil)
        deliver-fn (fn [params text] (reset! deliver-check true))
        deliver-spec [#"^deliver" :check deliver-fn]

        save-check (atom nil)
        save-fn (fn [params text] (reset! save-check true))
        save-spec [#"^save" :save save-fn]

        commands (conj [] deliver-spec save-spec)

        router (command-router {} (map compile-command commands))

        reset-atoms! (fn [] (reset! deliver-check nil) (reset! save-check nil))]

    (testing "succesful routing"
      (router {} "deliver me from PHP")
      (is (deref deliver-check))
      (is (not (deref save-check)))

      (reset-atoms!)

      (router {} "save me from PHP")
      (is (not (deref deliver-check)))
      (is (deref save-check)))

    (reset-atoms!)

    (testing "routing failure"
      (router {} "extricate me from PHP")
      (is (not (deref deliver-check)))
      (is (not (deref save-check))))))

(deftest test-macro
  (testing "simple dispatcher"
    (let [deliver-check (atom nil)
          deliver-fn (fn [params text] (reset! deliver-check true))
          deliver-spec [#"^deliver" :check deliver-fn]]

      (defcommands dispatcher {}
        [deliver-spec])

      (dispatcher {} "deliver me from JBoss")
      (is (deref deliver-check))

      (reset! deliver-check nil)

      (dispatcher {} "save me from JBoss")
      (is (not (deref deliver-check)))))

  (testing "config binding"
    (let [check (atom nil)
          func (fn [params text] (reset! check (:test-conf *conf*)))
          spec [#"^deliver" :fun func]]

      (defcommands dispatcher {:test-conf 123}
        [spec])

      (dispatcher {} "deliver fun")
      (is (= (deref check) 123))))

  (testing "command arguments"
    (let [check (atom nil)
          func (fn [params arguments] (reset! check arguments))
          spec [#"^deliver (.+)" :fun func]]

      (defcommands dispatcher {}
        [spec])

      (dispatcher {} "deliver fun")
      (is (= (deref check) "fun")))))
