(ns tests.runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [tests.ipfs-fx-test]))

(enable-console-print!)

(doo-tests 'tests.ipfs-fx-test)
