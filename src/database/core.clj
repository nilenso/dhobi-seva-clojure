(ns database.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])

(declare save)

(def all-course-data (atom {}))
(def file-path "data.json")

(defn add-course
    [course-name start duration]
    (let [this-course {"start" start,
                      "duration" duration,
                      "students" {}}]
        (swap! all-course-data assoc-in [course-name] this-course)))


(defn course-exists?
    [course-name]
      (get-in @all-course-data [course-name]))


(defn student-exists?
    [course-name student-name]
    (get-in @all-course-data [course-name "students" student-name]))


(defn course-list
  []
  (for [all-courses (keys @all-course-data)]
      (let [course-data (get-in @all-course-data [all-courses])]
          (hash-map :name all-courses, :date (get course-data "start"), :duration (get course-data "duration")))))


(defn student-list
  [course-name]
  (for [all-students (sort (keys (get-in @all-course-data [course-name "students"])))]
      (let [student-data (get-in @all-course-data [course-name "students" all-students])]
          (hash-map :name all-students, :room (get student-data "room"), :seat (get student-data "seat")))))


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! all-course-data (json/read-str (slurp file-path)))))