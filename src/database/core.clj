(ns database.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])

(declare save)

(def all-course-data (atom {}))
(def file-path "data.json")

(defn add-course
    [coursename start duration]
    (let [this-course {"start" start,
                      "duration" duration,
                      "students" {}}]
        (swap! all-course-data assoc-in [coursename] this-course)))


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! all-course-data (json/read-str (slurp file-path)))))