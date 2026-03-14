package android.view;

public class ViewGroup extends View {
    private final java.util.List<View> mChildren = new java.util.ArrayList<>();

    public ViewGroup(android.content.Context context, android.util.AttributeSet attrs) {}
    public ViewGroup(android.content.Context context) {}
    public ViewGroup(int nodeType) { super(nodeType); }
    public ViewGroup() {}

    @Override
    public void destroy() {
        for (View child : mChildren) {
            child.destroy();
        }
        mChildren.clear();
        super.destroy();
    }

    public static class LayoutParams {
        public static final int MATCH_PARENT = -1;
        public static final int WRAP_CONTENT = -2;
        public int width;
        public int height;
        public LayoutParams() {}
        public LayoutParams(int width, int height) { this.width = width; this.height = height; }
    }

    public static class MarginLayoutParams extends LayoutParams {
        public int leftMargin;
        public int topMargin;
        public int rightMargin;
        public int bottomMargin;
        public MarginLayoutParams() {}
        public MarginLayoutParams(int width, int height) { super(width, height); }
        public MarginLayoutParams(MarginLayoutParams source) {}
        public void setMargins(int left, int top, int right, int bottom) {
            leftMargin = left; topMargin = top; rightMargin = right; bottomMargin = bottom;
        }
    }

    public static final int CLIP_TO_PADDING_MASK = 0;
    public static final int FOCUS_AFTER_DESCENDANTS = 0;
    public static final int FOCUS_BEFORE_DESCENDANTS = 0;
    public static final int FOCUS_BLOCK_DESCENDANTS = 0;
    public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
    public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 0;
    public boolean addStatesFromChildren() { return false; }

    public void addView(View child) {
        if (child != null) {
            if (child.mParent != null) {
                child.mParent.removeView(child);
            }
            mChildren.add(child);
            child.mParent = this;
        }
    }
    public void addView(View child, int index) {
        if (child != null) {
            if (child.mParent != null) {
                child.mParent.removeView(child);
            }
            mChildren.add(index, child);
            child.mParent = this;
        }
    }

    @Override
    public View findViewById(int id) {
        if (id == NO_ID) return null;
        if (getId() == id) return this;
        for (int i = 0; i < mChildren.size(); i++) {
            View found = mChildren.get(i).findViewById(id);
            if (found != null) return found;
        }
        return null;
    }
    public void addView(Object p0) { if (p0 instanceof View) addView((View) p0); }
    public void addView(Object p0, Object p1) { if (p0 instanceof View) addView((View) p0); }
    public void addView(Object p0, Object p1, Object p2) { if (p0 instanceof View) addView((View) p0); }
    public boolean addViewInLayout(Object p0, Object p1, Object p2) { return false; }
    public boolean addViewInLayout(Object p0, Object p1, Object p2, Object p3) { return false; }
    public void attachLayoutAnimationParameters(Object p0, Object p1, Object p2, Object p3) {}
    public void attachViewToParent(Object p0, Object p1, Object p2) {}
    public void bringChildToFront(Object p0) {}
    public boolean canAnimate() { return false; }
    public boolean checkLayoutParams(Object p0) { return false; }
    public void childDrawableStateChanged(Object p0) {}
    public void childHasTransientStateChanged(Object p0, Object p1) {}
    public void cleanupLayoutState(Object p0) {}
    public void clearChildFocus(Object p0) {}
    public void clearDisappearingChildren() {}
    public void debug(Object p0) {}
    public void detachAllViewsFromParent() {}
    public void detachViewFromParent(Object p0) {}
    public void detachViewsFromParent(Object p0, Object p1) {}
    public void dispatchFreezeSelfOnly(Object p0) {}
    public void dispatchSetActivated(Object p0) {}
    public void dispatchSetSelected(Object p0) {}
    public void dispatchThawSelfOnly(Object p0) {}
    public boolean drawChild(Object p0, Object p1, Object p2) { return false; }
    public void endViewTransition(Object p0) {}
    public Object focusSearch(Object p0, Object p1) { return null; }
    public void focusableViewAvailable(Object p0) {}
    public boolean gatherTransparentRegion(Object p0) { return false; }
    public Object generateDefaultLayoutParams() { return null; }
    public Object generateLayoutParams(Object p0) { return null; }
    public View getChildAt(int index) {
        if (index >= 0 && index < mChildren.size()) return mChildren.get(index);
        return null;
    }
    public int getChildCount() { return mChildren.size(); }
    public int getChildDrawingOrder(Object p0, Object p1) { return 0; }
    public int getChildDrawingOrder(Object p0) { return 0; }
    public static int getChildMeasureSpec(Object p0, Object p1, Object p2) { return 0; }
    public boolean getChildStaticTransformation(Object p0, Object p1) { return false; }
    public boolean getChildVisibleRect(Object p0, Object p1, Object p2) { return false; }
    public Object getFocusedChild() { return null; }
    public Object getLayoutAnimation() { return null; }
    public Object getLayoutAnimationListener() { return null; }
    public int getLayoutMode() { return 0; }
    public Object getLayoutTransition() { return null; }
    public int getNestedScrollAxes() { return 0; }
    public Object getOverlay() { return null; }
    public int indexOfChild(Object p0) {
        if (p0 instanceof View) return mChildren.indexOf(p0);
        return -1;
    }
    public boolean isLayoutSuppressed() { return false; }
    public boolean isMotionEventSplittingEnabled() { return false; }
    public boolean isTransitionGroup() { return false; }
    public void layout(Object p0, Object p1, Object p2, Object p3) {}
    public void measureChild(Object p0, Object p1, Object p2) {}
    public void measureChildWithMargins(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void measureChildren(Object p0, Object p1) {}
    public void notifySubtreeAccessibilityStateChanged(Object p0, Object p1, Object p2) {}
    public void offsetDescendantRectToMyCoords(Object p0, Object p1) {}
    public void offsetRectIntoDescendantCoords(Object p0, Object p1) {}
    public boolean onInterceptHoverEvent(Object p0) { return false; }
    public boolean onInterceptTouchEvent(Object p0) { return false; }
    public void onLayout(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public boolean onNestedFling(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean onNestedPreFling(Object p0, Object p1, Object p2) { return false; }
    public boolean onNestedPrePerformAccessibilityAction(Object p0, Object p1, Object p2) { return false; }
    public void onNestedPreScroll(Object p0, Object p1, Object p2, Object p3) {}
    public void onNestedScroll(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void onNestedScrollAccepted(Object p0, Object p1, Object p2) {}
    public boolean onRequestFocusInDescendants(Object p0, Object p1) { return false; }
    public boolean onRequestSendAccessibilityEvent(Object p0, Object p1) { return false; }
    public boolean onStartNestedScroll(Object p0, Object p1, Object p2) { return false; }
    public void onStopNestedScroll(Object p0) {}
    public void onViewAdded(Object p0) {}
    public void onViewRemoved(Object p0) {}
    public void recomputeViewAttributes(Object p0) {}
    public void removeAllViews() {
        for (View child : mChildren) child.mParent = null;
        mChildren.clear();
    }
    public void removeAllViewsInLayout() {
        for (View child : mChildren) child.mParent = null;
        mChildren.clear();
    }
    public void removeDetachedView(Object p0, Object p1) {}
    public void removeView(View child) {
        if (mChildren.remove(child)) child.mParent = null;
    }
    public void removeView(Object p0) { if (p0 instanceof View) removeView((View) p0); }
    public void removeViewAt(Object p0) {
        if (p0 instanceof Integer) {
            int index = (Integer) p0;
            if (index >= 0 && index < mChildren.size()) {
                View child = mChildren.remove(index);
                if (child != null) child.mParent = null;
            }
        }
    }
    public void removeViewInLayout(Object p0) {}
    public void removeViews(Object p0, Object p1) {}
    public void removeViewsInLayout(Object p0, Object p1) {}
    public void requestChildFocus(Object p0, Object p1) {}
    public boolean requestChildRectangleOnScreen(Object p0, Object p1, Object p2) { return false; }
    public void requestDisallowInterceptTouchEvent(Object p0) {}
    public boolean requestSendAccessibilityEvent(Object p0, Object p1) { return false; }
    public void requestTransparentRegion(Object p0) {}
    public void scheduleLayoutAnimation() {}
    public void setAddStatesFromChildren(Object p0) {}
    public void setChildrenDrawingOrderEnabled(Object p0) {}
    public void setClipChildren(Object p0) {}
    public void setClipToPadding(Object p0) {}
    public void setDescendantFocusability(Object p0) {}
    public void setLayoutAnimation(Object p0) {}
    public void setLayoutAnimationListener(Object p0) {}
    public void setLayoutMode(Object p0) {}
    public void setLayoutTransition(Object p0) {}
    public void setMotionEventSplittingEnabled(Object p0) {}
    public void setOnHierarchyChangeListener(Object p0) {}
    public void setStaticTransformationsEnabled(Object p0) {}
    public void setTouchscreenBlocksFocus(Object p0) {}
    public void setTransitionGroup(Object p0) {}
    public boolean shouldDelayChildPressedState() { return false; }
    public boolean showContextMenuForChild(Object p0) { return false; }
    public boolean showContextMenuForChild(Object p0, Object p1, Object p2) { return false; }
    public Object startActionModeForChild(Object p0, Object p1) { return null; }
    public Object startActionModeForChild(Object p0, Object p1, Object p2) { return null; }
    public void startLayoutAnimation() {}
    public void startViewTransition(Object p0) {}
    public void suppressLayout(Object p0) {}
    public void updateViewLayout(Object p0, Object p1) {}
}
