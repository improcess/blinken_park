(ns blinken_park.coren
  (:use
   [overtone.live :only [defsynth]]
   [clojure.contrib.seq-utils :only [indexed]]
   [polynome.spirit-worker :only [spirit elicit]])

  (:require [polynome.core :as poly])
  (:require [overtone-contrib.time :as con]))

(poly/register "/dev/tty.usbserial-m64-0790")

(def spawn-rate (atom 1))
(def ttl (atom 1))

(def spirits
  (zipmap (poly/coords) (repeatedly spirit)))

(defn summon
  [x y]
  (let [flash #(do (poly/led-on x y) (Thread/sleep (* 250 @ttl)) (poly/led-off x y))
        spirit (get spirits [x y])]
    (elicit spirit flash)))

(defn run
  []
  (loop []
    (summon (poly/rand-x) (poly/rand-y))
    (Thread/sleep (* 200 @spawn-rate))
    (recur)))

(swap! ttl (fn [_] 1))
(swap! spawn-rate (fn [_] 0.1))

(run)

