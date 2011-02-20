(ns nl.zeekat.java.fields
  (:require [nl.zeekat.identifiers :as identifiers])
  (:import java.lang.reflect.Modifier
           java.lang.reflect.Field))

(defn- public-fields
  "return the public, non-static fields of a class"
  [^Class class]
  (filter #(let [m (.getModifiers %)]
             (and (not (= 0 (bit-and Modifier/PUBLIC m)))
                  (= 0 (bit-and Modifier/STATIC m))))
          (.getDeclaredFields class)))

(defn- field-to-keyword
  [^Field f]
  (-> f .getName identifiers/lisp-name keyword))

(defn- field-to-access-symbol
  [^Field f]
  (symbol (str "." (.getName f))))

(defn fields
  "Dynamically convert the public, non-static fields of an object to a map. Uses reflection at run time."
  [object]
  (let [fields (public-fields (class object))]
    (zipmap (map field-to-keyword fields)
            (map #(.get % object) fields))))

(defmacro def-fields
  [n klass]
  "Build a function to convert an object of class klass to a map.
Like `fields' but only uses the reflection API at compile time."
  (let [cls (eval klass)] ; get the class object from the symbol
    `(defn ~n [~(with-meta 'object {:tag klass})]
       ;; function body will be
       ;; {:field-name  (.fieldName object)
       ;;  :field-name2 (.fieldName2 object)
       ;;  ...}
       ~(apply hash-map
               (mapcat #(vector (field-to-keyword %)
                                (list (field-to-access-symbol %) 'object))
                       (public-fields cls))))))


