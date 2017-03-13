(ns printpdf.core
  (:use clj-pdf.core)
  (:require [database.core :as database])
  (:gen-class))

(defn single-student
  [course-name student-name]
  (let [[room seat deposit purchase laundry] (database/student-details course-name student-name)]
      [:table {:border true}
      [[:paragraph [:paragraph [:chunk {:style :bold} "Name: "] student-name]
                   [:paragraph [:chunk {:style :bold} "Room: "] room]
                   [:paragraph [:chunk {:style :bold} "Deposit: "] deposit]
                   [:paragraph [:chunk {:style :bold} "Laundry: "] laundry]
                   [:paragraph [:chunk {:style :bold} "Purchases: "] purchase]]]]))


(defn generate-pdf
  [course-name]
    (pdf
     [{:size "a4"
       :left-margin 50
       :top-margin  30}
       (loop [student-list (database/all-student-names course-name)
              table-data [:table {:align :justified :border false :cell-border false :spacing 3}]]
              (if (empty? student-list)
                  (into table-data [["" ""]])
                  (if (empty? (rest student-list))
                      (recur (rest student-list) (into table-data [(conj [] (single-student course-name (first student-list)) "")]))
                      (recur (rest (rest student-list)) (into table-data [(conj [] (single-student course-name (first student-list)) (single-student course-name (second student-list)))])))))]
      (str course-name ".pdf")))