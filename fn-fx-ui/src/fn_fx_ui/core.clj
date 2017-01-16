(ns fn-fx-ui.core
  (:require
   [fn-fx.fx-dom :as fx-dom]
   [fn-fx.controls :as controls]
   [fn-fx.render-core :as render-core]
   [fn-fx.diff :refer [component defui render should-update?]])
  (:import (javafx.application Application)
           (javafx.stage FileChooser)
           (javafx.scene Scene)
           )
  (:gen-class :extends javafx.application.Application))

(def scene (controls/scene
            :root (controls/stack-pane
                   :children [(controls/button
                               :text "Import CSV"
                               :on-action {:event :import-csv
                                           :fn-fx/include {:fn-fx/event #{:target}}
                                           })])))

(defui Stage
  (render [this args]
          (controls/stage
           :title "fn-fx-ui"
           :shown true
           :min-width 200
           :min-height 200
           :scene scene)))

(defmulti handle-event (fn [_ {:keys [event]}]
                         event))

(defmethod handle-event :import-csv
  [state {:keys [fn-fx/includes]}]
  (println "Importing CSV")
  (doto (FileChooser.)
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


(defn -main
  [& args]
  (let [data-state (atom {:csv nil})
        handler-fn (fn [event]
                     (println event)
                     (try
                       (swap! data-state handle-event event)
                       (catch Throwable exception
                         (println exception))))
        ui-state (agent (fx-dom/app (stage @data-state) handler-fn))
        ]

    #_(def window
        (.getWindow ((fn-fx.render-core/get-getter (type @(:root @us)) :scene) @(:root @us)))      )
    (.launch fn_fx_ui.core)
    (add-watch data-state :ui (fn [_ _ _ _]
                                (send ui-state
                                      (fn [old-ui]
                                        (let [st (stage @data-state)]
                                          #_(doto (FileChooser.)
                                              (.setTitle "fiii")
                                              (.showOpenDialog @(:root old-ui)))
                                          (fx-dom/update-app old-ui (stage @data-state)))))))))
