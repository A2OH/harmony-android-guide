package android.view;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.transition.Transition;
import android.view.accessibility.AccessibilityEvent;

/** Auto-generated stub for AOSP compilation. */
public class ViewRootImpl implements ViewParent {
    public static final int NEW_INSETS_MODE_FULL = 2;
    public boolean mIsAnimating = false;
    public static int sNewInsetsMode = NEW_INSETS_MODE_FULL;
    public Choreographer mChoreographer;
    public Surface mSurface = new Surface();
    public WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();
    public int mCurScrollY;
    public Thread mThread = Thread.currentThread();

    public ViewRootImpl() {}

    public static boolean isViewDescendantOf(View child, View parent) { return false; }
    public Object getInsetsController() { return null; }
    public View getAccessibilityFocusedHost() { return null; }
    public void requestTransitionStart(Transition transition) {}
    public void requestTransitionStart(android.animation.LayoutTransition transition) {}

    // Methods needed for View.java
    public void cancelInvalidate(View view) {}
    public void dispatchInvalidateDelayed(View view, long delayMilliseconds) {}
    public void dispatchInvalidateOnAnimation(View view) {}
    public void dispatchInvalidateRectDelayed(Object info, long delayMilliseconds) {}
    public void dispatchInvalidateRectOnAnimation(Object info) {}
    public Object getImeFocusController() { return null; }
    public WindowInsets getWindowInsets(boolean forceConstruct) { return new WindowInsets(); }
    public void updateSystemGestureExclusionRectsForView(View view) {}
    public boolean ensureTouchMode(boolean isInTouchMode) { return false; }
    public boolean isInTouchMode() { return true; }
    public boolean isInLayout() { return false; }
    public void setDragFocus(View view, DragEvent event) {}
    public boolean hasPointerCapture() { return false; }
    public void requestPointerCapture(boolean enable) {}
    public void setAccessibilityFocus(View view, AccessibilityEvent event) {}
    public void requestLayoutDuringLayout(View view) {}
    public void getLastTouchPoint(Point outLocation) {}
    public int getLastTouchSource() { return 0; }
    public SurfaceControl getSurfaceControl() { return new SurfaceControl(); }
    public void setLocalDragState(Object obj) {}
    public void transformMatrixToGlobal(Matrix m) {}
    public void transformMatrixToLocal(Matrix m) {}

    // ViewParent methods
    public void requestLayout() {}
    public boolean isLayoutRequested() { return false; }
    public void requestChildFocus(View child, View focused) {}
    public void clearChildFocus(View child) {}
    public ViewParent getParent() { return null; }
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    public void childDrawableStateChanged(View child) {}
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) { return false; }
    public boolean showContextMenuForChild(View originalView) { return false; }
    public boolean showContextMenuForChild(View originalView, float x, float y) { return false; }
    public android.view.ActionMode startActionModeForChild(View originalView, android.view.ActionMode.Callback callback) { return null; }
    public android.view.ActionMode startActionModeForChild(View originalView, android.view.ActionMode.Callback callback, int type) { return null; }
    public void createContextMenu(ContextMenu menu) {}
    public void childHasTransientStateChanged(View child, boolean hasTransientState) {}
    public boolean getChildVisibleRect(View child, Rect r, Point offset) { return false; }
    public View focusSearch(View v, int direction) { return null; }
    public void bringChildToFront(View child) {}
    public void focusableViewAvailable(View v) {}
    public boolean canResolveLayoutDirection() { return true; }
    public boolean isLayoutDirectionResolved() { return true; }
    public int getLayoutDirection() { return 0; }
    public boolean canResolveTextDirection() { return true; }
    public boolean isTextDirectionResolved() { return true; }
    public int getTextDirection() { return 0; }
    public boolean canResolveTextAlignment() { return true; }
    public boolean isTextAlignmentResolved() { return true; }
    public int getTextAlignment() { return 0; }
    public ViewParent getParentForAccessibility() { return null; }
    public void notifySubtreeAccessibilityStateChanged(View child, View source, int changeType) {}
    public void requestFitSystemWindows() {}
    public void requestTransparentRegion(View child) {}
    public void onDescendantInvalidated(View child, View target) {}
    public void onDescendantUnbufferedRequested() {}
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) { return false; }
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {}
    public void onStopNestedScroll(View target) {}
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {}
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {}
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) { return false; }
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) { return false; }
    public boolean onNestedPrePerformAccessibilityAction(View target, int action, android.os.Bundle args) { return false; }
    public void invalidateChild(View child, Rect dirty) {}
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) { return null; }
    public void recomputeViewAttributes(View child) {}
    public View keyboardNavigationClusterSearch(View currentCluster, int direction) { return null; }
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) { return false; }
}
