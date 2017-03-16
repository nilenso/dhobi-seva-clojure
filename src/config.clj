(ns config
  (:require [clojure.edn :as edn]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]))

(def filename "config.edn")

(defn read-config []
  (-> filename io/resource slurp edn/read-string))

(defonce current
  (atom (read-config)))

(defn lookup [& ks]
  (get-in @current ks))

(defn reload!
  "Resets the config atom. "
  ([]
   (reload! (read-config)))
  ([config-map]
   (reset! current config-map)))

(defn write-config! [config-map]
  (spit (-> filename io/resource)
        (-> config-map pp/pprint with-out-str)))

(defn update-config! [ks v]
  (-> (lookup)
      (assoc-in ks v)
      reload!
      write-config!))
