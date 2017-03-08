(ns database.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])

(declare save)

(def all-course-data (atom {}))
(def file-path "data.json")


(defn course-list
  []
  (for [all-courses (keys @all-course-data)]
      (let [course-data (get-in @all-course-data [all-courses])]
          (hash-map :name all-courses, :date (get course-data "start"), :duration (get course-data "duration")))))


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! all-course-data (json/read-str (slurp file-path)))))