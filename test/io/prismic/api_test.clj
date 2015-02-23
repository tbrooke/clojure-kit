(ns io.prismic.api-test
  (:require [clojure.test :refer :all]
            [io.prismic.api :refer :all]))

(def lbc (get-api "https://lesbonneschoses.prismic.io/api"))
(def micro (get-api "https://micro.prismic.io/api"))
(def test-endpoint "https://test.prismic.io/api")
(def test-repo
  (let [token "MC5VMFB1N0FFQUFDNEEyZF82.77-9AFfvv73vv71D77-977-977-977-9Unk2au-_ve-_vU7vv73vv73vv70u77-977-977-977-9bO-_vQbvv70dKwY"]
    (get-api test-endpoint token)))

(defn- is= [a b] (is (= a b)))

(deftest create
  (testing "get api"
    (is= (oauth-initiate lbc) "https://lesbonneschoses.prismic.io/auth"))

  (testing "get secured api"
    (is= (oauth-initiate test-repo) "https://test.prismic.io/auth")))

;(deftest oauth-exceptions
;  (testing "invalid token"
;    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"The provided access token is either invalid or expired" (get-api test-endpoint "abcd")))))

;  (testing "authorization needed"
;    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"You need to provide an access token to access this repository" (get-api test-endpoint))))

(deftest api-functions

  (testing "get bookmark"
    (is= (get-bookmark lbc :jobs) "UlfoxUnM0wkXYXbd"))

  (testing "get ref"
    (is= (-> (get-ref lbc :Master) .getRef) "UlfoxUnM08QWYXdl")))

(deftest search-documents

  (testing "count blog posts"
    (is= (results-size (search lbc :blog nil)) 6))

  (testing "fulltext search"
    (let [query "[[:d = fulltext(my.job-offer.name, \"Pastry dresser\")]]"
          response (search lbc :everything query)]
      (is= (-> (results response) first .getId) "UlfoxUnM0wkXYXbh")))

  (testing "fulltext search in a future release"
    (let [query "[[:d = fulltext(my.article.title, \"release\")]]"
          response (search test-repo "VDP8USsAACsAg3jK" :everything query)]
      (is= (-> (results response) first .getId) "U0VaEwEAADMA2kLw")))

  (testing "find by id"
    (let [id "UlfoxUnM0wkXYXbV" doc (get-by-id lbc id)]
      (is= (.getId doc) id)))

  (testing "find by id a document in a future release"
    (let [id "U0VaMQEAADYA2kMz"
          doc (get-by-id test-repo "VDP8USsAACsAg3jK" id)]
      (is= (.getId doc) id)))

  (testing "find by bookmark"
    (let [doc (get-by-bookmark lbc :stores)
          text (get-text doc :article.title)]
      (is= text "Don't be a stranger!")))
)

;  (testing "find by bookmark in a future released"
;    (let [doc (get-by-bookmark test-repo "VDP8USsAACsAg3jK" :home)
;          text (-> (get-fragment doc :title) :value first :text)]
;      (is= text "Released in the future")))

(deftest select-fragments
  (let [job (get-by-id lbc "UlfoxUnM0wkXYXbs")
        post (get-by-id micro "UrDcEAEAAKUbpbND")]

    (testing "get image"
      (is= (type (get-fragment job :job-offer.name)) io.prismic.Fragment$StructuredText))

    (testing "get one link"
      (is= (get-slug (get-fragment job :job-offer.location)) "new-york-fifth-avenue"))

    (testing "get all links"
      (let [links (get-fragments job :job-offer.location)]
        (is= (get-slug (first links)) "new-york-fifth-avenue")
        (is= (get-slug (second links)) "tokyo-roppongi-hills")))

    (testing "get structured text"
      (let [f (.getDocs (get-fragment post :docchapter.docs))]
        (is= "UrDejAEAAFwMyrW9" (-> f first (get-link :linktodoc) .getId))
        (is= io.prismic.Fragment$StructuredText (-> f first (get-fragment :desc) type))
        (is= "UrDmKgEAALwMyrXA" (-> f second (get-link :linktodoc) .getId))))

    ))
