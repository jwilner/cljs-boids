(ns cljs-boids.core
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def canvas-id "sky")
(def canvas-bg-color "#0C1415")
(def height-over-width 0.8)
(def boid-colors ["#FFFFFF"])
(def line-width 6)
(def min-separation 2)

(defn distance-formula [[x1 y1] [x2 y2]]
  (Math/sqrt (+ (Math/pow (- x1 x2) 2)
                (Math/pow (- y1 y2) 2))))

(defprotocol Buildable
  (build [this]))

(defprotocol Drawable
  (draw! [this]))

(defprotocol Flockable
  (distance [this other])
  (steer [this heading])
  (move [this]))

(defrecord Canvas []
  Buildable
  (build [this]
    (let [canvas-dom (.getElementById js/document canvas-id)]
      (merge this {:canvas-dom canvas-dom  
                   :width (.-width canvas-dom)
                   :height (.-height canvas-dom)
                   :bg-color canvas-bg-color
                   :context (.getContext canvas-dom "2d")}))))

(defrecord Boid [id canvas x y width heading speed mass]
  Buildable
  (build [this]
    (let [height (* width height-over-width)
          borders [[x y] [(+ x width) y]
                   [(+ x width) (+ y height)] [x (+ y height)]]]
      (merge this {:width width
                   :height height  
                   :center [(+ x (/ width 2)) (+ y (/ height 2))]
                   :heading heading
                   :speed speed
                   :mass mass 
                   :borders borders})))
  Drawable
  (draw! [{:keys [color borders angle width height] :as this} canvas] 
    (let [context (:context canvas)
          [centerX centerY] center] 
      (set! (.-strokeStyle context) color)
      (set! (.-lineWidth context) line-width)
      (.beginPath context)
      (doseq [[x y] borders]
        (.moveTo context x y)
        (.lineTo context x y))
      (.closePath context)
      (.stroke context)))

  Flockable
  (distance [{:keys [center] :as this}
             other]
    (distance-formula center (:center this)))
  (steer [this heading]
    "Needs to be deadened somehow"
    (assoc this :heading heading))
  (move [{:keys [heading speed] :as this}]))

(defn all-but-me [me flock]
  (remove (partial = (:id me)) flock))

(defn boid-headings [flock]
  (/ (reduce + (map :heading flock))
     (count flock)))

(defn boids-too-close [me flock]
  (filter #(< (distance me %) min-separation) flock))

(defn center-of-mass [flock]
 (let [mass-times-center (apply mapv + (mapv (fn [boid]
                                               (let [[x y] (:center boid)
                                                    m (:mass boid)]
                                                   [(* m x) (* m y)]))
                                              flock))  
       total-m (reduce + (mapv :mass flock))]
   (mapv (/ % total-m) mass-times-center)))
