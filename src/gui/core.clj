(ns gui.core
  (:use [seesaw core])
  (:require [database.core :as database])
  (:require [validate.core :as validate])
  (:require [clojure.string :as str])
  (:gen-class))

(declare add-course-frame)
(declare add-student-frame)
(def f (frame :minimum-size [640 :by 480]))


(defn main-screen-frame []
    (flow-panel
      :items [(button :id :goto-add-course
                      :text "Add Course"
                      :font {:size 20}
                      :listen [:action (fn [e] (config! f :title "Add Course" :content (add-course-frame)))])]))


(defn handler-add-course [event]
    (let [data (value (select f [:#add-course-form]))
          course-name (str/trim (:course-name data))
          start-date  (:start-date data)
          duration    (:duration data)]
     (cond
        (or (empty? course-name) (empty? start-date) (empty? duration)) (alert "Please enter all the fields")
        (not (validate/date-validator start-date)) (alert "Please enter date in the correct format")
        (not (validate/duration-validator duration)) (alert "Please enter correct duration")
        (database/course-exists? course-name) (alert (str "Course with name " course-name " already exists"))
        :else (do (database/add-course course-name start-date duration)
                  (alert "Course added successfully")
                  (config! f :title "Vipassana" :content (main-screen-frame))))))


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
                              (label :text "Duration:" :font {:size 20})
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
                 :content (main-screen-frame))
    show!))
