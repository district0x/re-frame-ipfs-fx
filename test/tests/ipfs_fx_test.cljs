(ns tests.ipfs-fx-test
  (:require [cljs.test :refer [deftest is run-tests]]
            [clojure.string :as string]
            [day8.re-frame.test :refer [run-test-async wait-for run-test-sync]]
            [district0x.re-frame.ipfs-fx :as core]
            ["buffer" :refer [Buffer]]
            [re-frame.core :as re-frame]))

(def interceptors [re-frame/trim-v])

(defn to-buffer [data]
  (.from Buffer data))

(defn- parse-response [s]
  (-> s
      (#(.parse js/JSON %))
      (js->clj :keywordize-keys true)))

(re-frame/reg-event-fx
 ::error
 interceptors
 (fn [{:keys [:db]} [data]]
   {:db (assoc db :last-error data)}))

(re-frame/reg-event-fx
 ::on-list-files-success
 interceptors
 (fn [{:keys [:db]} [data]]
   {:db (assoc db :files data)}))

(re-frame/reg-event-fx
 ::init-ipfs
 interceptors
 (fn [_ [url]]
   {:ipfs/init {:host "http://127.0.0.1:5001" :endpoint "/api/v0"}}))

(re-frame/reg-event-fx
 ::list-files
 interceptors
 (fn [_ [url]]
   {:ipfs/call {:func "ls"
                :args [url]
                :on-success [::on-list-files-success]
                :on-error [::error]}}))

(re-frame/reg-event-fx
 ::on-file-added
 interceptors
 (fn [{:keys [:db]} [data]]
   {:db (assoc db :file-added data)}))

(re-frame/reg-event-fx
 ::add-file
 interceptors
 (fn [_ [data]]
   {:ipfs/call {:func "add"
                :args [data]
                :on-success [::on-file-added]
                :on-error [::error]}}))

(re-frame/reg-sub ::files (fn [db] (:files db)))
(re-frame/reg-sub ::file-added (fn [db] (:file-added db)))
(re-frame/reg-sub ::last-error (fn [db] (:last-error db)))

(deftest ls-files
  (run-test-async
   (let [files (re-frame/subscribe [::files])]
     (re-frame/dispatch [::init-ipfs])
     (re-frame/dispatch [::list-files "/ipfs/QmTeW79w7QQ6Npa3b1d5tANreCDxF2iDaAPsDvW6KtLmfB"])
     (wait-for [::on-list-files-success ::error]
       (is (-> @files nil? not))))))

(deftest upload-files
  (run-test-async
   (let [fadded (re-frame/subscribe [::file-added])]
     (re-frame/dispatch [::init-ipfs])
     (re-frame/dispatch [::add-file (to-buffer "test data")])
     (wait-for [::on-file-added ::error]
       (is (-> @fadded nil? not))))))
