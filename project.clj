(defproject labs-4-cource "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0"]
                 [net.mikera/core.matrix "0.61.0"]
                 [com.taoensso/timbre "4.10.0"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.13"]]

  :min-lein-version "2.5.0"

  :clean-targets ^{:protect false}


  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :resource-paths ["public"]
  :source-paths ["src/cljc" "test"]

  :figwheel {:http-server-root "."
             :nrepl-port 7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs ["public/css"]}

  :cljsbuild {:builds {:app
                       {:source-paths ["src/cljc" "src/cljs" "env/dev/cljs"]
                        :compiler
                        {:main "labs-4-cource.dev"
                         :output-to "public/js/app.js"
                         :output-dir "public/js/out"
                         :asset-path   "js/out"
                         :source-map true
                         :optimizations :none
                         :pretty-print  true
                         :foreign-libs [{:file "public/libs/events.js"
                                         :provides ["canvas.events"]
                                         :module-type :commonjs}]}
                        :figwheel
                        {:on-jsload "labs-4-cource.core/init!"
                         :open-urls ["http://localhost:3449/index.html"]}}
                       :release
                       {:source-paths ["src/cljc" "src/cljs" "env/dev/cljs"]
                        :compiler
                        {:output-to "public/js/app.js"
                         :output-dir "public/js/release"
                         :asset-path   "js/out"
                         :optimizations :advanced
                         :pretty-print false
                         :foreign-libs [{:file "public/libs/events.js"
                                         :provides ["canvas.events"]
                                         :module-type :commonjs}]}}}}

  :aliases {"package" ["do" "clean" ["cljsbuild" "once" "release"]]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [com.cemerick/piggieback "0.2.2"]]}})
