(defproject fn-fx-ui "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [halgari/fn-fx "0.3.0-SNAPSHOT"]
                 [org.clojure/data.csv "0.1.3"]]
  :main ^:skip-aot fn-fx-ui.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
