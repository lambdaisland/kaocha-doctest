(ns kaocha.type.doctest
  (:refer-clojure :exclude [symbol])
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as spec]
            [clojure.test :as t]
            [kaocha.ns :as ns]
            [kaocha.core-ext :refer :all]
            [kaocha.hierarchy :as hierarchy]
            [kaocha.load :as load]
            [kaocha.specs]
            [kaocha.test-suite :as test-suite]
            [kaocha.testable :as testable]
            [kaocha.type.ns :as type.ns]
            [lambdaisland.tools.namespace.find :as ctn-find])
  (:import java.io.StringReader
           java.io.PushbackReader))

(require 'kaocha.type.clojure.test
         'kaocha.type.ns)

(set! *warn-on-reflection* true)

(defn ns-testable [ns-name]
  {:kaocha.testable/type :kaocha.type.doctest/ns
   :kaocha.testable/id   (keyword (str "doctest:" ns-name))
   :kaocha.testable/desc (str ns-name " doctests")
   :kaocha.ns/name       ns-name})

(defmethod testable/-load :kaocha.type/doctest [testable]
  (-> testable
      (load/load-test-namespaces ns-testable)
      (testable/add-desc "doctests")))

(defmethod testable/-run :kaocha.type/doctest [testable test-plan]
  (test-suite/run testable test-plan))

(defn skip [^PushbackReader reader pred]
  (loop []
    (let [ch (.read reader)]
      (when-not (= -1 ch)
        (if (pred (char ch))
          (recur)
          (.unread reader ch))))))

(defn consume [^PushbackReader reader s]
  (loop [[x & xs] s]
    (if x
      (let [ch (.read reader)]
        (if (= (int x) ch)
          (recur xs)
          (do
            (.unread reader ch)
            false)))
      true)))

(defn extract-doctests [doc]
  (let [reader (PushbackReader. (StringReader. doc))]
    (loop [forms []]
      (let [ch (.read reader)]
        (if (= -1 ch)
          forms
          (if (= (int \() ch)
            (let [form (try
                         (.unread reader ch)
                         (read reader)
                         (catch Exception e))]
              (if form
                (do
                  (skip reader #{\space \newline \return \tab \;})
                  (if (consume reader "=>")
                    (let [result (try
                                   (read reader)
                                   (catch Exception e))]
                      (if result
                        (recur (conj forms [form result]))
                        (recur forms)))
                    (recur forms)))
                (recur forms)))
            (recur forms)))))))

(comment
  (extract-doctests "

Some explanation

(foo-bar 1 2 3)
;;=> 6

"))

(defmethod testable/-load :kaocha.type.doctest/ns [testable]
  (let [ns-name         (:kaocha.ns/name testable)
        ns-obj          (ns/required-ns ns-name)
        ns-meta         (meta ns-obj)]
    (assoc testable
           :kaocha.testable/meta (meta ns-obj)
           :kaocha.ns/ns ns-obj
           :kaocha.test-plan/tests
           (->> ns-obj
                ns-interns
                (keep (fn [[sym var]]
                        (when-let [forms (some-> var meta :doc extract-doctests seq)]
                          [sym var forms])))
                (sort-by first)
                (map (fn [[sym var forms]]
                       (let [test-name (symbol (str "doctest:" ns-name) (str sym))]
                         {:kaocha.testable/type :kaocha.type/var
                          :kaocha.testable/id   (keyword test-name)
                          :kaocha.testable/meta (meta var)
                          :kaocha.testable/desc (str sym)
                          :kaocha.var/name      test-name
                          :kaocha.var/var       var
                          :kaocha.var/test      (eval
                                                 `(do
                                                    (in-ns '~ns-name)
                                                    (fn []
                                                      ~@(for [[test result] forms]
                                                          `(t/testing ~(pr-str test)
                                                             (t/is (~'= ~result ~test)))))))})))))))

(spec/def :kaocha.type/doctest (spec/keys :req [:kaocha/test-paths
                                                :kaocha/ns-patterns]))
(spec/def :kaocha.type.doctest/ns (spec/keys  :req [:kaocha.testable/type
                                                    :kaocha.testable/id
                                                    :kaocha.ns/name]
                                              :opt [:kaocha.ns/ns
                                                    :kaocha.test-plan/tests]))


(hierarchy/derive! :kaocha.type/doctest :kaocha.testable.type/suite)
(hierarchy/derive! :kaocha.type.doctest/ns :kaocha.testable.type/group)
(hierarchy/derive! :kaocha.type.doctest/ns :kaocha.type/ns)
