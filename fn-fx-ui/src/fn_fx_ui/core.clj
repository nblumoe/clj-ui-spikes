(ns fn-fx-ui.core
  (:require
   [fn-fx.fx-dom :as fx-dom]
   [fn-fx.diff :refer [component defui render should-update?]]
   [fn-fx.controls :as controls]
   [fn-fx.util :as util]
   #_[fn-fx.render-core :as render-core]
   [clojure.data.csv :as csv]
   [clojure.java.io :as io])
  (:import (javafx.application Application)
           (javafx.stage FileChooser)
           (javafx.scene Scene))
  (:gen-class :extends javafx.application.Application))

(defui Stage
  (render [this args]
          (controls/stage
           :title "fn-fx-ui"
           :shown true
           :min-width 200
           :min-height 200
           :scene (controls/scene
                   :root (controls/v-box
                          :children [(controls/button
                                      :text "Import CSV"
                                      :on-action {:event :import-csv
                                                  :fn-fx/include {:fn-fx/event #{:target}}})])))))

(defmulti handle-event (fn [_ {:keys [event]}]
                         event))

(defmethod handle-event :import-csv
  [state {:keys [fn-fx/includes]}]
  (let [window (.getWindow (.getScene (:target (:fn-fx/event includes))))
        dialog (doto (FileChooser.) (.setTitle "Import CSV"))
        file (util/run-and-wait (.showOpenDialog dialog window))
        data (with-open [reader (io/reader file)]
               (doall (csv/read-csv reader)))]
    (assoc state :file file :data data)))

(defn -main
  [& args]
  (let [data-state (atom {:csv nil})
        handler-fn (fn [event]
                     (println event)
                     (try
                       (swap! data-state handle-event event)
                       (catch Throwable exception
                         (println exception))))
        ui-state (agent (fx-dom/app (stage @data-state) handler-fn))]

    (add-watch data-state :ui (fn [_ _ _ _]
                                (send ui-state
                                      (fn [old-ui]
                                        (println "-- State Updated --")
                                        (println @data-state)
                                        (fx-dom/update-app old-ui (stage @data-state))
                                        ))))))

(comment
  #_(def window
      (.getWindow ((fn-fx.render-core/get-getter (type @(:root @us)) :scene) @(:root @us)))      )
  #_(.launch fn_fx_ui.core)

  (let [st (stage @data-state)]
    #_(doto (FileChooser.)
        (.setTitle "fiii")
        (.showOpenDialog @(:root old-ui)))
    )

  #_(doto (FileChooser.)
      (.setTitle "fiii")
      (.showOpenDialog (.getWindow (.getScene (:target (:fn-fx/event includes))))))
  #_(-> (javafx.stage.FileChooser.)
        (.setTitle "Foo Bar")
        (.showOpenDialog stage)
        )
  #_(-> (controls/file-chooser
         :title "Import CSV"
         )
        (.showOpenDialog)
        )

  )
