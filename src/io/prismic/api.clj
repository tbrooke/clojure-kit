(ns io.prismic.api
  (:use [clojure.core.match :only (match)])
  (:require
    [clojure.algo.generic.functor :as gf]))

(defn- authorization-needed [oauth-url]
  (ex-info "You need to provide an access token to access this repository" {:type "AuthorizationNeeded" :oauth-url oauth-url}))

(defn- invalid-token [oauth-url]
  (ex-info "The provided access token is either invalid or expired" {:type "InvalidToken" :oauth-url oauth-url}))

(defn- unexpected-error [msg]
  (ex-info msg {:type "UnexpectedError"}))

; API

(defn get-api
  ([url] (io.prismic.Api/get url nil))
  ([url token] (io.prismic.Api/get url token))
  )

(defn get-form [api form] (.getForm api (name form)))
(defn get-refs [api] (.getRefs api))
(defn get-ref [api ref] (.getRef api (name ref)))
(defn get-bookmark [api key] (-> (.getBookmarks api) (.get (name key))))
(defn master-ref [api] (-> api .getMaster .getRef))

(defn oauth-initiate [api] (.getOAuthInitiateEndpoint api))

(defn search
  ([api form q] (search api (master-ref api) form q))
  ([api ref form q]
    (-> api
      (.getForm (name form))
      (.ref (name ref))
      (.query q)
      .submit
      )
    )
  )

(defn get-by-id
  ([api id] (get-by-id api (master-ref api) id))
  ([api ref id]
    (-> api
      (search ref :everything (str "[[:d = at(document.id, \"" id "\")]]"))
      .getResults
      first
      )
    )
  )

(defn get-by-bookmark
  ([api name] (get-by-id api (get-bookmark api name)))
  ([api ref name] (get-by-id api ref (get-bookmark api name)))
  )

; Response

(defn results-size [response] (.getTotalResultsSize response))

(defn results [response] (.getResults response))

; Documents

(defn get-fragment ([document frag] (.get document (name frag))))

(defn get-fragments ([document frag] (.getAll document (name frag))))

(defn get-text ([document frag] (.getText document (name frag))))

(defn get-link ([document frag] (.getLink document (name frag))))

(defn get-structured-text ([document frag] (.getStructuredText document (name frag))))

(defn get-slug ([fragment] (.getSlug fragment)))
