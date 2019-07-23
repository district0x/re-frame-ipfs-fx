(defproject district0x/re-frame-ipfs-fx "1.1.0-SNAPSHOT"
  :description "library for calling ipfs HTTP API"
  :url "https://github.com/district0x/cljs-ipfs-http-client"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[district0x/cljs-ipfs-http-client "1.0.1"]
                 [org.clojure/clojurescript "1.10.439"]
                 [re-frame "0.10.8"]]

  :doo {:build "tests"
        :paths {:karma "./node_modules/karma/bin/karma"}}

  :npm {:devDependencies [karma "2.0.0"
                          karma-es6-shim "1.0.0"
                          karma-chrome-launcher "3.0.0"
                          karma-phantomjs-launcher "1.0.4"
                          karma-slimerjs-launcher "1.1.0"
                          karma-cli "2.0.0"
                          karma-cljs-test "0.1.0"]}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]
                                  [day8.re-frame/test "0.1.5"]]
                   :plugins [[lein-cljsbuild "1.1.7"]
                             [lein-doo "0.1.11"]
                             [lein-npm "0.6.2"]]}}

  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["deploy"]]

  :cljsbuild {:builds [{:id "tests"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "tests-output/tests.js"
                                   :output-dir "tests-output"
                                   :main "tests.runner"
                                   :optimizations :none
                                   :external-config {:devtools/config {:features-to-install :all}}}}]})
