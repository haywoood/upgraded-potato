(ns bakery.db)

(def raw-data (js->clj js/window.data :keywordize-keys true))

;; :cart is a map of key value pairs, where the key is the
;; corresponding treat id, and the value is a number that we
;; use to keep track of the item amount via `(update-in db [:cart id] inc)`

(def default-db
  {:treats (:treats raw-data)
   :cart {}})
