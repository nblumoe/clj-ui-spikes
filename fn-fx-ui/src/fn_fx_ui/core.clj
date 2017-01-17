(ns fn-fx-ui.core
  (:require
   [fn-fx.fx-dom :as fx-dom]
   [fn-fx.diff :refer [component defui render should-update?]]
   [fn-fx.controls :as controls]
   [fn-fx.util :as util]
   [fn-fx.diff :as diff]
   #_[fn-fx.render-core :as render-core]
   [clojure.data.csv :as csv]
   [clojure.java.io :as io])
  (:import (javafx.application Application)
           (javafx.stage FileChooser)
           (javafx.scene Scene)
           (javafx.scene.chart.XYChart)
           (javafx.beans.property ReadOnlyObjectWrapper))
  (:gen-class :extends javafx.application.Application))

(defn cell-value-factory [f]
  (reify javafx.util.Callback
    (call [this entity]
      (ReadOnlyObjectWrapper. (f (.getValue entity))))))

(defn table-column [index name]
  (controls/table-column
   :text name
   :cell-value-factory (cell-value-factory #(nth % index))))

(defn data->series [data]
  (if (some? data)
    (let [transpose #(apply mapv vector %)
          transposed-data (transpose data)
          xs (rest (first transposed-data))
          build-series (fn [[name & ys]]
                         (let [series (javafx.scene.chart.XYChart$Series.)]
                           (.setName series name)
                           (doseq [[x y] (transpose [xs ys])]
                             (.add (.getData series)
                                   (javafx.scene.chart.XYChart$Data. (bigdec x) (bigdec y))))
                           series))]
      (map build-series transposed-data))
    []))

(defui Stage
  (render [this {:keys [data options] :as state}]
          (controls/stage
           :title "fn-fx-ui"
           :shown true
           :scene (controls/scene
                   :root (controls/border-pane
                          :top (controls/h-box
                                :padding (javafx.geometry.Insets. 15 12 15 12)
                                :spacing 10
                                :alignment (javafx.geometry.Pos/CENTER)
                                :children [(controls/button
                                            :text "Import CSV"
                                            :on-action {:event :import-csv
                                                        :fn-fx/include {:fn-fx/event #{:target}}})
                                           (controls/check-box
                                            :text "Import first row as headers"
                                            :selected (get-in options [:csv :first-row-headers])
                                            :on-action {:event :toggle-option
                                                        :path [:csv :first-row-headers]})
                                           (controls/button
                                            :text "Reset"
                                            :on-action {:event :reset})])
                          :center (controls/v-box
                                   :children [(controls/table-view
                                               :columns (map-indexed table-column (first data))
                                               :items (rest data)
                                               :placeholder (controls/label
                                                             :text "Import some data first"))
                                              (diff/component [:javafx.scene.chart.ScatterChart
                                                               []
                                                               [(javafx.scene.chart.NumberAxis.)
                                                                (javafx.scene.chart.NumberAxis.)]]
                                                              {:data (data->series data)})]))))))
(defmulti handle-event (fn [_ {:keys [event]}]
                         event))

(def initial-state
  {:options {:csv {:first-row-headers false}}
   :data nil})

(defonce data-state (atom initial-state))

(defmethod handle-event :reset
  [_ _]
  initial-state)

(defmethod handle-event :toggle-option
  [state {:keys [path]}]
  (update-in state (cons :options path) not))

(defmethod handle-event :import-csv
  [{:keys [options] :as state} {:keys [fn-fx/includes]}]
  (let [window (.getWindow (.getScene (:target (:fn-fx/event includes))))
        dialog (doto (FileChooser.) (.setTitle "Import CSV"))
        file (util/run-and-wait (.showOpenDialog dialog window))
        data (with-open [reader (io/reader file)]
               (doall (csv/read-csv reader)))]
    (assoc state :file file :data
           (if (get-in options [:csv :first-row-headers])
             data
             (cons (map #(str "x" (inc %)) (range (count (first data)))) data)))))

(defn -main
  [& args]
  (let [handler-fn (fn [event]
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

  )
