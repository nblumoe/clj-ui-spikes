(ns fn-fx-ui.javafx-init
  ;; requiring this prevents the java process from quitting when the last window is closed
  ;; caused by pullin in the fn-fx library, probably because that creates panels before
  ;; the application was started, without creating the panels compilation fails due to
  ;; uninitialized toolkit though
  ;; - moving (JFXPanel.) to aaa.clj in this project did not help
  (:require [fn-fx-ui.core :as core])
  (:gen-class
   :extends javafx.application.Application))

(defn -start [app stage]
  (let [force-exit (reify javafx.event.EventHandler
                     (handle [this event]
                       (println "Closing application")
                       (javafx.application.Platform/exit)))]
    (.setOnCloseRequest stage force-exit)
    (core/start)))

(defn -main [& args]
  (javafx.application.Application/launch fn_fx_ui.javafx_init (into-array String args)))
