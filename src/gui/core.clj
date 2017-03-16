(ns gui.core
  (:use [seesaw core table])
  (:require [database.core :as database])
  (:require [validate.core :as validate])
  (:require [printpdf.core :as printpdf])
  (:require [clojure.string :as str])
  (:gen-class))

(declare add-course-frame)
(declare add-student-frame)
(declare student-list-frame)
(declare view-student-frame)
(declare enter-deposit-frame)
(declare purchase-list-frame)
(declare add-purchase-frame)
(declare laundry-list-frame)
(declare add-laundry-frame)
(declare handler-end-course)

(def f (frame :size [800 :by 600] :resizable? false))


(defn handler-select-course [event]
    (let [table (select f [:#all-courses])
          data (value-at table (selection table))
          course-name (:name data)]
      (cond
        (empty? course-name) (alert "Please select a course")
        :else (config! f :title "Student List"
                         :content (student-list-frame course-name)))))


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
                                        :font {:size 20}
                                        :listen [:action handler-select-course])])))


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
                                      :listen [:action handler-add-course])])

        :south (flow-panel :items [(button :text "Back"
                 :font {:size 20}
                 :size [200 :by 40]
                 :listen [:action (fn [e] (config! f :title "Course List"
                                                     :content (view-courses-frame)))])])))


(defn handler-view-student [course-name]
    (let [table (select f [:#all-students])
          data (value-at table (selection table))
          student-name (:name data)]
          (cond
            (empty? student-name) (alert "Please select a student")
            :else (config! f :title "Student Details"
                             :content (view-student-frame course-name student-name)))))


(defn student-list-frame [course-name]
  (border-panel :hgap 5 :vgap 5
                :west " "
                :east " "
                :north (flow-panel
                            :items [(button :text "Home"
                                            :font {:size 20}
                                            :listen [:action (fn [e]
                                                                (config! f :title "Course List"
                                                                           :content (view-courses-frame)))])])
                :center (scrollable
                            (table
                                :id :all-students
                                :selection-mode :single
                                :font {:size 16}
                                :model [:columns
                                          [{:key :name, :text "Student Name"}
                                           {:key :room, :text "Room No."}
                                           {:key :seat, :text "Seat No."}]
                                        :rows
                                          (vec (database/student-list course-name))]))
                :south (flow-panel
                            :items [(button :text "Add Student"
                                            :font {:size 20}
                                            :listen [:action (fn [e]
                                                                (config! f :title "Add Student"
                                                                           :content (add-student-frame course-name)))])
                                    "  "
                                    (button :text "Select Student"
                                             :font {:size 20}
                                             :listen [:action (fn [e]
                                                                  (handler-view-student course-name))])
                                    "  "
                                    (button :text "End Course"
                                             :font {:size 20}
                                             :listen [:action (fn [e]
                                                                  (handler-end-course course-name))])])))


(defn handler-add-student [course-name]
    (let [data (value (select f [:#add-student-form]))
          student-name (str/trim (:student-name data))
          room         (str/trim (:room data))
          seat         (str/trim (:seat data))]
          (cond
            (validate/is-empty? student-name room seat) (alert "Please enter all the fields")
            (database/student-exists? course-name student-name) (alert (str "Student with name " student-name " already exists"))
            :else (do (database/add-student course-name student-name room seat)
                  (alert "Student added successfully")
                  (config! f :title "Student List"
                             :content (student-list-frame course-name))))))


(defn add-student-frame [course-name]
  (border-panel :vgap 150 :hgap 150
        :north " "
        :west  " "
        :east  " "
        :center
            (vertical-panel
                      :id :add-student-form
                      :items [(label :text "Student Name:" :font {:size 20})
                              (text :id :student-name :font {:size 20})
                              " "
                              (label :text "Room" :font {:size 20})
                              (text :id :room :font {:size 20})
                              " "
                              (label :text "Seat:" :font {:size 20})
                              (text :id :seat :font {:size 20})
                              " "
                              (button :id :add-student
                                      :text "Add Student"
                                      :font {:size 20}
                                      :listen [:action (fn [e] (handler-add-student course-name))])])

        :south (flow-panel :items [(button :text "Home"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Course List"
                                                                               :content (view-courses-frame)))])
                                   "  "
                                   (button :text "Back"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Student List"
                                                                               :content (student-list-frame course-name)))])])))


(defn view-student-frame
  [course-name student-name]
  (let [[room seat deposit purchase laundry] (database/student-details course-name student-name)]
  (border-panel :hgap 100 :vgap 100
    :north " "
    :west  " "
    :east  " "
    :center (grid-panel
                :border "Details"
                :columns 2
                :items [(label :text "Name" :font {:size 20})
                        (label :text student-name :font {:size 20})
                        (label :text "Room" :font {:size 20})
                        (label :text room :font {:size 20})
                        (label :text "Seat" :font {:size 20})
                        (label :text seat :font {:size 20})
                        (label :text "Deposit" :font {:size 20})
                        (label :text deposit :font {:size 20})
                        (label :text "Purchases" :font {:size 20})
                        (label :text purchase :font {:size 20})
                        (label :text "Laundry" :font {:size 20})
                        (label :text laundry :font {:size 20})])
    :south (flow-panel
                :items [(button :text "Enter Deposit"
                                :font {:size 20}
                                :listen [:action (fn [e] (config! f :title "Enter Deposit"
                                                                    :content (enter-deposit-frame course-name student-name)))])
                        " "
                        (button :text "Purchases"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Purchase List"
                                                                               :content (purchase-list-frame course-name student-name)))])
                        " "
                        (button :text "Laundry"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Laundry List"
                                                                               :content (laundry-list-frame course-name student-name)))])
                        " "
                        (button :text "Back"
                                :font {:size 20}
                                :size [150 :by 40]
                                :listen [:action (fn [e] (config! f :title "Student List"
                                                                    :content (student-list-frame course-name)))])]))))


(defn handler-add-deposit [course-name student-name]
    (let [data (value (select f [:#deposit-form]))
          deposit (str/trim (:deposit data))]
        (cond
          (empty? deposit) (alert "Please enter the deposit amount")
          (not (validate/integer-validator deposit)) (alert "Please enter correct deposit amount")
          :else (do (database/add-deposit course-name student-name deposit)
                    (alert "Deposit added successfully")
                    (config! f :title "Student Details"
                               :content (view-student-frame course-name student-name))))))


(defn enter-deposit-frame [course-name student-name]
  (border-panel :vgap 215 :hgap 200
        :north " "
        :west  " "
        :east  " "
        :center (vertical-panel
                      :id :deposit-form
                      :items [(label :text "Deposit Amount:" :font {:size 20})
                              (text :id :deposit :font {:size 20})
                              " "
                              (button :id :enter-deposit
                                      :text "Enter Deposit"
                                      :font {:size 20}
                                      :listen [:action (fn [e] (handler-add-deposit course-name student-name))])])

        :south (flow-panel :items [(button :text "Back"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Student Details"
                                                                               :content (view-student-frame course-name student-name)))])])))


(defn purchase-list-frame [course-name student-name]
  (border-panel :hgap 5 :vgap 5
                :west " "
                :east " "
                :north " "
                :center (scrollable (table
                                :id :all-purchases
                                :selection-mode :single
                                :font {:size 16}
                                :model [:columns
                                          [{:key :serial-number, :text "S.No."}
                                           {:key :purchase-name, :text "Purchase"}
                                           {:key :purchase-cost, :text "Cost"}]
                                        :rows
                                          (vec (database/purchase-list course-name student-name))]))
                :south (flow-panel
                            :items [(button :text "Add Purchase"
                                             :font {:size 20}
                                             :listen [:action (fn [e]
                                                                (config! f :title "Add Purchase"
                                                                           :content (add-purchase-frame course-name student-name)))])
                                    "  "
                                    (button :text "Back"
                                            :font {:size 20}
                                            :listen [:action (fn [e]
                                                                (config! f :title "Student Details"
                                                                           :content (view-student-frame course-name student-name)))])])))


(defn handler-add-purchase [course-name student-name]
    (let [data (value (select f [:#purchase-form]))
          purchase-name (str/trim (:purchase-name data))
          purchase-cost (str/trim (:purchase-cost data))]
      (cond
          (or (empty? student-name) (empty? purchase-cost)) (alert "Please enter all the fields")
          (not (validate/integer-validator purchase-cost)) (alert "Please enter correct cost")
          :else (do (database/add-purchase course-name student-name purchase-name purchase-cost)
                    (alert "Purchase added successfully")
                    (config! f :title "Purchase List"
                               :content (purchase-list-frame course-name student-name))))))


(defn add-purchase-frame [course-name student-name]
  (border-panel :hgap 180 :vgap 180
        :north " "
        :west  " "
        :east  " "
        :center
          (vertical-panel
                      :id :purchase-form
                      :items [(label :text "Purchase Name:" :font {:size 20})
                              (text :id :purchase-name :font {:size 20})
                              " "
                              (label :text "Cost:" :font {:size 20})
                              (text :id :purchase-cost :font {:size 20})
                              " "
                              (button :id :add-purchase
                                      :text "Add Purchase"
                                      :font {:size 20}
                                      :listen [:action (fn [e] (handler-add-purchase course-name student-name))])])

        :south (flow-panel :items [(button :text "Back"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Purchase List"
                                                                               :content (purchase-list-frame course-name student-name)))])])))


(defn laundry-list-frame [course-name student-name]
  (border-panel :hgap 5 :vgap 5
                :west " "
                :east " "
                :north " "
                :center (scrollable (table
                                :id :all-laundry
                                :selection-mode :single
                                :font {:size 16}
                                :model [:columns
                                          [{:key :serial-number, :text "S.No."}
                                           {:key :laundry-name, :text "Name"}
                                           {:key :laundry-cost, :text "Cost"}]
                                        :rows
                                          (vec (database/laundry-list course-name student-name))]))
                :south (flow-panel
                            :items [(button :text "Add Laundry"
                                             :font {:size 20}
                                             :listen [:action (fn [e]
                                                                (config! f :title "Add Laundry"
                                                                           :content (add-laundry-frame course-name student-name)))])
                                    "  "
                                    (button :text "Back"
                                            :font {:size 20}
                                            :listen [:action (fn [e]
                                                                (config! f :title "Student Details"
                                                                           :content (view-student-frame course-name student-name)))])])))


(defn handler-add-laundry [course-name student-name]
    (let [data (value (select f [:#laundry-form]))
          laundry-cost (str/trim (:laundry-cost data))]
        (cond
          (validate/is-empty? laundry-cost) (alert "Please enter laundry cost")
          (not (validate/integer-validator laundry-cost)) (alert "Please enter correct cost")
          :else (do (database/add-laundry course-name student-name laundry-cost)
                    (alert "Laundry added successfully")
                    (config! f :title "Laundry List"
                               :content (laundry-list-frame course-name student-name))))))


(defn add-laundry-frame [course-name student-name]
  (border-panel :vgap 215 :hgap 200
        :north " "
        :west  " "
        :east  " "
        :center
          (vertical-panel
                      :id :laundry-form
                      :items [(label :text "Laundry Cost:" :font {:size 20})
                              (text :id :laundry-cost :font {:size 20})
                              " "
                              (button :id :add-laundry
                                      :text "Add Laundry"
                                      :font {:size 20}
                                      :listen [:action (fn [e] (handler-add-laundry course-name student-name))])])

       :south (flow-panel :items [(button :text "Back"
                                           :font {:size 20}
                                           :size [150 :by 40]
                                           :listen [:action (fn [e] (config! f :title "Laundry List"
                                                                               :content (laundry-list-frame course-name student-name)))])])))


(defn handler-end-course
  [course-name]
  (do (printpdf/generate-pdf course-name)
      (alert "Course ended")
      (config! f :title "Course List"
               :content (view-courses-frame))))

(defn init []
  (-> (config! f :title "Vipassana"
               :content (view-courses-frame))
      show!))
