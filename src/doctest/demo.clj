(ns doctest.demo)


(defn sum
  "This function computes a sum

  (sum 1 2 3)
  ;; => 6
  (sum 4 5 6)
  ;; => 10
  "
  [& args]
  (apply + args))
