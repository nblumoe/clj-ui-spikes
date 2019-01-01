(defproject fn-fx-ui "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [fn-fx/fn-fx-javafx "0.5.0-SNAPSHOT"]
                 [org.clojure/data.csv "0.1.3"]]
  :main fn-fx-ui.javafx-init
  :aot [fn-fx-ui.javafx-init]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
