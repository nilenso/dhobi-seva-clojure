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


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! all-course-data (json/read-str (slurp file-path)))))