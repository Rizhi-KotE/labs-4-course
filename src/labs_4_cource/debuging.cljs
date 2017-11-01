(ns labs-4-cource.debuging
  (:require [cljs.test :refer-macros [deftest]]
            [labs-4-cource.canvas
             :refer
             [clean-canvas! draw-pixels! swap-hidden-to-visible!]]
            [labs-4-cource.first-order-lines :refer [line-points]]
            [labs-4-cource.storage
             :refer
             [add-primitives
              debug-state
              drawer
              lines-generators
              new-points
              new-primitives
              not-full-line
              primitives
              remove-debug-line!
              selected]]
            [taoensso.timbre :as timbre :refer-macros [spy]]))

(defn save-debug-line! []
  (when-not (nil? (:line @not-full-line))
    (draw-pixels! @drawer (:rest-points @not-full-line))
    (add-primitives (:line @not-full-line))))

(defn draw-line-by-point! []
  (spy :debug "draw-point" (when (seq (:rest-points @not-full-line)) (draw-pixels! @drawer [(first (:rest-points @not-full-line))])))
  (swap! not-full-line assoc :rest-points (rest (:rest-points @not-full-line)))
  (when (empty? (:rest-points @not-full-line))
    (save-debug-line!)
    (remove-debug-line!)))

(defn add-line-to-debug! [line]
  (save-debug-line!)
  (reset! not-full-line {:line line :rest-points (line-points line)}))

(defn add-line-from-pos []
  (when (spy :info (= (count @new-points) 2))
    (let [line (spy :info "new-line" (apply (@selected lines-generators) @new-points))]
      (if (= @debug-state :not)
        (add-primitives line)
        (add-line-to-debug! line))
      (reset! new-points nil))))

(deftest add-line-to-debug-test
  (reset! not-full-line nil)
  (add-line-to-debug! (line-points {:type :wu :p1 [0 0] :p2 [5 5]}))
  (= @not-full-line {:line {:type :wu [0 0] [5 5]}
                     :rest-points '([0 0 1] [1 1 1] [2 2 1] [3 3 1] [4 4 1] [5 5 1])}))

(defmulti get-line (fn [line] (spy :debug (str "draw-line " line) [(:type line) @debug-state])))

(derive :simple ::line)
(derive :be     ::line)
(derive :wu     ::line)
(derive :circle ::line)
(derive :elipse ::line)
(derive :hyperbola ::line)
(derive :ermit ::line)
(derive :bezie ::line)
(derive :spline ::line)

(defmethod get-line [:wu    :not] [line]  (line-points line))
(defmethod get-line :default [line]  (map (fn [[x y]] [x y 1]) (line-points line)))

(defn draw-permanent-content [{:keys [hidden visible] :as drawer} lines]
  (draw-pixels! hidden
                (mapcat get-line lines))
  (assoc drawer :perm-changed true))

(defn draw-temporaly-content [{:keys [extra visible] :as drawer} lines]
  (clean-canvas! extra)
  (draw-pixels! extra
                (mapcat get-line lines))
  (assoc drawer :temp-changed true))

(defn draw-canvas-contents!
  [permanent-change temporary]
  (let [{:keys [visible hidden extra]} @drawer]
    (clean-canvas! extra)
    (swap! drawer draw-temporaly-content temporary)
    (when-not (nil? permanent-change)
      (swap! drawer draw-permanent-content permanent-change))))

(defn draw-visible-content
    [{:keys [visible hidden extra perm-changed temp-changed] :as drawer}]
    {:pre [(not (nil? visible))]}
    (when (or temp-changed perm-changed) (clean-canvas! visible))
    (when perm-changed (swap-hidden-to-visible! visible hidden))
    (when temp-changed (swap-hidden-to-visible! visible extra))
    (assoc drawer :perm-changed nil :temp-changed nil))

(comment (mapcat get-line @new-primitives)
         (mapcat   (comp pr :type) @primitives)
         (:type {:type :simple, :p1 '(49 62.5), :p2 '(64 55)}))
