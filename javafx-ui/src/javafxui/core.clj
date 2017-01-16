(ns javafxui.core
  (:import [javafx.application Application]
           [javafx.scene Scene]
           [javafx.scene.control Button]
           [javafx.stage FileChooser])
  (:gen-class :extends javafx.application.Application))


(defn event-handler [f]
  (reify javafx.event.EventHandler
    (handle [this event] (f event))))

(defn show-file-dialog [event]
  (let [window (-> event
                   .getTarget
                   .getScene
                   .getWindow)]
    (doto (FileChooser.)
      (.setTitle "Import CSV")
      (.showOpenDialog window))))

(defn -start [app stage]
  (let [button (Button. "Import CSV")
        scene (Scene. button)]
    (doto button
      (.setOnAction (event-handler show-file-dialog)))
    (doto stage
      (.setTitle "JavaFX UI Spike")
      (.setScene scene)
      (.show))))

(defn -main
  [& args]
  (Application/launch javafxui.core (into-array String "")))
