(ns blinken_park.core
  (:use
   overtone.live
   [clojure.contrib.seq-utils :only [indexed]]
   [polynome.spirit-worker :only [spirit elicit]])

  (:require [polynome.core :as poly])
  (:require [overtone-contrib.core :as contrib]))

(contrib/boot-and-wait)
(poly/register "/dev/tty.usbserial-m64-0790")

(def spawn-rate (atom 1))
(def ttl (atom 1))
(def continue  (atom true))

(defsynth beep [freq-mul 1 duration 1]
  (* (sin-osc (* 25 (+ 1 freq-mul))) (env-gen (perc 0.01 duration) 1 1 0 1 :free)))

(defn play
  [freq-mul time-ms]
  (let [time-s (/ time-ms 1000.0)]
    (beep freq-mul time-s)))

(def spirits
  (zipmap (poly/coords) (repeatedly spirit)))

(defn summon
  [x y]
  (let [length (* 250 @ttl)
        flash #(do (poly/led-on x y) (play (poly/button-id x y) length) (Thread/sleep length) (poly/led-off x y))
        spirit (get spirits [x y])]
    (elicit spirit flash)))

(defn run
  []
  (loop []
    (if @continue
      (do
        (summon (poly/rand-x) (poly/rand-y))
        (Thread/sleep (* 200 @spawn-rate))
        (recur)))))

(defn modify-params
  [x y]
  (let [new-spawn-rate (/ (+ 1 x) 4.0)
        new-ttl (+ 1 y)]
    (swap! ttl (fn [_] new-ttl))
    (swap! spawn-rate (fn [_] new-spawn-rate))))

(poly/on-press (fn [x y] (modify-params x y)))

(defn start
  []
  (swap! continue (fn [_] true))
  (run))

(defn stop
  []
  (swap! continue (fn [_] false)))

(stop)
(start)

;;;;;;
;(swap! ttl (fn [_] 1))
;(swap! spawn-rate (fn [_] 2))
;(swap! continue (fn [_] false))



