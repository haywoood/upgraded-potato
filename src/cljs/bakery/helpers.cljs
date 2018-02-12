(ns bakery.helpers
  (:require [goog.string :as gs]
            [goog.string.format]))

;; Bulk pricing logic
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

;; Get the cart total factoring bulk pricing in
(defn get-total [cart] (reduce (fn [amount [treat number-of-items]]
                                 (price-for-treat amount treat number-of-items))
                        0
                        cart))

;; DB helpers
(defn get-treat-by-id [treats _id]
  (first (filter (fn [{:keys [id]}] (= id _id)) treats)))

(defn normalize-cart-item [treats [id number-of-items]]
  (let [treat (get-treat-by-id treats id)]
    [treat number-of-items]))


;; Formatting
(defn format-number [n] (str "$" (gs/format "%.2f" n)))

(defn price-text [price bulkPricing]
  (str (format-number price) (and bulkPricing
                               (str " or " (:amount bulkPricing)
                                    " for " (format-number (:totalPrice bulkPricing))))))
