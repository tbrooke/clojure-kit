(ns io.prismic.structured-test
  (:require [clojure.test :refer :all]
            [io.prismic.test-utils :refer :all]
            [io.prismic.api :refer :all]))

(def micro (get-api "https://micro.prismic.io/api"))
(def lbc (get-api "https://lesbonneschoses.prismic.io/api"))
(defn- resolver [link]
  (str "http://localhost/" (.getType link) "/" (.getId link)))

(deftest render-structured

  (testing "render simple structured text"
    (let [fragment (-> (json-mock "structured_text_linkfile.json") (.get "value") io.prismic.Fragment$StructuredText/parse)
          expected "<p><a href=\"https://prismic-io.s3.amazonaws.com/annual.report.pdf\">2012 Annual Report</a></p><p><a href=\"https://prismic-io.s3.amazonaws.com/annual.budget.pdf\">2012 Annual Budget</a></p><p><a href=\"https://prismic-io.s3.amazonaws.com/vision.strategic.plan_.sm_.pdf\">2015 Vision &amp; Strategic Plan</a></p>"]
      (is (= expected (render fragment resolver))))))

