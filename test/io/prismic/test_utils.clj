(ns io.prismic.test-utils
  (:require [clojure.test :refer :all])
  )

(defn pp [arg] (prn arg) arg)

(defn ^com.fasterxml.jackson.databind.JsonNode json-mock [file]
  (let [mapper (com.fasterxml.jackson.databind.ObjectMapper.)]
    (.readTree mapper (slurp (str "resources/mock/" file)))
    ))
