(ns validate.core-test
  (:require [validate.core :refer :all]
            [clojure.test :refer :all]))

(testing "date validation"
  (deftest date-validation-accepts-dates-from-0000
    (is (not (nil? (date-validator "0000-12-01")))))

  (deftest date-validation-accepts-dates-to-9999
    (is (not (nil? (date-validator "9999-12-01"))))))
