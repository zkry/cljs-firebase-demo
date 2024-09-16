(ns zkry.frontend.app
  (:require [zkry.frontend.firebase.core :refer [init-fb-app]]
            [zkry.frontend.firebase.firestore :as fb-firestore]
            [zkry.frontend.firebase.auth :as fb-auth]
            [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            ["firebase/app" :refer [initializeApp]]
            ["firebase/analytics" :refer [getAnalytics]]
            ["firebase/auth" :refer [getAuth onAuthStateChanged signInAnonymously]]))

(defn child-component []
  (let [uid (rf/subscribe [::fb-auth/uid])
        todos (rf/subscribe [::fb-firestore/user-todos @uid])
        text (r/atom "")]

    (fn []
      [:div
       [:div "User ID: " @uid]
       [:input {:type "text"
                :value @text
                :on-change #(reset! text (-> % .-target .-value))}]
       [:button {:on-click #(do (rf/dispatch [::fb-firestore/add-todo @text])
                                (reset! text ""))}
        "Add todo"]
       [:div
        (pr-str @todos)
        [:ul
         (for [todo @todos]
           (do
             (js/console.log (:data todo))
             [:li (:id todo) ": " (pr-str (:data todo))]))]]])))

(defn application-root []
  (rf/dispatch [::fb-auth/sign-in])
  [child-component])

(defn ^:dev/after-load mountit []
  (rd/render [application-root]
             (.getElementById js/document "root")))

(defn ^:export init []
  (println "Starting init...")
  (init-fb-app)
  (mountit))
