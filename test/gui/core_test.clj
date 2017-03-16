(ns gui.core-test
  (:require [clojure.test :refer :all]
            [gui.core :refer :all]))

(def all-course-data (atom {}))

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

(deftest a-test
	(let [data {:Vipassana {:start "2017-03-17", :duration "10", :students {}}}] 
  (testing "FIXME, I fail."
    (is (= data (add-course "Vipassana" "2017-03-17" "10"))))))
