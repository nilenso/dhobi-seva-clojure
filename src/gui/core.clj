(ns gui.core
  (:use [seesaw core])
  (:require [database.core :as database])
  (:require [validate.core :as validate])
  (:gen-class))

(declare add-course-frame)
(declare add-student-frame)

(def f (frame :minimum-size [640 :by 480]))


(defn main-screen-frame []
    (config! f :title "Vipasana"
               :content (flow-panel
                            :items [(button :id :goto-add-course
                                             :text "Add Course"
                                             :font {:size 20}
                                             :listen [:action (fn [e] (config! f :title "Add Course" :content (add-course-frame)))])])))


(defn handler-add-course [event]
    (let [data (value (select f [:#form]))
          course-name (:course-name data)
          start-date  (:start-date data)
          duration    (:duration data)]
     (cond
        (or (empty? course-name) (empty? start-date) (empty? duration)) (alert "Please enter all the fields")
        (not (validate/date-validator start-date)) (alert "Please enter date in the correct format")
        (not (validate/duration-validator duration)) (alert "Please enter correct duration")
        :else (do (database/add-Course course-name start-date duration)
                  (config! f :title "Add Student" :content (add-student-frame course-name))))))


(defn add-course-frame []
    (form-panel :id :form
      :items [
        [nil :fill :both :insets (java.awt.Insets. 5 5 5 5) :gridx 0 :gridy 0]

        [(label :text "Course Name:" :halign :center :font {:size 20})]

        [(text :columns 20 :id :course-name :font {:size 20}) :grid :next]

        [(label :text "Start Date (YYYY-MM-DD):" :halign :center :font {:size 20}) :gridheight 1 :grid :wrap]

        [(text :columns 20 :id :start-date :font {:size 20}) :grid :next :weightx 1.0]

        [(label :text "Duration:" :halign :center :font {:size 20}) :gridheight 1 :grid :wrap]

        [(text :columns 20 :id :duration :font {:size 20}) :grid :next :weightx 1.0]

        [[1 :by 1] :grid :wrap]

        [(button :id :add-course :text "Add Course" :font {:size 20} :listen [:action handler-add-course]) :grid :next :weightx 1.0]]))


(defn handler-add-student [course-name]
    (let [data (value (select f [:#form]))
          student-name (:student-name data)
          room         (:room data)
          seat         (:seat data)]
          (cond 
            (or (empty? student-name) (empty? room) (empty? seat)) (alert "Please enter all the fields")
            :else (do (database/add-Student course-name student-name room seat)
                  (alert "Student added successfully")
                  (config! f :title "Add Student" :content (add-student-frame course-name))))))


(defn add-student-frame [course-name]
  (form-panel :id :form
      :items [
        [nil :fill :both :insets (java.awt.Insets. 5 5 5 5) :gridx 0 :gridy 0]

        [(label :text "Course Name: " :halign :center :font {:size 20}) :grid :wrap]

        [(label :text course-name :halign :left :font {:size 20}) :grid :next]

        [[1 :by 1] :grid :wrap]

        [(label :text "Name:" :halign :center :font {:size 20})]

        [(text :columns 20 :id :student-name :font {:size 20}) :grid :next]

        [(label :text "Room:" :halign :center :font {:size 20}) :gridheight 1 :grid :wrap]

        [(text :columns 20 :id :room :font {:size 20}) :grid :next :weightx 1.0]

        [(label :text "Seat:" :halign :center :font {:size 20}) :gridheight 1 :grid :wrap]

        [(text :columns 20 :id :seat :font {:size 20}) :grid :next :weightx 1.0]
        
        [[1 :by 1] :grid :wrap]

        [(button :id :add-student :text "Add Student" :font {:size 20} 
                                  :listen [:action (fn [e] (handler-add-student course-name))]) :grid :next :weightx 1.0]]))


(defn -main []
  (database/main)
  (-> (main-screen-frame)
    show!))