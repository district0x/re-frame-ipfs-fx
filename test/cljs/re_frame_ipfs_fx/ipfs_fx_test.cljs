(ns re-frame-ipfs-fx.ipfs-fx-test
  (:require
   [cljs.test :refer [deftest testing is async]]
   [cljs.test :as t]
   [district0x.re-frame-ipfs-fx.ipfs-fx :as core]
   )
  )

(deftest defnils-test []
  (is (= 1 2))
  ;; (is (= (core/nil-patched-defns 'test ['data ['options] ['callback] ['quack] 'mordata]) nil))
  )
