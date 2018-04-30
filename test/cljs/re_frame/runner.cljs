(ns re-frame.runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [re-frame.ipfs-fx-test]))

(doo-tests 're-frame.ipfs-fx-test)
