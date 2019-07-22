(ns district0x.re-frame.ipfs-fx
  (:require
   [cljs-ipfs-api.core :as ipfs-core]
   [cljs-ipfs-api.utils :as ipfs-utils]
   [re-frame.core :as re-frame]))

(re-frame/reg-fx
 :ipfs/init
 (fn ipfs-init [config]
   (ipfs-core/init-ipfs (or config {}))))

(re-frame/reg-fx
 :ipfs/call
 (fn [{:keys [:inst :func :args :opts :on-success :on-error]}]
   (ipfs-utils/api-call (or inst @ipfs-core/*ipfs-instance*)
                        func
                        args
                        (merge {:options opts}
                               (when (or on-success on-error)
                                 {:callback (fn [err data]
                                              (if err
                                                (when on-error
                                                  (re-frame/dispatch (vec (concat on-error [err]))))
                                                (when on-success
                                                  (re-frame/dispatch (vec (concat on-success [data]))))))})))))
