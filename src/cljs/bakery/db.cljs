(ns bakery.db)

(def raw-data (js->clj js/window.data :keywordize-keys true))

(def default-db
  {:treats (:treats raw-data)
   :cart {}})
