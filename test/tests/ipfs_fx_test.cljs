(ns tests.ipfs-fx-test
  (:require [cljs.test :refer [deftest is]]
            [clojure.string :as string]
            [day8.re-frame.test :refer [run-test-async wait-for run-test-sync]]
            [district0x.re-frame.ipfs-fx :as core]
            [re-frame.core :as re-frame]))

(def interceptors [re-frame/trim-v])

(defn- parse-response [s]
  (-> s
      (#(.parse js/JSON %))
      (js->clj :keywordize-keys true)))

(re-frame/reg-event-fx
 ::error
 interceptors
 (fn [{:keys [:db]} [data]]

   (prn "@@@ ERROR" data)

   {:db (assoc db :last-error data)}))

(re-frame/reg-event-fx
 ::on-list-files-success
 interceptors
 (fn [{:keys [:db]} [data]]

   (prn "@@@ LIST-FILES-SUCCESS" data)

   {:db (assoc db :files data)}))

(re-frame/reg-event-fx
 ::init-ipfs
 interceptors
 (fn [_ [url]]
   {:ipfs/init nil}))

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

   (prn "@@@ ON-FILE-ADDED" data)

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
     (re-frame/dispatch [::list-files "/ipfs/QmTeW79w7QQ6Npa3b1d5tANreCDxF2iDaAPsDvW6KtLmfB/"])
     (wait-for [::on-list-files-success ::error]
       (is (= (parse-response @files)
              {:Objects [{:Hash "/ipfs/QmTeW79w7QQ6Npa3b1d5tANreCDxF2iDaAPsDvW6KtLmfB/",
                          :Links [{:Name "docs", :Hash "QmZiCKXmkWA5S5PsnvKFqarZiA4uhGBUJF8Mt927TuxS9p", :Size 0, :Type 1, :Target ""}
                                  {:Name "index.html", :Hash "QmVag56JHbM3dzrX8knDTQyirgBzuxokJ41UqhXn8VY1mD", :Size 9967, :Type 2, :Target ""}
                                  {:Name "styles", :Hash "QmcJWgy61a7j1fXpr9FmPouSttAqyXvAb3TfJ32mosDVKD", :Size 0, :Type 1, :Target ""}]}]}))))))

(deftest upload-files
  (run-test-async
   (let [fadded (re-frame/subscribe [::file-added])]
     (re-frame/dispatch [::init-ipfs])
     (re-frame/dispatch [::add-file (js/Blob. ["test data"])])
     (wait-for [::on-file-added ::error]
       (is (= (parse-response @fadded) {:Name "blob", :Hash "QmWmsL95CYvci8JiortAMhezezr8BhAwAVohVUSJBcZcBL", :Size "17"}))))))
