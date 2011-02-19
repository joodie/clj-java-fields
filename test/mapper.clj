(ns mapper
  (:use clojure.test
        nl.zeekat.reflect-map)
  (:require [clojure.contrib.logging :as log]))

(set! *warn-on-reflection* true)

(deftype TestRecord
    [field1 field2])

(deftype OtherType
    [a b])

(def-field-mapper rec-map TestRecord)



(defn bla
  []
  (rec-map ^OtherType (OtherType. 1 2)))

(deftest test-mapper
  (is (= {:field1 1 :field2 2}
         (rec-map (TestRecord. 1 2)))
      "mapping works"))


(defmacro throws-compiler-exception?
  "Check if form throws a compiler exception caused by
an exception of class cause"
  [cause form]
  `(try (eval '~form)
        false
        (catch clojure.lang.Compiler$CompilerException e#
          (or (= (class (.getCause e#))  ~cause)
              (do (println (str "Actual cause: " (.getCause e#)))
                  false)))))

(deftest test-type-checks
  (is (throws-compiler-exception? ClassCastException
                                  (mapper/rec-map (mapper.OtherType. 1 2)))
      "type hinting"))

