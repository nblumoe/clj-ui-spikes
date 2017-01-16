(ns javafxui.core
  (:import [javafx.application Application]
           [javafx.scene Scene]
           [javafx.scene.control Button]
           )
  (:gen-class :extends javafx.application.Application))

(defonce force-toolkit-init (javafx.embed.swing.JFXPanel.))

(defn- start [app stage]
  (let [button (.setTitle (Button.) "Import CSV")
        scene (Scene. button)]
    (doto stage
      (.setTitle "JavaFX UI Spike")
      (.setScene scene)
      (.show)
      ))
  )

(defn -main
  [& args]
  (Application/launch javafxui.core (into-array String "")))
