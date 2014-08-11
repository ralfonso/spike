(defproject spike "1.0.0"
  :description "a Slack Bot"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.9.2"]
                 [org.clojure/clojure "1.6.0"]
                 [ring "1.3.0"]
                 [cheshire "5.3.1"]
                 [slingshot "0.10.3"]
                 [com.taoensso/timbre "3.2.0"]
                 [polaris "0.0.4"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:init spike.server/init
         :handler spike.server/handler}
  :resource-paths ["resources" "app"]
  :profiles {:dev {:source-paths ["src" "dev"]
                   :resource-paths ["resources" "dev-resources"]
                   :dependencies [[cider/cider-nrepl "0.7.0-SNAPSHOT"]
                                  [org.clojure/test.check "0.5.9"]
                                  [org.clojure/tools.namespace "0.2.4"]]}}
             :repl-options {:init-ns user
                            :nrepl-middleware
                              [cider.nrepl.middleware.classpath/wrap-classpath
                               cider.nrepl.middleware.complete/wrap-complete
                               cider.nrepl.middleware.info/wrap-info
                               cider.nrepl.middleware.inspect/wrap-inspect
                               cider.nrepl.middleware.macroexpand/wrap-macroexpand
                               cider.nrepl.middleware.stacktrace/wrap-stacktrace]}
  :main spike.bin
  :uberjar-name "spike-standalone.jar"
  :min-lein-version "2.0.0")
