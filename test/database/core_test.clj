(ns database.core-test
  (:require [clojure.test :refer :all]
            [database.core :refer :all]))

(deftest test-transformations
	(testing "add-course returns a map keyed by course name"
    (empty-data!)
    (is (= {:Vipassana {:start "2017-03-17", :duration "10", :students {}}}
           (add-course "Vipassana" "2017-03-17" "10"))))

  (testing "add-student returns a map the student"
    (empty-data!)
    (is (= {:name "Mayank" :room "23B", :seat "12", :deposit 0, :purchases [], :laundry []}
           (add-student "Dhamma Pith - 10 Day - December 2017 #2" "Mayank" "23B" "12")))))
