(ns database.core-test
  (:require [config]
            [clojure.test :refer :all]
            [database.core :refer :all]))

(config/update-config! [:json-path] "test.json")

(deftest test-transformations
	(testing "add-course returns the course saved"
    (empty-data!)
    (is (= {:name "Vipassana" :start "2017-03-17", :duration "10", :students {}}
           (add-course "Vipassana" "2017-03-17" "10"))))

  (testing "add-student returns the student saved"
    (empty-data!)
    (is (= {:name "Mayank" :room "23B", :seat "12", :deposit 0, :purchases [], :laundry []}
           (add-student "Dhamma Pith - 10 Day - December 2017 #2" "Mayank" "23B" "12"))))

  ;; TODO: check data formats for --
  ;; - add deposit
  ;; - add laundry
  ;; - add purchase
  )

(deftest test-persistence
  (testing "sanity check resetting and re-reading the json file"
    (empty-data!)
    (add-course "FooCourse" "1991-05-05" "30")
    (reset-from-json!)
    (is (= {:FooCourse {:name "FooCourse" :start "1991-05-05", :duration "30", :students {}}}
           @all-course-data))
    (add-course "ZigCourse" "2015-01-28" "60")
    (reset-from-json!)
    (is (= {:FooCourse {:name "FooCourse" :start "1991-05-05", :duration "30", :students {}}
            :ZigCourse {:name "ZigCourse" :start "2015-01-28", :duration "60", :students {}}}
           @all-course-data)))

  (testing "add-course saves the new course to json"
    (empty-data!)
    (add-course "Dhamma Paphulla March" "2015-01-28" "60")
    (reset-from-json!)
    (is (= {(keyword "Dhamma Paphulla March")
            {:name "Dhamma Paphulla March" :start "2015-01-28", :duration "60", :students {}}}
           @all-course-data)))

  ;; TODO (read tests are implicit):
  ;; - add student
  ;; - add deposit
  ;; - add laundry
  ;; - add purchase
  )
