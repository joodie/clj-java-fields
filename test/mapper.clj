(ns mapper
  (:use clojure.test
        nl.zeekat.reflect-map)
  (:require [clojure.contrib.logging :as log]))

(set! *warn-on-reflection* true)

(deftest dynamic-fields
  (is (= {:field1 1 :field2 2}
         (rec-map (TestType. 1 2)))))

(deftype TestType
    [field1 field2])

(deftype OtherType
    [a b])

(def-fields rec-map TestType)

(deftest static-fields
  (is (= {:field1 1 :field2 2}
         (rec-map (TestType. 1 2)))))

(deftest type-checks
  (is (thrown? ClassCastException
               (rec-map (OtherType. 1 2)))))



