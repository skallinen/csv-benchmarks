(ns csv-benchmarks.core)

(require '[clojure.java.shell :refer [sh]])
(sh "bash" "-c" "kill $(ps aux | grep 'library(Rserve)' | awk '{print $2}')")

(r/discard-all-sessions)

(require '[clojisr.v1.r :as r :refer
           [r eval-r->java r->java java->r java->clj java->naive-clj clj->java
            r->clj clj->r ->code r+ colon]]
         '[clojisr.v1.require :refer [require-r]]
         '[clojisr.v1.robject :as robject]
         '[clojisr.v1.rserve :as rserve])


(require-r '[utils :refer [read-csv write-csv]])
(require-r '[dplyr :refer [rename left_join]])
(require-r '[data.table :refer [fread merge]])



(require
 '[tech.ml.dataset :as d]
 '[libpython-clj.python :as py :refer [py. py.. py.-]]
 '[libpython-clj.require :refer [require-python]])


;; # The box used to do the tests on



;; <pre><code>processor       : 0
;; vendor_id       : GenuineIntel
;; cpu family      : 6
;; model           : 158
;; model name      : Intel(R) Xeon(R) CPU E3-1275 v6 @ 3.80GHz
;; stepping        : 9
;; microcode       : 0xb4
;; cpu MHz         : 4065.930
;; cache size      : 8192 KB
;; physical id     : 0
;; siblings        : 8
;; core id         : 0
;; cpu cores       : 4
;; apicid          : 0
;; initial apicid  : 0
;; fpu             : yes
;; fpu_exception   : yes
;; cpuid level     : 22
;; wp              : yes
;; flags           : fpu vme de pse tsc msr pae mce cx8 apic
;;                   sep mtrr pge mca cmov pat pse36 clflush dts
;;                   acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx
;;                   pdpe1gb rdtscp lm constant_tsc art arch_perfmon
;;                   pebs bts rep_good nopl xtopology nonstop_tsc cpuid
;;                   aperfmperf pni pclmulqdq dtes64 monitor ds_cpl vmx
;;                   smx est tm2 ssse3 sdbg fma cx16 xtpr pdcm pcid sse4_1
;;                   sse4_2 x2apicmovbe popcnt tsc_deadline_timer aes
;;                   xsave avx f16c rdrand lahf_lm abm 3dnowprefetch
;;                   cpuid_fault epb invpcid_single pti ssbd ibrs
;;                   ibpb stibp tpr_shadow vnmi flexpriority ept
;;                   vpid ept_ad fsgsbase tsc_adjust bmi1 hle avx2
;;                   smep bmi2 erms invpcid rtm mpx rdseed adx smap
;;                   clflushopt intel_pt xsaveopt xsavec xgetbv1
;;                   xsaves dtherm ida arat pln pts hwp hwp_notify
;;                   hwp_act_window hwp_epp md_clear flush_l1d
;; bugs            : cpu_meltdown spectre_v1 spectre_v2
;;                   spec_store_bypass l1tf mds swapgs taa itlb_multihit
;; bogomips        : 7602.45
;; clflush size    : 64
;; cache_alignment : 64
;; address size    : 39 bits physical, 48 bits virtual</pre></code>
;;  
;;  



;; ## free -m

;;<pre><code>               total
;; Mem:          64110
;; Swap:         32734</pre></code>
;;  
;; <br/><br/>  
;; ---


;; ## Clojure and JVM version
;; CIDER 0.24.0snapshot (package: 20200101.839), nREPL 0.6.0
;; Clojure 1.10.0, Java 1.8.0_242

;; ## JVM configuration
;; <pre><code>:jvm-opts ["-Xmx60G"]</pre></code>
;;  
;;  
;; <br/><br/>  
;; ---


;; # The test data
;; - 1 Million rows  
;; - 8 columns  

(def path "/home/user/1M.csv")


;; ## Small example sample

;; Head (simulated):
;; <pre><code>18264,1.9,2019-07-21 00:00:00+00,1.597,6030338631003,,1292022,
;; 1953862,0.15,2019-07-21 00:00:00+00,1,1020933930002,,605393134,
;; 8381411,1.3,2019-07-21 00:00:00+00,0.818,3000813730008,,808898093,
;; 2648956,4.65,2019-07-21 00:00:00+00,1,6109320034462,,48543142,
;; 998993,1.58,2019-07-21 00:00:00+00,1,6616697116919,,7197072,
;; 1571692,2.55,2019-07-21 00:00:00+00,1,5409500045230,,40529314,
;; 4437197,2.49,2019-07-21 00:00:00+00,1,8738008098490,,92533132,
;; 392738,8,2019-07-21 00:00:00+00,3,1310403111190,,2193643,
;; 6430911,0.7,2019-07-21 00:00:00+00,1,4451402197108,,995233414,</pre></code>
;; <br/><br/>  
;; ---



;; # Benchmarking lib
(require '[criterium.core :as b])
;;  
;;  
;; <br/><br/>  
;; ---




;; ## clojure.data.csv JVM
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(time
 (with-open [reader (io/reader path)]
   (doall
    (csv/read-csv reader))))

;; ## Single pass (time)
;;  "Elapsed time: 2397.786306 msecs"  
;;
;; ## Using criterium.core/bench 
;; bench runs out of memory  
;;  
;;  
;; <br/><br/>  
;; ---




;; # tech.ml.dataset JVM
(b/bench
 (d/->dataset path))
;; ## Single pass with (time):
;;  "Elapsed time: 1096.673491 msecs"
;;
;; ## Using criterium.core/bench 
;; <pre><code>
;; Evaluation count : 120 in 60 samples of 2 calls.
;; Execution time mean : 556.801306 ms
;; Execution time std-deviation : 1.482398 ms
;; Execution time lower quantile : 554.401045 ms ( 2.5%)
;; Execution time upper quantile : 560.090302 ms (97.5%)
;; Overhead used : 1.436580 ns </pre></code>
;;  
;;  
;; <br/><br/>  
;; ---



;; # semantic-csv JVM
(require '[semantic-csv.core :as sc])

(b/bench
 (sc/slurp-csv path))
;; ## Single pass with (time)
;;  "Elapsed time: 6094.983367 msecs"
;;
;; ## Using criterium.core/bench 
;; <pre><code>Evaluation count : 60 in 60 samples of 1 calls.
;; Execution time mean : 13.441014 sec
;; Execution time std-deviation : 1.747917 sec
;; Execution time lower quantile : 12.720849 sec ( 2.5%)
;; Execution time upper quantile : 18.453125 sec (97.5%)
;; Overhead used : 1.436580 ns
;; Found 11 outliers in 60 samples (18.3333 %)
;; low-severe    1 (1.6667 %)
;; low-mild      10 (16.6667 %)
;; Variance from outliers : 80.6396 % Variance is severely
;; inflated by outliers</pre></code>
;;  
;;  
;; <br/><br/>  
;; ---


;; # ultra-csv library JVM
;;
(require '[ultra-csv.core :as ultra])
(time (ultra/read-csv path))
(b/bench (ultra/read-csv path))


;; ## Single pass:
;; "Elapsed time: 38.625054 msecs"  
;;
;; ## Using criterium.core/bench 
;; <pre><code>"Elapsed time: 38.625054 msecs"
;; Evaluation count : 31140 in 60 samples of 519 calls.
;; Execution time mean : 1.918202 ms
;; Execution time std-deviation : 8.073095 µs
;; Execution time lower quantile : 1.912235 ms ( 2.5%)
;; Execution time upper quantile : 1.942727 ms (97.5%)
;; Overhead used : 1.252094 ns
;; Found 8 outliers in 60 samples (13.3333 %)
;; low-severe    3 (5.0000 %)
;; low-mild      5 (8.3333 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated by outliers </pre></code>
;;  
;;  
;; <br/><br/>  
;; ---




;; # R implementation on JVM base-r read.csv JVM
(require 'clojisr.v1.renjin)

(defn renjin-read-csv [path]
(-> (r ['read.csv path] :session-args {:session-type :renjin})
    r/r->java
    (r/java->clj :session-args {:session-type :renjin})))

(b/bench (renjin-read-csv path))
;; ## Single pass with (time)
;;  "Elapsed time: 3818.361591 msecs"
;;
;; ## Using criterium.core/bench 
;; <pre><code>Evaluation count : 60 in 60 samples of 1 calls.
;; Execution time mean : 2.463850 sec
;; Execution time std-deviation : 39.353008 ms
;; Execution time lower quantile : 2.446292 sec ( 2.5%)
;; Execution time upper quantile : 2.560234 sec (97.5%)
;; Overhead used : 1.436580 ns
;; Found 5 outliers in 60 samples (8.3333 %)
;; low-severe    2 (3.3333 %)
;; low-mild      3 (5.0000 %)
;; Variance from outliers : 1.6389 % Variance is slightly inflated
;; by outliers</pre></code>
;;  
;;  



;; # Base R read.csv Rserve (R process)
(b/bench
 (->
  (read-csv path
            :header false
            :stringsAsFactors false)
  r->clj))
;; ## Single pass with (time) "Elapsed time: 5567.814253 msecs"
;; ## Using criterium.core/bench 
;; <pre><code>Evaluation count : 60 in 60 samples of 1 calls.
;; Execution time mean : 4.558503 sec
;; Execution time std-deviation : 44.128313 ms
;; Execution time lower quantile : 4.485932 sec ( 2.5%)
;; Execution time upper quantile : 4.639285 sec (97.5%)
;; Overhead used : 1.435983 ns</pre></code>
;;  
;;  
;; <br/><br/>  
;; ---



;; # data.table library on R.
;; (usually better memory management and faster than base-r) fread
(b/bench
 (->
  (fread path
         :header false
         :stringsAsFactors false)
  r->clj))

;; ## Single pass measured with (time)
;;  Elapsed time: 3507.413101 msecs
;;
;; ## Using criterium.core/bench 
;; <pre><code>Evaluation count : 60 in 60 samples of 1 calls.
;; Execution time mean : 3.410649 sec
;; Execution time std-deviation : 32.014570 ms
;; Execution time lower quantile : 3.358565 sec ( 2.5%)
;; Execution time upper quantile : 3.479392 sec (97.5%) Overhead used : 1.435819 ns </pre></code>
;; <br/><br/>  
;; ---


;; # Pandas PYTHON libpython-clj interop and tech.ml.dataset
(require-python '[pandas :as pd])

(time (pd/read_csv(path)))
;; getting error "java.lang.String cannot be cast to clojure.lang.IFn"


