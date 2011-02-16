(ns nl.zeekat.reflect-map
  (:use [clojure.contrib.string :only [split join]]))

(defn- upcase-first
  "uppercase the first letter of the string"
  [^String s]
  (apply str (Character/toUpperCase (first s)) (rest s)))

(defn- to-camel-case
  "convert a dashed-name to a camelCaseName"
  [^String name]
  (let [[first-word & other-words] (split #"-" name)] 
    (apply str first-word
           (map upcase-first other-words))))

(defn- to-dashed
  "convert a camelCaseName to a dashed-name"
  [^String name]
  (apply str (mapcat #(if (Character/isUpperCase %)
                        ["-" (Character/toLowerCase %)]
                        [%]) (seq name))))


