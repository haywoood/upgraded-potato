(ns bakery.subs
  (:require [re-frame.core :as re-frame]))

(defn get-price-with-bulk [price {:keys [amount totalPrice]} number-of-items]
  (if (>= number-of-items amount)
    (let [number-of-bulk (js/Math.floor (/ number-of-items amount))
          number-of-non-bulk (- number-of-items (* number-of-bulk amount))
          bulk-price (* number-of-bulk totalPrice)
          non-bulk-price (* number-of-non-bulk price)]
      (+ non-bulk-price bulk-price))
    (* number-of-items price)))

(defn price-for-treat
  ([treat number-of-items]
   (price-for-treat 0 treat number-of-items))
  ([total {:keys [price bulkPricing]} number-of-items]
   (if (nil? bulkPricing)
     (+ total (* number-of-items price))
     (+ total (get-price-with-bulk price bulkPricing number-of-items)))))

(defn get-total [cart] (reduce (fn [amount [treat number-of-items]]
                                 (price-for-treat amount treat number-of-items))
                        0
                        cart))

(defn get-treat-by-id [treats _id]
  (first (filter (fn [{:keys [id]}] (= id _id)) treats)))

(defn normalize-cart-item [treats [id number-of-items]]
  (let [treat (get-treat-by-id treats id)]
    [treat number-of-items]))

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
