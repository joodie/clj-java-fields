(ns nl.zeekat.reflect-map
  (:require [clojure.contrib.string :as string])
  (:import java.lang.reflect.Modifier
           java.lang.reflect.Field))

(defn- upcase-first
  "uppercase the first letter of the string"
  [^String s]
  (apply str (Character/toUpperCase (first s)) (rest s)))

(defn- to-camel-case
  "convert a dashed-name to a camelCaseName"
  [^String name]
  (let [[first-word & other-words] (string/split #"-" name)] 
    (apply str first-word
           (map upcase-first other-words))))

(defn- to-dashed
  "convert a camelCaseName to a dashed-name"
  [^String name]
  (apply str (Character/toLowerCase (first name))
         (mapcat #(if (Character/isUpperCase %)
                        ["-" (Character/toLowerCase %)]
                        [%]) (rest name))))

(defn- public-fields
  "return the public, non-static fields of a class"
  [^Class class]
  (filter #(let [m (.getModifiers %)]
             (and (not (= 0 (bit-and Modifier/PUBLIC m)))
                  (= 0 (bit-and Modifier/STATIC m))))
          (.getDeclaredFields class)))

(defn- field-to-keyword
  [^Field f]
  (-> f .getName to-dashed keyword))

(defn- field-to-access-symbol
  [^Field f]
  (symbol (str "." (.getName f))))

(defn map-fields
  "Dynamically convert the public fields of an object to a map"
  [object]
  (let [fields (public-fields (class object))]
    (zipmap (map field-to-keyword fields)
            (map #(.get % object) fields))))

(defmacro def-field-mapper
  [name class]
  "build a static mapper from class to hash-map."
  (let [cls (eval class)]
    `(defn ~name [~(with-meta 'object {:tag class})]
       ~(apply hash-map
               (mapcat #(vector (field-to-keyword %)
                                (list (field-to-access-symbol %) 'object))
                       (public-fields cls))))))


