(ns bakery.views
  (:require [re-frame.core :as re-frame]
            [goog.string :as gs]
            [goog.string.format]
            [bakery.subs :as subs]
            [bakery.events :as events]))

(defn format-number [n] (str "$" (gs/format "%.2f" n)))

(defn price-text [price bulkPricing]
  (str (format-number price) (and bulkPricing
                               (str " or " (:amount bulkPricing)
                                    " for " (format-number (:totalPrice bulkPricing))))))

(defn treat-item [{:keys [id name imageURL id price bulkPricing]}]
  [:div {:key id :class "Treat-item"}
    [:img {:src imageURL :class "Treat-image"}]
    [:div {:class "u-displayFlex u-flexColumn"}
      [:div {:class "u-flex1"}
       [:div {:class "name"} name]
       [:div (price-text price bulkPricing)]]
      [:button {:class "AddToCart Button"
                :on-click #(re-frame/dispatch [::events/add-to-cart id])}
         "Add to Cart"]]])

(defn treat-list []
  (let [treats (re-frame/subscribe [::subs/treats])]
    [:div {:class "Treat-list"} (map treat-item @treats)]))

(defn cart-item [{:keys [text total id]}]
  [:div {:key id :class "CartItem u-displayFlex u-spaceBetween"}
    [:div {:class "CartItem-text"} text]
    [:div {:class "CartItem-total"} (format-number total)]])

(defn cart []
  (let [items (re-frame/subscribe [::subs/cart-list-view])
        total (re-frame/subscribe [::subs/total])]
    (if (not-empty @items)
      [:div
        [:div {:class "Cart-list"}
         (map cart-item @items)
         (cart-item {:text "Total" :total @total :id "total"})]
        [:button {:class "Clear Button" :on-click #(re-frame/dispatch [::events/initialize-db])} "Clear Cart"]]
      [:div ""])))

(defn main-panel []
  [:div {:class "App"}
   (treat-list)
   [:div {:class "Cart"}
    [:div {:class "Logo"} "Kontor Bakery"]
    (cart)]])
