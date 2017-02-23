(ns database.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])

(declare save)

(def course (atom {}))
(def file-path "data.json")

(defn add-Course
    [coursename start duration]
    (let [thiscourse {"start" start,
                      "duration" duration,
                      "students" {}}]
        (swap! course assoc-in [coursename] thiscourse)))


(defn add-Student
    [id studentname age gender deposit roomNo]
    (let [student {"age" age,
                   "gender" gender,
                   "deposit" deposit,
                   "roomNo" roomNo,
                   "transactions" []}]
        (swap! course assoc-in [id "students" studentname] student)))


(defn add-Transaction
    [id studentName desc quantity rate date]
    (let [transaction {"desc" desc,
                       "quantity" quantity,
                       "rate" rate,
                       "total" (* quantity rate),
                       "date" date}
          length (count (get-in @course [id "students" studentName "transactions"]))]
    (swap! course assoc-in [id "students" studentName "transactions" length] transaction)))


(defn update-Deposit
    [id studentName amount]
    (swap! course update-in [id "students" studentName "deposit"] + amount))


(defn save
    []
    (spit file-path (json/write-str @course)))


(defn main
    []
    (if (.exists (io/as-file file-path))
        (reset! course (json/read-str (slurp file-path)))))