(ns database.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])

(declare save)

(def course (atom {}))
(def file-path "data.json")


(defn add-Course
    [coursename start duration]
    (let [this-course {"start" start,
                      "duration" duration,
                      "students" {}}]
        (swap! course assoc-in [coursename] this-course)))


(defn add-Student
    [course-name student-name roomNo seatNo]
    (let [student {"room" roomNo,
                   "seat" seatNo,
                   "deposit" 0,
                   "transactions" []}]
        (swap! course assoc-in [course-name "students" student-name] student)))


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! course (json/read-str (slurp file-path)))))