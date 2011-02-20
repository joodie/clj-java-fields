(ns nl.zeekat.java.fields.test
  (:use clojure.test
        nl.zeekat.java.fields)
  (:require [clojure.contrib.logging :as log]))

(deftype TestType
    [field1 field2])

(deftest dynamic-fields
  (is (= {:field1 1 :field2 2}
         (fields (TestType. 1 2)))))

(deftype OtherType
    [a b])

(def-fields rec-map TestType)

(deftest static-fields
  (is (= {:field1 1 :field2 2}
         (rec-map (TestType. 1 2)))))

(deftest type-checks
  (is (thrown? ClassCastException
               (rec-map (OtherType. 1 2)))))



