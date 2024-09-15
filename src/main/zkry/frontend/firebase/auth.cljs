(ns zkry.frontend.firebase.auth
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            ["firebase/auth" :refer [getAuth onAuthStateChanged signInAnonymously]]))


(defn sign-in-anonymously [_]
  "Sign-in anonymously into Firebase."
  (-> (signInAnonymously (getAuth))
      (.then (fn [^js result]
               (js/console.log "User signed in: " (.-user result))))
      (.catch (fn [e]
                (js/console.error e)))))

(defn user->data [^js user]
  (when user
    {:email        (.-email user)
     :uid          (.-uid user)
     :display-name (.-displayName user)}))

(defn user-info []
  (let [auth-state (r/atom nil)
        callback (fn [x]
                   (reset! auth-state (user->data x)))
        error-callback (fn [x] (reset! auth-state x))]
    (onAuthStateChanged (getAuth)
      callback
      error-callback)
    auth-state))

(rf/reg-sub ::user-auth
  user-info
  (fn [user]
    (if (or (not user) (instance? js/Error user))
      nil
      user)))

(rf/reg-sub ::uid
  (fn [] (rf/subscribe [::user-auth]))
  (fn [auth]
    (when auth
      (:uid auth))))

(rf/reg-fx ::anonymous-sign-in sign-in-anonymously)

(rf/reg-event-fx
  ::sign-in
  (fn [_ _] {::anonymous-sign-in nil}))
