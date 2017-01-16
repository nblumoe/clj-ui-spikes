(ns bootfxui.core
  (:import [javafx.application Application]
           [javafx.scene Scene]
           [javafx.stage Stage]
           [javafx.scene.control Button]
           [javafx.scene.layout StackPane])
  (:gen-class :extends javafx.application.Application))

(defn -start [^bootfxui.core app ^Stage stage]
  (let [button (Button. "Import CSV")
        root (StackPane.)]
    (-> root
        .getChildren
        (.add button))
    (doto stage
      (.setTitle "JavaFX UI Spike")
      (.setScene (Scene. root 1000 1000))
      (.show))))

(defn -main
  [& args]
  (Application/launch bootfxui.core (into-array String "")))
