(ns fn-fx-ui.javafx-init
  (:require [fn-fx-ui.core :as core])
  (:gen-class
   :extends javafx.application.Application))

(defn -start [app stage]
  (core/start {:root-stage? false}))

(defn -main [& args]
  (javafx.application.Application/launch fn_fx_ui.javafx_init (into-array String args)))
