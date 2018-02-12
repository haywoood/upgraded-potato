(ns bakery.subs
  (:require [re-frame.core :as re-frame]
            [bakery.helpers :refer [price-for-treat get-total normalize-cart-item]]))

(re-frame/reg-sub ::treats :treats)
(re-frame/reg-sub ::cart :cart)

(re-frame/reg-sub
  ::cart-normalized
  :<- [::treats]
  :<- [::cart]
  (fn [[treats cart] _]
    (map (partial normalize-cart-item treats) cart)))

(re-frame/reg-sub
  ::cart-list-view
  :<- [::cart-normalized]
  (fn [cart _]
    (mapv (fn [[treat number-of-items]]
            (let [{:keys [name id]} treat
                  text (str name (if (> number-of-items 1)
                                   (str " x " number-of-items)))
                  total (price-for-treat treat number-of-items)]
              {:text text
               :id id
               :total total}))
          cart)))

(re-frame/reg-sub
  ::total
  :<- [::cart-normalized]
  get-total)
