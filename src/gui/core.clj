(ns gui.core
  (:use [seesaw core table])
  (:require [database.core :as database])
  (:require [validate.core :as validate])
  (:require [clojure.string :as str])
  (:gen-class))

(declare add-course-frame)
(declare view-courses-frame)

(def f (frame :minimum-size [640 :by 480]))


(defn view-courses-frame
  []
  (border-panel :vgap 5 :border 5
          :north (flow-panel
                :items [(button :text "Add Course"
                                :font {:size 20} 
                                :listen [:action (fn [e] (config! f :title "Add Course" 
                                                                    :content (add-course-frame)))])])
          :center (scrollable (table
                                :id :all-courses
                                :selection-mode :single
                                :font {:size 16}
                                :model [:columns
                                          [{:key :name, :text "Course Name"}
                                           {:key :date, :text "Start Date"}
                                           {:key :duration, :text "Duration"}]
                                        :rows
                                          (vec (reverse (sort-by :date (database/course-list))))]))))


(defn -main []
  (database/main)
  (-> (config! f :content (view-courses-frame))
    show!))