(defproject cljs-boids "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1806"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :source-paths["src/clj"]
  :cljsbuild {
    :builds [{:id "cljs-boids"
              :source-paths ["src/cljs"]
              :compiler {
                  :output-to "cljs_boids.js"
                  :output-dir "out"
                  :optimizations :none
                  :source-map true}}]})
