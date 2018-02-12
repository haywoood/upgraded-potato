(ns bakery.events
  (:require [re-frame.core :as re-frame]
            [bakery.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::add-to-cart
  (fn [db [_ id]]
    (update-in db [:cart id] inc)))
