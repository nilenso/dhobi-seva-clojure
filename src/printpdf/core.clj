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