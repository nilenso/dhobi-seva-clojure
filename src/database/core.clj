(ns database.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])

(declare save)

(def all-course-data (atom {}))
(def file-path "data.json")


(defn add-course
    [course-name start duration]
    (let [this-course {:start start,
                       :duration duration,
                       :students {}}]
        (swap! all-course-data assoc-in [(keyword course-name)] this-course)))


(defn add-student
    [course-name student-name roomNo seatNo]
    (let [student {:room roomNo,
                   :seat seatNo,
                   :deposit 0,
                   :purchases [],
                   :laundry []}]
        (swap! all-course-data assoc-in [(keyword course-name) :students (keyword student-name)] student)))


(defn course-exists?
    [course-name]
      (get-in @all-course-data [(keyword course-name)]))


(defn student-exists?
    [course-name student-name]
    (get-in @all-course-data [(keyword course-name) :students (keyword student-name)]))


(defn course-list
  []
  (for [all-courses (keys @all-course-data)]
      (let [course-data (get-in @all-course-data [all-courses])]
          (hash-map :name (name all-courses), :date (get course-data :start), :duration (get course-data :duration)))))


(defn student-list
  [course-name]
  (for [all-students (sort (keys (get-in @all-course-data [(keyword course-name) :students])))]
      (let [student-data (get-in @all-course-data [(keyword course-name) :students all-students])]
          (hash-map :name (name all-students), :room (get student-data :room), :seat (get student-data :seat)))))


(defn sum-of-list
  [in-list]
  (if (empty? in-list) 
      0 
      (reduce + in-list)))


(defn student-details
  [course-name student-name]
  (let [student-data (get-in @all-course-data [(keyword course-name) :students (keyword student-name)])
        room (get student-data :room)
        seat (get student-data :seat)
        deposit (get student-data :deposit)
        all-purchases (get student-data :purchases)
        purchase-amount-list (for [purchase all-purchases]
                                    (get purchase :purchase-cost))
        total-purchase (sum-of-list purchase-amount-list)
        all-laundry (get student-data :laundry)
        laundry-amount-list (for [laundry all-laundry]
                                    (get laundry :laundry-cost))
        total-laundry (sum-of-list laundry-amount-list)]
    [room seat deposit total-purchase total-laundry]))


(defn add-deposit
    [course-name student-name deposit]
        (swap! all-course-data assoc-in [(keyword course-name) :students (keyword student-name) :deposit] (Integer. deposit)))


(defn add-purchase
    [course-name student-name purchase-name purchase-cost]
        (let [data {:purchase-name purchase-name,
                    :purchase-cost (Integer. purchase-cost)}
              length (count (get-in @all-course-data [(keyword course-name) :students (keyword student-name) :purchases]))]
        (swap! all-course-data assoc-in [(keyword course-name) :students (keyword student-name) :purchases length] data)))


(defn add-laundry
    [course-name student-name laundry-cost]
        (let [data {:laundry-name "Laundry"
                    :laundry-cost (Integer. laundry-cost)}
              length (count (get-in @all-course-data [(keyword course-name) :students (keyword student-name) :laundry]))]
        (swap! all-course-data assoc-in [(keyword course-name) :students (keyword student-name) :laundry length] data)))


(defn purchase-list
  [course-name student-name]
  (let [x (atom 0)]
    (for [all-purchases (get-in @all-course-data [(keyword course-name) :students (keyword student-name) :purchases])
          :let [position (swap! x inc)]]
        (hash-map :serial-number position, 
                  :purchase-name (get all-purchases :purchase-name), 
                  :purchase-cost (get all-purchases :purchase-cost)))))


(defn laundry-list
  [course-name student-name]
  (let [x (atom 0)]
    (for [all-laundry (get-in @all-course-data [(keyword course-name) :students (keyword student-name) :laundry])
          :let [position (swap! x inc)]]
        (hash-map :serial-number position, 
                  :laundry-name "Laundry", 
                  :laundry-cost (get all-laundry :laundry-cost)))))


(defn all-student-names
  [course-name]
  (sort (keys (get-in @all-course-data [(keyword course-name) :students]))))


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! all-course-data (json/read-str (slurp file-path) :key-fn keyword))))