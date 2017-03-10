(ns gui.core
  (:use [seesaw core])
  (:require [database.core :as database])
  (:require [validate.core :as validate])
  (:require [clojure.string :as str])
  (:gen-class))

(declare add-course-frame)
(declare add-student-frame)

(def f (frame :size [800 :by 600] :resizable? false))


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
                                          (vec (reverse (sort-by :date (database/course-list))))]))
          :south (flow-panel :items [(button 
                                        :text "Select Course" 
                                        :font {:size 20})]))) 


(defn handler-add-course [event]
    (let [data (value (select f [:#add-course-form]))
          course-name (str/trim (:course-name data))
          start-date  (:start-date data)
          duration    (:duration data)]
     (cond
        (validate/is-empty? course-name start-date duration) (alert "Please enter all the fields")
        (not (validate/date-validator start-date)) (alert "Please enter date in the correct format")
        (not (validate/duration-validator duration)) (alert "Please enter correct duration")
        (database/course-exists? course-name) (alert (str "Course with name " course-name " already exists"))
        :else (do (database/add-course course-name start-date duration)
                  (alert "Course added successfully")
                  (config! f :title "Vipassana" 
                             :content (view-courses-frame))))))


(defn add-course-frame []
    (border-panel :vgap 150 :hgap 150
        :north " "
        :west  " "
        :east  " "
        :center (vertical-panel 
                      :id :add-course-form
                      :items [(label :text "Course Name:" :font {:size 20}) 
                              (text :id :course-name :font {:size 20})
                              " "
                              (label :text "Start Date (YYYY-MM-DD):" :font {:size 20})
                              (text :id :start-date :font {:size 20})  
                              " "
                              (label :text "Duration (days):" :font {:size 20})
                              (text :id :duration :font {:size 20})
                              " "
                              (button :id :add-course 
                                      :text "Add Course"
                                      :halign :center 
                                      :font {:size 20} 
                                      :listen [:action handler-add-course])])))


(defn -main []
  (database/main)
  (-> (config! f :title "Vipassana"
                 :content (view-courses-frame))
    show!))
