(defproject csv-benchmarks "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.7"]
                 [com.clojure-goes-fast/clj-memory-meter "0.1.0"]
                 [clj-python/libpython-clj "1.36"]
                 [scicloj/clojisr "1.0.0-BETA8-SNAPSHOT"]
                 [org.clojure/data.csv "1.0.0"]
                 [semantic-csv "0.2.1-alpha1"]
                 [criterium "0.4.5"]
                 [ultra-csv "0.2.3"]
                 [panthera "0.1-alpha.18"]
                 ]
  :repl-options {:init-ns csv-benchmarks.core}
  :jvm-opts ["-Xmx60G"])
