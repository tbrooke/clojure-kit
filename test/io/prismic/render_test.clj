(ns io.prismic.render-test
  (:require [clojure.test :refer :all]
            [io.prismic.test-utils :refer :all]
            [io.prismic.api :refer :all]))

(def lbc (get-api "https://lesbonneschoses.prismic.io/api"))
(def public (get-api "https://test-public.prismic.io/api"))
(def micro (get-api "https://micro.prismic.io/api"))
(defn- resolver [link]
  (str "http://localhost/" (.getType link) "/" (.getId link)))

(deftest render-fragments

  (testing "render group"
    (let [doc (get-by-id micro "UrDcEAEAAKUbpbND")
          expected (str
                     "<section data-field=\"desc\"><p>Just testing another field in a group section.</p></section>\n"
                     "<section data-field=\"linktodoc\"><a href=\"http://localhost/doc/UrDejAEAAFwMyrW9\">installing-meta-micro</a></section>"
                     "<section data-field=\"linktodoc\"><a href=\"http://localhost/doc/UrDmKgEAALwMyrXA\">using-meta-micro</a></section>")]
      (is (= expected (render (get-fragment doc :docchapter.docs) resolver)))))

  (testing "render image"
    (let [doc (get-by-bookmark lbc :stores)
          html (render (get-fragment doc :article.image) resolver)
          url "https://prismic-io.s3.amazonaws.com/lesbonneschoses/946cdd210d5f341df7f4d8c7ec3d48adbf7a9d65.jpg"]
      (is (= (str "<img alt=\"\" src=\"" url "\" width=\"1500\" height=\"500\" />") html))))

  (testing "render image view"
    (let [doc (get-by-id public "Uyr9sgEAAGVHNoFZ")
          html (-> doc (get-fragment :article.illustration) (image-view :icon) (render resolver))
          url "https://prismic-io.s3.amazonaws.com/test-public/9f5f4e8a5d95c7259108e9cfdde953b5e60dcbb6.jpg"]
      (is (= (str "<img alt=\"some alt text\" src=\"" url "\" width=\"100\" height=\"100\" />") html))))

  (testing "render document link"
    (let [doc (get-by-id lbc "UlfoxUnM0wkXYXbs")
          html (render (get-fragment doc :job-offer.location) resolver)
          url "http://localhost/store/UlfoxUnM0wkXYXbc"]
      (is (= (str "<a href=\"" url "\">new-york-fifth-avenue</a>") html))))

  (testing "render web link"
    (let [doc (get-by-id public "Uy4VGQEAAPQzRDR9")
          html (render (get-fragment doc :test-link.related))
          url "https://github.com/prismicio"]
      (is (= (str "<a href=\"" url "\">" url "</a>") html))))

  (testing "render file link"
    (let [doc (get-by-id public "Uy4VGQEAAPQzRDR9")
          html (render (get-fragment doc :test-link.download))
          url "https://prismic-io.s3.amazonaws.com/test-public%2Feb14f588-07b4-4df7-be43-5b6f6383d202_ambiance-radio.m3u"]
      (is (= (str "<a href=\"" url "\">ambiance-radio.m3u</a>") html))))

  (testing "render number"
    (let [doc (get-by-id lbc "UlfoxUnM0wkXYXbF")
          html (render (get-fragment doc :product.price))]
      (is (= "<span class=\"number\">3.0</span>" html))))

  (testing "render color"
    (let [doc (get-by-id lbc "UlfoxUnM0wkXYXbF")
          html (render (get-fragment doc :product.color))]
      (is (= "<span class=\"color\">#fa001b</span>" html))))

  (testing "render date"
    (let [fragment (io.prismic.Fragment$Date. (org.joda.time.LocalDate. 2013 8 17))
          html (render fragment)]
      (is (= "<time>2013-08-17</time>" html))))

  (testing "render text"
    (let [doc (get-by-id lbc "UlfoxUnM0wkXYXbm")
          html (render (get-fragment doc :blog-post.author))]
      (is (= "<span class=\"text\">Tsutomu Kabayashi, Pastry Dresser</span>" html))))

  (testing "render document"
    (let [doc (io.prismic.Document/parse (json-mock "document.json"))
          expected
          "<section data-field=\"product.name\"><h1>Vanilla Macaron</h1></section>\n<section data-field=\"product.short_lede\"><h2>Crispiness and softness, rolled into one</h2></section>\n<section data-field=\"product.description\"><p>Experience the ultimate vanilla experience. Our vanilla Macarons are made with our very own (in-house) <strong>pure extract of Madagascar vanilla</strong>, and subtly dusted with <strong>our own vanilla sugar</strong> (which we make from real vanilla beans).</p></section>\n<section data-field=\"product.location\"></section>\n<section data-field=\"product.image\"><img alt=\"Alternative text to image\" src=\"https://wroomio.s3.amazonaws.com/lesbonneschoses/0417110ebf2dc34a3e8b7b28ee4e06ac82473b70.png\" width=\"500\" height=\"500\" /></section>\n<section data-field=\"product.allergens\"><span class=\"text\">Contains almonds, eggs, milk</span></section>\n<section data-field=\"product.price\"><span class=\"number\">3.55</span></section>\n<section data-field=\"product.flavour[0]\"><span class=\"text\">Vanilla</span></section>\n<section data-field=\"product.color\"><span class=\"color\">#ffeacd</span></section>\n<section data-field=\"product.related[0]\"><a href=\"http://localhost/product/UdUjvt_mqVNObPeO\">dark-chocolate-macaron</a></section>\n<section data-field=\"product.related[1]\"><a href=\"http://localhost/product/UdUjsN_mqT1ObPeM\">salted-caramel-macaron</a></section>\n<section data-field=\"product.testimonial_author[0]\"><h3>Chef Guillaume Bort</h3></section>\n<section data-field=\"product.testimonial_quote[0]\"><p>The taste of pure vanilla is very hard to tame, and therefore, most cooks resort to substitutes. <strong>It takes a high-skill chef to know how to get the best of tastes, and </strong><strong><em>Les Bonnes Choses</em></strong><strong>'s vanilla macaron does just that</strong>. The result is more than a success, it simply is a gastronomic piece of art.</p></section>\n<section data-field=\"product.some_timestamp\"></section>\n<section data-field=\"product.linked_images\"><p>Here is some introductory text.</p><p>The following image is linked.</p><p class=\"block-img\"><a href=\"http://google.com/\"><img alt=\"\" src=\"http://fpoimg.com/129x260\" width=\"260\" height=\"129\" /></a></p><p><strong>More important stuff</strong></p><p>One more image, this one is not linked:</p><p class=\"block-img\"><img alt=\"\" src=\"http://fpoimg.com/199x300\" width=\"300\" height=\"199\" /></p></section>"]
      (is (= (render doc resolver) expected))))

;  (testing "render select"
;    (let [fragment (-> (json-mock "select_fragment.json") (.get "value") io.prismic.Fragment$Text/parse)
;           html (render fragment)]
;      (is (= "<span class=\"text\">&amp;my &lt;value&gt; #abcde</span>" html))))

  (testing "render embed"
    (let [doc (get-by-bookmark public :links)
          html (render (get-fragment doc :test-link.embed))]
     (is (= "<div data-oembed=\"https://gist.github.com/srenault/71b4f1e62783c158f8af\" data-oembed-type=\"rich\" data-oembed-provider=\"github\"><script src=\"https://gist.github.com/srenault/71b4f1e62783c158f8af.js\"></script></div>" html))))

    )
