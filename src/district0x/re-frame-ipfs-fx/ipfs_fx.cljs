(ns district0x.re-frame-ipfs-fx.ipfs-fx
  (:require
   [cljs-ipfs-api.core :as ipfs-core]
   [cljs-ipfs-api.utils :as ipfs-utils]
   [re-frame.core :refer [reg-fx dispatch console reg-event-db reg-event-fx]]))

(reg-fx
 :ipfs/init
 (fn ipfs-init [config]
   (ipfs-core/init-ipfs (or config {}))))

(reg-fx
 :ipfs/call
 (fn ipfs-call [{:keys [:fn :args :on-success :on-error]}]
   (ipfs-utils/api-call ipfs-core/*ipfs-instance*
                        fn
                        args
                        (merge
                         {}
                         (when (or on-success on-error)
                           {:callback (fn ipfs-callback [err data]
                                        (if err
                                          (when on-error
                                            (dispatch (vec (concat on-error [err]))))
                                          (when on-success
                                            (dispatch (vec (concat on-success [err]))))))})))))
