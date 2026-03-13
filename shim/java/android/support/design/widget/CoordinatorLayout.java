package android.support.design.widget;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.support.design.widget.CoordinatorLayout
 *
 * CoordinatorLayout is a super-powered FrameLayout intended for two primary use cases:
 * (1) As a top-level application decor or chrome layout.
 * (2) As a container for a specific interaction with one or more child views.
 *
 * All coordination and animation is stubbed.
 */
public class CoordinatorLayout extends ViewGroup {

    public CoordinatorLayout() {
        super();
    }

    public CoordinatorLayout(Object context) {
        super();
    }

    public CoordinatorLayout(Object context, Object attrs) {
        super();
    }

    public CoordinatorLayout(Object context, Object attrs, int defStyleAttr) {
        super();
    }

    // ── Behavior (abstract inner class) ──

    /**
     * Interaction behavior plugin for child views of CoordinatorLayout.
     *
     * A Behavior implements one or more interactions that a user can take on a child view.
     * These interactions may include drags, swipes, flings, or any other gestures.
     *
     * @param <V> the child View type that this Behavior acts upon
     */
    public static abstract class Behavior<V extends View> {

        public Behavior() {}

        public Behavior(Object context, Object attrs) {}

        /**
         * Called when the dependent view has moved or changed its layout.
         *
         * @param parent     the CoordinatorLayout parent of the view this Behavior is
         *                   associated with
         * @param child      the child view of the CoordinatorLayout this Behavior is
         *                   associated with
         * @param dependency the dependent view that changed
         * @return return true if the behavior changed the child view's size or position,
         *         false otherwise
         */
        public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
            return false;
        }

        /**
         * Called when a descendant of the CoordinatorLayout attempts to initiate a nested scroll.
         *
         * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
         *                          associated with
         * @param child             the child view of the CoordinatorLayout this Behavior is
         *                          associated with
         * @param directTargetChild the child view of the CoordinatorLayout that either is or
         *                          contains the target of the nested scroll operation
         * @param target            the descendant view of the CoordinatorLayout initiating the
         *                          nested scroll
         * @param nestedScrollAxes  the axes that this nested scroll applies to
         * @return true if the Behavior wishes to accept this nested scroll
         */
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                           View directTargetChild, View target,
                                           int nestedScrollAxes) {
            return false;
        }

        /**
         * Called when a nested scroll in progress has updated and the target has scrolled or
         * attempted to scroll.
         *
         * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
         *                          associated with
         * @param child             the child view of the CoordinatorLayout this Behavior is
         *                          associated with
         * @param target            the descendant view of the CoordinatorLayout performing the
         *                          nested scroll
         * @param dxConsumed        horizontal pixels consumed by the target's own scroll operation
         * @param dyConsumed        vertical pixels consumed by the target's own scroll operation
         * @param dxUnconsumed      horizontal pixels not consumed by the target's own scroll
         * @param dyUnconsumed      vertical pixels not consumed by the target's own scroll
         */
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                   int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed) {}
    }

    // ── LayoutParams ──

    /**
     * Parameters describing the desired layout for a child of a CoordinatorLayout.
     */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        private Behavior mBehavior;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.mBehavior = source.mBehavior;
        }

        /**
         * Set the Behavior associated with the child view this LayoutParams instance is
         * associated with.
         *
         * @param behavior behavior to set
         */
        public void setBehavior(Behavior behavior) {
            this.mBehavior = behavior;
        }

        /**
         * Get the Behavior currently associated with this LayoutParams instance and its
         * associated child view.
         *
         * @return the currently associated Behavior
         */
        public Behavior getBehavior() {
            return mBehavior;
        }

        /** Gravity of the associated child within the parent CoordinatorLayout. */
        public int gravity = -1;

        /** Anchor to another view by id. -1 means no anchor (equivalent to View.NO_ID). */
        public int anchorId = -1;

        /** Anchor gravity within the referenced anchor view. */
        public int anchorGravity = -1;
    }
}
