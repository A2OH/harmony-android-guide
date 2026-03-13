package android.view;

import android.graphics.Rect;

/**
 * Shim: android.view.TouchDelegate — pure Java stub.
 * Allows expanding the touchable area of a View beyond its actual bounds.
 */
public class TouchDelegate {

    /** Constant for extending the touch area above the view. */
    public static final int ABOVE = 1;
    /** Constant for extending the touch area below the view. */
    public static final int BELOW = 2;
    /** Constant for extending the touch area to the left. */
    public static final int TO_LEFT = 4;
    /** Constant for extending the touch area to the right. */
    public static final int TO_RIGHT = 8;

    private final Rect mBounds;
    private final View mDelegateView;

    /**
     * Construct a TouchDelegate.
     * @param bounds the expanded bounds (in the parent's coordinate system)
     * @param delegateView the child view that receives touch events
     */
    public TouchDelegate(Rect bounds, View delegateView) {
        mBounds = bounds;
        mDelegateView = delegateView;
    }

    /**
     * Forward touch events to the delegate view if within bounds.
     * @param event the motion event (Object to avoid dependency chains)
     * @return true if the event was handled
     */
    public boolean onTouchEvent(Object event) {
        // no-op stub — always returns false
        return false;
    }
}
