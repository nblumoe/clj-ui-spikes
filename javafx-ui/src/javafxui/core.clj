(ns javafxui.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io  :as io])
  (:import [javafx.application Application]
           [javafx.scene Scene]
           [javafx.scene.control Button TableView TableColumn]
           [javafx.stage FileChooser]
           [javafx.scene.layout VBox])
  (:gen-class :extends javafx.application.Application))

(defonce runtime-state (atom {:root nil
                              :csv-file nil
                              :data nil}))

(defn create-column [index name]
  (let [column (TableColumn. name)]
    #_(.setCellValueFactory column identity)
    column))

(defn render-table
  [data]
  (let [col-names (first data)
        table-columns (map-indexed create-column col-names)
        table-view (TableView.)]
    (doseq [col table-columns]
      (-> table-view
          .getColumns
          (.add col)))
    (-> (:root @runtime-state)
        .getChildren
        (.add table-view))))

(defn watch-fn [key reference old-state new-state]
  (println "-- Runtime State Changed from:")
  (println old-state)
  (println "-- to:")
  (println new-state)
  (when (:data new-state)
    (render-table (:data new-state))))

(defn event-handler [f]
  (reify javafx.event.EventHandler
    (handle [this event] (f event))))

(defn import-file! [file]
  (with-open [reader (io/reader file)]
    (swap! runtime-state assoc
           :csv-file file
           :data (csv/read-csv reader))))

(defn show-file-dialog [event]
  (let [window (-> event .getTarget .getScene .getWindow)
        dialog (doto (FileChooser.)
                 (.setTitle "Import CSV"))
        file (.showOpenDialog dialog window)]
    (import-file! file)))

(defn -start [app stage]
  (let [button (Button. "Import CSV")
        root (VBox.)
        scene (Scene. root 800 600)]
    (add-watch runtime-state :main-watcher watch-fn)
    (swap! runtime-state assoc :root root)
    (doto button
      (.setOnAction (event-handler show-file-dialog)))
    (-> root
        .getChildren
        (.add button))
    (doto stage
      (.setTitle "JavaFX UI Spike")
      (.setScene scene)
      (.show))))

(defn -main
  [& args]
  (Application/launch javafxui.core (into-array String "")))
