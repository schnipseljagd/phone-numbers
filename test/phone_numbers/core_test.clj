(ns phone-numbers.core-test
  (:require [clojure.test :refer :all]
            [phone-numbers.core :refer :all]))

(def list-ok [["Bob" "91125426"] ["Alice" "97625992"]])
(def list-ok-with-three-elements
  [["Bob" "91125426"]
   ["Alice" "97625992"]
   ["Marc" "93456789"]])
(def list-fail-1 [["Bob" "91125426"] ["Emergency" "911"]])
(def list-fail-2 [["Emergency" "911"] ["Bob" "91125426"]])
(def list-fail-with-three-elements
  [["Bob" "91125426"]
   ["Marc" "93456789"]
   ["Emergency" "911"]])

(defn are-prefix? [[_ string1] [_ string2]]
  (or (clojure.string/starts-with? string1 string2)
      (clojure.string/starts-with? string2 string1)))

(defn debug [a b x]
  (when x
    (prn a ":" b))
  x)

(defn is-consistent [phone-list]
  (cond
    (empty? phone-list) nil
    (= (count phone-list) 1) true
    :else (->> (for [a phone-list
                     b phone-list]
                 (when-not (= a b)
                   (debug a b (are-prefix? a b))))
               (filter true?)
               (empty?))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  DATA
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-phone-data [file]
  (->> (slurp file)
       (clojure.string/split-lines)
       (drop 1)
       (map #(clojure.string/split % #","))
       (map (fn [[name phone-number]]
              [name (clojure.string/replace phone-number #"[^0-9]" "")]))
       (set)
       (into [])))

(def phone-list-1
  (read-phone-data "phone_data.txt"))

(def phone-list-2
  (read-phone-data "phone_data_10000.txt"))

(def phone-list-3
  (read-phone-data "phone_data_65535.txt"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  TESTS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest test-with-empty-list
  (is (nil? (is-consistent []))))

(deftest test-with-one-element-list
  (is (true? (is-consistent [["Bob" "911"]]))))

(deftest test-with-consistent-list
  (is (true? (is-consistent list-ok))))

(deftest test-with-non-consistent-list
  (is (false? (is-consistent list-fail-1))))

(deftest test-with-non-consistent-list-swapped
  (is (false? (is-consistent list-fail-2))))

(deftest test-with-consistent-list-of-three-elements
  (is (true? (is-consistent list-ok-with-three-elements))))

(deftest test-with-non-consistent-list-with-three-elements
  (is (false? (is-consistent list-fail-with-three-elements))))

(deftest test-with-phone-list-1
  (is (true? (is-consistent phone-list-1))))

(deftest test-with-phone-list-2
  (is (false? (is-consistent phone-list-2))))

(deftest test-with-phone-list-3
  (is (false? (is-consistent phone-list-3))))

