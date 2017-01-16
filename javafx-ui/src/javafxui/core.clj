(ns javafxui.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io  :as io])
  (:import [javafx.application Application]
           [javafx.scene Scene]
           [javafx.scene.control Button]
           [javafx.stage FileChooser])
  (:gen-class :extends javafx.application.Application))

(defonce runtime-state (atom {:csv-file nil
                              :data nil}))

(defn watch-fn [key reference old-state new-state]
  (println "-- Runtime State Changed from:")
  (println old-state)
  (println "-- to:")
  (println new-state))

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
        scene (Scene. button)]
    (add-watch runtime-state :main-watcher watch-fn)
    (doto button
      (.setOnAction (event-handler show-file-dialog)))
    (doto stage
      (.setTitle "JavaFX UI Spike")
      (.setScene scene)
      (.show))))

(defn -main
  [& args]
  (Application/launch javafxui.core (into-array String "")))
