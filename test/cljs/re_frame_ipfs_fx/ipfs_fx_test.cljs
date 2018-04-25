(ns re-frame-ipfs-fx.ipfs-fx-test
  (:require
   [cljs.test :refer [deftest testing is async]]
   [cljs.test :as t]
   [district0x.re-frame-ipfs-fx.ipfs-fx :as core]
   [day8.re-frame.test :refer [run-test-async wait-for run-test-sync]]
   [re-frame.core :refer [reg-event-fx console dispatch trim-v reg-sub subscribe]]
   [taoensso.timbre :as timbre :refer-macros [log
                                              trace
                                              debug
                                              info
                                              warn
                                              error
                                              fatal
                                              report]]))

(def interceptors [trim-v])

(reg-event-fx
 ::error
 interceptors
 (fn [{:keys [:db]} [data]]
   {:db (assoc db :last-error data)}))

(reg-event-fx
 ::on-list-files-success
 interceptors
 (fn [{:keys [:db]} [data]]
   (info "GOT FILES" data)
   {:db (assoc db :files data)}))

(reg-event-fx
 ::init-ipfs
 interceptors
 (fn [_ [url]]
   {:ipfs/init nil}))

(reg-event-fx
 ::list-files
 interceptors
 (fn [_ [url]]
   {:ipfs/call {:func "ls"
                :args [url]
                :on-success ::on-list-files-success
                :on-error ::error}}))


(reg-sub ::files (fn [db] (:files db)))
(reg-sub ::last-error (fn [db] (:last-error db)))

(deftest ether-tests
  (run-test-async
    (let [files (subscribe [::files])]
      (dispatch [::init-ipfs])
      (dispatch [::list-files "/ipfs/QmYwAPJzv5CZsnA625s3Xf2nemtYgPpHdWEz79ojWnPbdG/"])

      (wait-for [::on-list-files-success ::error]
                (is (= @files {:Objects
                     [{:Hash "/ipfs/QmYwAPJzv5CZsnA625s3Xf2nemtYgPpHdWEz79ojWnPbdG/",
                       :Links [{:Name "about",
                                :Hash "QmZTR5bcpQD7cFgTorqxZDYaew1Wqgfbd2ud9QqGPAkK2V",
                                :Size 1688,
                                :Type 2}
                               {:Name "contact",
                                :Hash "QmYCvbfNbCwFR45HiNP45rwJgvatpiW38D961L5qAhUM5Y",
                                :Size 200,
                                :Type 2}
                               {:Name "help",
                                :Hash "QmY5heUM5qgRubMDD1og9fhCPA6QdkMp3QCwd4s7gJsyE7",
                                :Size 322,
                                :Type 2}
                               {:Name "quick-start",
                                :Hash "QmdncfsVm2h5Kqq9hPmU7oAVX2zTSVP3L869tgTbPYnsha",
                                :Size 1728,
                                :Type 2}
                               {:Name "readme",
                                :Hash "QmPZ9gcCEpqKTo6aq61g2nXGUhM4iCL3ewB6LDXZCtioEB",
                                :Size 1102,
                                :Type 2}
                               {:Name "security-notes",
                                :Hash "QmTumTjvcYCAvRRwQ8sDRxh8ezmrcr88YFU7iYNroGGTBZ",
                                :Size 1027,
                                :Type 2}]}]}))
                ))))
