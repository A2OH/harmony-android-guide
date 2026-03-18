package android.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;

/**
 * Android-compatible ViewParent shim.
 * Interface implemented by views that can contain child views.
 */
public interface ViewParent {

    void requestLayout();
    boolean isLayoutRequested();
    void requestChildFocus(View child, View focused);
    void clearChildFocus(View child);
    ViewParent getParent();
    void requestDisallowInterceptTouchEvent(boolean disallowIntercept);
    void childDrawableStateChanged(View child);

    // Methods needed by AOSP ViewGroup
    default void focusableViewAvailable(View v) {}
    default boolean showContextMenuForChild(View originalView) { return false; }
    default boolean showContextMenuForChild(View originalView, float x, float y) { return false; }
    default ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) { return null; }
    default ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback, int type) { return null; }
    default View focusSearch(View v, int direction) { return null; }
    default boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) { return false; }
    default void childHasTransientStateChanged(View child, boolean hasTransientState) {}
    default void recomputeViewAttributes(View child) {}
    default void notifySubtreeAccessibilityStateChanged(View child, View source, int changeType) {}
    default void onDescendantInvalidated(View child, View target) {}
    default ViewParent invalidateChildInParent(int[] location, Rect dirty) { return null; }
    default boolean getChildVisibleRect(View child, Rect r, Point offset) { return false; }
    default void requestTransparentRegion(View child) {}
    default void subtractObscuredTouchableRegion(android.graphics.Region touchableRegion, View child) {}
    default boolean canResolveLayoutDirection() { return false; }
    default boolean isLayoutDirectionResolved() { return false; }
    default int getLayoutDirection() { return 0; }
    default boolean canResolveTextDirection() { return false; }
    default boolean isTextDirectionResolved() { return false; }
    default int getTextDirection() { return 0; }
    default boolean canResolveTextAlignment() { return false; }
    default boolean isTextAlignmentResolved() { return false; }
    default int getTextAlignment() { return 0; }
    default void onDescendantUnbufferedRequested() {}
    default void bringChildToFront(View child) {}
    default void createContextMenu(ContextMenu menu) {}
    default ViewParent getParentForAccessibility() { return null; }
    default void invalidateChild(View child, Rect dirty) {}
    default View keyboardNavigationClusterSearch(View currentCluster, int direction) { return null; }
    default void requestFitSystemWindows() {}
    default boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) { return false; }
    default boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) { return false; }
    default void onNestedScrollAccepted(View child, View target, int axes) {}
    default void onStopNestedScroll(View child) {}
    default void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {}
    default void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {}
    default boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) { return false; }
    default boolean onNestedPreFling(View target, float velocityX, float velocityY) { return false; }
    default boolean onNestedPrePerformAccessibilityAction(View target, int action, android.os.Bundle args) { return false; }
}
