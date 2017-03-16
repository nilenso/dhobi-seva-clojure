(ns app
  (:require [config]
            [database.core :as database]
            [gui.core :as gui]))

(defn -main []
  (config/update-config! [:json-path] "data.json")
  (database/init)
  (gui/init))
