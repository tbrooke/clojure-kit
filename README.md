## Clojure development kit for prismic.io

[![Clojars Project](http://clojars.org/prismic/latest-version.svg)](http://clojars.org/prismic)

## Installation

`prismic` is available as a Maven artifact from [Clojars](http://clojars.org/prismic):

```clojure
[prismic "1.3.1"]
```

### Try it from REPL

```
git clone https://github.com/prismicio/clojure-kit
cd clojure-kit
lein repl

user=> (require '[io.prismic.api :refer :all])

user=> (def prismic (get-api "https://lesbonneschoses.prismic.io/api"))

user=> (def stores (get-by-bookmark prismic :stores))

user=> (.getSlugs stores)
["dont-be-a-stranger"]

user=> (render (get-fragment stores :article.image) link-resolver)
"<img alt=\"\" src=\"https://prismic-io.s3.amazonaws.com/lesbonneschoses/946cdd210d5f341df7f4d8c7ec3d48adbf7a9d65.jpg\" width=\"1500\" height=\"500\" />"
```

### Continuously run tests while developing

```
lein test-refresh
```
