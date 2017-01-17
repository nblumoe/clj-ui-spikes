(ns app.renderer
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(def electron (js/require "electron"))
(def remote (.-remote electron))
(def dialog (.-dialog remote))
(def fs     (js/require "fs"))

(defn log [t]
  (.log js/console t))

(defn get-by-id [id]
  (.getElementById js/document (name id)))

(defn parse-csv [data]
  (map #(str/split % #",")
       (str/split data #"\n")))

(defn add-headers [data]
  (let [headers-indices (range (count (first data)))
        headers (map #(str "x" (inc %)) headers-indices)]
    (cons headers data)))

;; app state

(def initial-state
  {:options {:csv {:first-row-headers false}}
   :data nil})

(defonce app-state (r/atom initial-state))

;; event handlers

(defn import-csv []
  (let [data (->> (first (.showOpenDialog dialog))
                  (.readFileSync fs)
                  parse-csv)
        data-with-headers (if (get-in @app-state [:options :csv :first-row-headers])
                            data
                            (add-headers data))]
    (swap! app-state assoc :data data-with-headers)))

(defn toggle-first-row-headers []
  (swap! app-state update-in [:options :csv :first-row-headers] not))

;; Components

(defn import-button []
  [:button#import-csv.btn.btn-default {:on-click import-csv}
   [:span.icon.icon-folder.icon-text]
   "Import CSV"])

(defn headers-checkbox []
  [:label
   [:input#first-row-headers {:type :checkbox
                              :checked (get-in @app-state [:options :csv :first-row-headers])
                              :on-click toggle-first-row-headers}]
   "Import first row as headers"])

(defn toolbar []
  [:header.toolbar.toolbar-header
   [:div.toolbar-actions
    [import-button]
    [headers-checkbox]]])

(defn table []
  (let [data    (:data @app-state)
        headers (first data)
        rows    (rest data)]
    [:table.table-striped
     [:thead [:tr (for [header headers]
                    ^{:key (random-uuid)} [:th header])]]
     [:tbody
      (for [row rows]
        ^{:key (random-uuid)} [:tr (for [cell row]
                                     ^{:key (random-uuid)} [:td cell])])]]))

(defn window-content [data]
  [:div.window
   [toolbar]
   [:div.window-content
    [table]]])

(defn init []
  (js/console.log "Starting Application")
  (r/render-component [window-content]
                      (get-by-id :app-root)))
