(ns app.renderer
  (:require [reagent.core :as r]))

(def electron (js/require "electron"))
(def remote (.-remote electron))
(def dialog (.-dialog remote))

(defn get-by-id [id]
  (.getElementById js/document (name id)))

(defn import-csv []
  (.showOpenDialog dialog))

;; Components

(defn import-button []
  [:button#import-csv.btn.btn-default
   [:span.icon.icon-folder.icon-text]
   "Import CSV"])

(defn headers-checkbox []
  [:label
   [:input#first-row-headers {:type :checkbox}]
   "Import first row as headers"])

(defn toolbar []
  [:header.toolbar.toolbar-header
   [:div.toolbar-actions
    [import-button]
    [headers-checkbox]]])

(defn table [data]
  (let [headers (first data)
        rows    (rest data)]
    [:table.table-striped
     [:thead [:tr (for [header headers] [:th header])]]
     [:tbody
      (for [row rows]
        [:tr (for [cell row] [:td cell])])]]))

(defn window-content [data]
  [:div.window
   [toolbar]
   [:div.window-content
    [table data]]])

(defn init []
  (js/console.log "Starting Application")
  (let [data [["x1" "x2"]
              [1 2]
              [5 6]
              [3 4]]]
    #_(.addEventListener (get-by-id :import-csv) "click" import-csv)
    (r/render-component [window-content data]
                        (get-by-id :app-root))))
