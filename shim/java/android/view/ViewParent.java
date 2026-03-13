package android.view;

/**
 * Android-compatible ViewParent shim.
 * Interface implemented by views that can contain child views.
 */
public interface ViewParent {

    /** Ask the parent to update its view layout. */
    void requestLayout();

    /** Returns true if a layout was requested but not yet completed. */
    boolean isLayoutRequested();

    /** Called when a child view wants to take focus. */
    void requestChildFocus(View child, View focused);

    /** Called when a child no longer holds focus. */
    void clearChildFocus(View child);

    /** Returns this view's parent, or null if it has none. */
    ViewParent getParent();

    /**
     * Called when a descendant does not want the parent to intercept touch events
     * during a gesture.
     */
    void requestDisallowInterceptTouchEvent(boolean disallowIntercept);

    /** Called when the state of a child's drawable has changed. */
    void childDrawableStateChanged(View child);
}
