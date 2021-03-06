(ns labs-4-cource.carcas-mode
  (:require [labs-4-cource.affine-transformations
             :refer
             [project rotate scale translate]]
            [labs-4-cource.state-mashines :refer [get-state StateMachine]]
            [labs-4-cource.storage
             :refer
             [carcas-modification-state new-primitives]]
            [taoensso.timbre :as timbre :refer-macros [debug spy]]))

(derive :translate ::mode)
(derive :scale ::mode)
(derive :rotate ::mode)
(derive :project ::mode)

(derive :move ::mouse)
(derive :click ::mouse)

(defmulti push-event-carcas-mode (fn [this event] (spy :debug [(get-state this) (:type event)])))

(defn get-axis-delta
  "get vector of deltas within X, Y, Z axis."
  [event]
  (let [code (spy :info (.-code event))]
    (spy :info (= code "KeyJ"))
    [(cond
       (= "KeyH" code) -1
       (= "KeyL" code) 1
       :else 0)
     (cond
       (= "KeyJ" code) -1
       (= "KeyK" code) 1
       :else 0)
     (cond
       (= "KeyN" code) -1
       (= "KeyP" code) 1
       :else 0)]))

(defmethod push-event-carcas-mode [:translate :keyboard]
  [this {event :event}]
  (let [translation-diff (spy :info (get-axis-delta event))]
    (swap! new-primitives update-in [0 :points]
           translate translation-diff)))

(defmethod push-event-carcas-mode [:rotate :keyboard]
  [this {event :event}]
  (let [rotation-diff (map (partial * 0.1) (get-axis-delta event))]
    (swap! new-primitives update-in [0 :points]
           rotate rotation-diff)))

(defmethod push-event-carcas-mode [:scale :keyboard]
  [this {event :event}]
  (let [scale-diff (map (fn [x] (+ 1 (* 0.1 x))) (get-axis-delta event))]
    (swap! new-primitives update-in [0 :points]
           scale scale-diff)))

(defn third [col]
    (first (rest (rest col))))

(defn find-minimal-z-value [points]
    (first (sort (map third points) )))

(defmethod push-event-carcas-mode [:project :keyboard]
  [this {event :event}]
  (let [projection-diff (- (find-minimal-z-value (:points (first @new-primitives))) 1) ]
    (swap! new-primitives update-in [0 :points]
           project (spy :info projection-diff))))

(defmethod push-event-carcas-mode [::mode :file]
  [this {file :event}]
  (debug file)
  (reset! new-primitives [file]))

(defmethod push-event-carcas-mode [::mode ::mouse]
    ;; do nothing
  [this event])

(deftype CarcasMode
         ^{:doc "editing mode for carcas figures iteractions"}
         []
  StateMachine
  (push-event [this event] (push-event-carcas-mode this event))
  (get-state [this] @carcas-modification-state))
