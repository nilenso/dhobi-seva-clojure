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


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! course (json/read-str (slurp file-path)))))