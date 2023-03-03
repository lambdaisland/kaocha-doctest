
(ns kaocha.type.doctest-test
  (:require [clojure.test :refer [deftest testing is]]
            [kaocha.type.doctest :as doctest]))

(deftest test-doctest-parsing
  (testing "simple forms are extracted"
    (is (= (doctest/extract-doctests
             "some docstring
             (+ 1 1)
             ;; => 2")
           [['(+ 1 1) 2]])))
  (testing "nested forms are extracted"
    (is (= (doctest/extract-doctests 
             "some docstring
             (+ (* 2 2) 1)
             ;; => 5")
           [['(+ (* 2 2) 1) 5]])))
  (testing "forms with data structure literals are extracted"
    (is (= (doctest/extract-doctests 
             "some docstring
             (map inc [1 2 3])
             ;; => (2 3 4)") 
           [['(map inc [1 2 3]) '(2 3 4)]])))
  (testing "parentheticals as a part of ordinary prose text are not extracted"
    (is (= (doctest/extract-doctests
             "some forms, like (map inc [1 2 3]), include functions as function arguments.
             (2 3 4)")
           [])))
  (testing "'arrows' ('=>') as a part of ordinary prose text are not extracted"
    (is (= (doctest/extract-doctests
             "foo => bar")
           []))))
