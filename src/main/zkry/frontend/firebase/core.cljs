(ns zkry.frontend.firebase.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            ["firebase/analytics" :refer [getAnalytics]]
            ["firebase/app" :refer [initializeApp]]))

(def firebase-config
  #js {:apiKey "AIzaSyDT7AbrZXxQSvW7JVF_DRhBM8RxvgcYHW8"
       :authDomain "cljs-demo.firebaseapp.com"
       :projectId "cljs-demo"
       :storageBucket "cljs-demo.appspot.com"
       :messagingSenderId "617590944724"
       :appId "1:617590944724:web:ec03a6ff3c272773af5c41"
       :measurementId "G-GY9Y474M7N"})

(defn init-fb-app []
  (let [app (initializeApp firebase-config)]
    (getAnalytics app)
    (js/console.log "Firebase initialized")))
