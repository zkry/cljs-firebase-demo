(ns zkry.frontend.firebase.firestore
  (:require [zkry.frontend.firebase.auth :as fb-auth]
            [reagent.core :as r]
            [re-frame.core :as rf]
            ["firebase/firestore" :refer
             [query where onSnapshot
              getFirestore collection doc setDoc]]
            ["firebase/app" :refer [getApp]]))

(defn- add-todo [{:keys [description userId]}]
  (let [db (getFirestore (getApp))
        todo-uuid (random-uuid)]
    (setDoc (doc db "todos" (str todo-uuid))
            (clj->js {:userId userId
                      :description description}))))

(rf/reg-fx ::add-todo-fx
           (fn [args]
             (add-todo args)))

(rf/reg-event-fx
 ::add-todo
 (fn [_ [_ todo-text]]
   (let [uid @(rf/subscribe [::fb-auth/uid])]
     (when uid
       {::add-todo-fx {:userId uid
                       :description todo-text}}))))

(defn user-todos [[_ uid]]
  (let [todos (r/atom nil)
        callback (fn [docs]
                   (reset! todos [])
                   (.forEach docs
                             (fn [doc]
                               (swap! todos conj
                                      {:id (.-id doc)
                                       :data (js->clj (.data doc) :keywordize-keys true)}))))
        db (getFirestore (getApp))
        q (query (collection db "todos")
                 (where "userId" "==" uid))]
    ;; (onAuthStateChanged (getAuth)
    ;;   callback
    ;;   error-callback)
    (onSnapshot q callback)
    todos))

(rf/reg-sub ::user-todos
  user-todos
  (fn [todos]
    todos))
