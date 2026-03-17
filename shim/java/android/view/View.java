package android.view;

public class View {
    public View(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {}
    public View(android.content.Context context, android.util.AttributeSet attrs) {}
    public View(android.content.Context context) {}
    public View(int nodeType) {
        if (nodeType > 0) {
            nativeHandle = com.ohos.shim.bridge.OHBridge.nodeCreate(nodeType);
        }
    }
    public View() {}

    // OHBridge attribute constants for ArkUI node properties
    private static final int ATTR_VISIBILITY = 1;
    private static final int ATTR_BG_COLOR = 2;
    private static final int ATTR_OPACITY = 3;
    private static final int ATTR_PADDING = 4;
    private static final int ATTR_ENABLED = 5;

    // Event constants
    private static final int EVENT_CLICK = 1;
    private int mClickEventId;
    private static final java.util.concurrent.atomic.AtomicInteger sEventIdGen = new java.util.concurrent.atomic.AtomicInteger(1);
    private static int generateEventId() { return sEventIdGen.getAndIncrement(); }

    // OHBridge native handle for OHOS ArkUI node
    protected long nativeHandle;
    public long getNativeHandle() { return nativeHandle; }
    public View findViewByHandle(long handle) {
        if (nativeHandle == handle) return this;
        return null;
    }
    public void onNativeEvent(int eventType, int eventCode, String data) {}

    // ── State storage for properly-typed API ──
    private int mId = NO_ID;
    private int mVisibility = VISIBLE;
    private boolean mEnabled = true;
    private boolean mClickable;
    private boolean mLongClickable;
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;
    private int mLeft, mTop, mRight, mBottom;
    private float mAlpha = 1.0f;
    private Object mTag;
    private OnClickListener mClickListener;
    private OnTouchListener mTouchListener;
    private OnKeyListener mKeyListener;
    ViewGroup mParent;
    private Object mLayoutParams;
    private int mBackgroundColor = 0; // 0 = transparent (no background)
    private android.graphics.drawable.Drawable mBackground;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mScrollX;
    private int mScrollY;
    private float mTranslationX;
    private float mTranslationY;

    public interface OnClickListener {
        void onClick(View v);
    }

    public interface OnTouchListener {
        boolean onTouch(View v, MotionEvent event);
    }

    public interface OnKeyListener {
        boolean onKey(View v, int keyCode, KeyEvent event);
    }

    public void destroy() {
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeDispose(nativeHandle);
            nativeHandle = 0;
        }
    }

    // Properly-typed overloads
    public void setVisibility(int visibility) {
        mVisibility = visibility;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrInt(nativeHandle, ATTR_VISIBILITY, visibility);
        }
    }
    public int getVisibility() { return mVisibility; }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrInt(nativeHandle, ATTR_ENABLED, enabled ? 1 : 0);
        }
    }
    public boolean isEnabled() { return mEnabled; }

    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left; mPaddingTop = top; mPaddingRight = right; mPaddingBottom = bottom;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PADDING, (float) left, (float) top, (float) right, (float) bottom, 4);
        }
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_OPACITY, alpha, 0, 0, 0, 1);
        }
    }
    public float getAlpha() { return mAlpha; }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrColor(nativeHandle, ATTR_BG_COLOR, color);
        }
    }
    public int getBackgroundColor() { return mBackgroundColor; }

    public void setTag(Object tag) { mTag = tag; }
    public Object getTag() { return mTag; }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
        if (!mClickable) mClickable = true;
        if (nativeHandle != 0 && mClickEventId == 0) {
            mClickEventId = generateEventId();
            com.ohos.shim.bridge.OHBridge.nodeRegisterEvent(nativeHandle, EVENT_CLICK, mClickEventId);
        }
    }

    public void setOnTouchListener(OnTouchListener listener) { mTouchListener = listener; }
    public void setOnKeyListener(OnKeyListener listener) { mKeyListener = listener; }

    public void setClickable(boolean clickable) { mClickable = clickable; }
    public boolean isClickable() { return mClickable; }

    public void setLongClickable(boolean longClickable) { mLongClickable = longClickable; }

    // ── Layout position ──
    public int getLeft() { return mLeft; }
    public int getTop() { return mTop; }
    public int getRight() { return mRight; }
    public int getBottom() { return mBottom; }
    public int getWidth() { return mRight - mLeft; }
    public int getHeight() { return mBottom - mTop; }

    public static final int WRAP_CONTENT = -2;
    public static final int MATCH_PARENT = -1;

    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 0;
    public static final int ALPHA = 0;
    public static final int AUTOFILL_FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 0;
    public static final int AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE = 0;
    public static final int AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY = 0;
    public static final int AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH = 0;
    public static final int AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR = 0;
    public static final int AUTOFILL_HINT_CREDIT_CARD_NUMBER = 0;
    public static final int AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE = 0;
    public static final int AUTOFILL_HINT_EMAIL_ADDRESS = 0;
    public static final int AUTOFILL_HINT_NAME = 0;
    public static final int AUTOFILL_HINT_PASSWORD = 0;
    public static final int AUTOFILL_HINT_PHONE = 0;
    public static final int AUTOFILL_HINT_POSTAL_ADDRESS = 0;
    public static final int AUTOFILL_HINT_POSTAL_CODE = 0;
    public static final int AUTOFILL_HINT_USERNAME = 0;
    public static final int AUTOFILL_TYPE_DATE = 0;
    public static final int AUTOFILL_TYPE_LIST = 0;
    public static final int AUTOFILL_TYPE_NONE = 0;
    public static final int AUTOFILL_TYPE_TEXT = 0;
    public static final int AUTOFILL_TYPE_TOGGLE = 0;
    public static final int DRAG_FLAG_GLOBAL = 0;
    public static final int DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION = 0;
    public static final int DRAG_FLAG_GLOBAL_PREFIX_URI_PERMISSION = 0;
    public static final int DRAG_FLAG_GLOBAL_URI_READ = 0;
    public static final int DRAG_FLAG_GLOBAL_URI_WRITE = 0;
    public static final int DRAG_FLAG_OPAQUE = 0;
    public static final int EMPTY_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_SELECTED_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_SELECTED_STATE_SET = 0;
    public static final int ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_STATE_SET = 0;
    public static final int ENABLED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 0;
    public static final int FIND_VIEWS_WITH_TEXT = 0;
    public static final int FOCUSABLE = 0;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 0;
    public static final int FOCUSABLE_AUTO = 0;
    public static final int FOCUSED_SELECTED_STATE_SET = 0;
    public static final int FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int FOCUSED_STATE_SET = 0;
    public static final int FOCUSED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int FOCUS_BACKWARD = 0;
    public static final int FOCUS_DOWN = 0;
    public static final int FOCUS_FORWARD = 0;
    public static final int FOCUS_LEFT = 0;
    public static final int FOCUS_RIGHT = 0;
    public static final int FOCUS_UP = 0;
    public static final int GONE = 8;
    public static final int HAPTIC_FEEDBACK_ENABLED = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_AUTO = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_NO = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_YES = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_AUTO = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO_EXCLUDE_DESCENDANTS = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES_EXCLUDE_DESCENDANTS = 0;
    public static final int INVISIBLE = 4;
    public static final int KEEP_SCREEN_ON = 0;
    public static final int LAYER_TYPE_HARDWARE = 0;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 0;
    public static final int LAYOUT_DIRECTION_INHERIT = 0;
    public static final int LAYOUT_DIRECTION_LOCALE = 0;
    public static final int LAYOUT_DIRECTION_LTR = 0;
    public static final int LAYOUT_DIRECTION_RTL = 0;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 0;
    public static final int MEASURED_SIZE_MASK = 0;
    public static final int MEASURED_STATE_MASK = 0;
    public static final int MEASURED_STATE_TOO_SMALL = 0;
    public static final int NOT_FOCUSABLE = 0;
    public static final int NO_ID = -1;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 0;
    public static final int OVER_SCROLL_NEVER = 0;
    public static final int PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_SELECTED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_STATE_SET = 0;
    public static final int PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_FOCUSED_SELECTED_STATE_SET = 0;
    public static final int PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_SELECTED_STATE_SET = 0;
    public static final int PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int PRESSED_STATE_SET = 0;
    public static final int PRESSED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ROTATION = 0;
    public static final int ROTATION_X = 0;
    public static final int ROTATION_Y = 0;
    public static final int SCALE_X = 0;
    public static final int SCALE_Y = 0;
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 0;
    public static final int SCROLLBARS_INSIDE_INSET = 0;
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    public static final int SCROLLBARS_OUTSIDE_INSET = 0;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 0;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 0;
    public static final int SCROLLBAR_POSITION_RIGHT = 0;
    public static final int SCROLL_AXIS_HORIZONTAL = 0;
    public static final int SCROLL_AXIS_NONE = 0;
    public static final int SCROLL_AXIS_VERTICAL = 0;
    public static final int SCROLL_INDICATOR_BOTTOM = 0;
    public static final int SCROLL_INDICATOR_END = 0;
    public static final int SCROLL_INDICATOR_LEFT = 0;
    public static final int SCROLL_INDICATOR_RIGHT = 0;
    public static final int SCROLL_INDICATOR_START = 0;
    public static final int SCROLL_INDICATOR_TOP = 0;
    public static final int SELECTED_STATE_SET = 0;
    public static final int SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int SOUND_EFFECTS_ENABLED = 0;
    public static final int TEXT_ALIGNMENT_CENTER = 0;
    public static final int TEXT_ALIGNMENT_GRAVITY = 0;
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    public static final int TEXT_ALIGNMENT_TEXT_END = 0;
    public static final int TEXT_ALIGNMENT_TEXT_START = 0;
    public static final int TEXT_ALIGNMENT_VIEW_END = 0;
    public static final int TEXT_ALIGNMENT_VIEW_START = 0;
    public static final int TEXT_DIRECTION_ANY_RTL = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG_LTR = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG_RTL = 0;
    public static final int TEXT_DIRECTION_INHERIT = 0;
    public static final int TEXT_DIRECTION_LOCALE = 0;
    public static final int TEXT_DIRECTION_LTR = 0;
    public static final int TEXT_DIRECTION_RTL = 0;
    public static final int TRANSLATION_X = 0;
    public static final int TRANSLATION_Y = 0;
    public static final int TRANSLATION_Z = 0;
    public static final int VIEW_LOG_TAG = 0;
    public static final int VISIBLE = 0;
    public static final int WINDOW_FOCUSED_STATE_SET = 0;
    public static final int X = 0;
    public static final int Y = 0;
    public static final int Z = 0;
    public void addChildrenForAccessibility(Object p0) {}
    public void addExtraDataToAccessibilityNodeInfo(Object p0, Object p1, Object p2) {}
    public void addFocusables(Object p0, Object p1) {}
    public void addFocusables(Object p0, Object p1, Object p2) {}
    public void addKeyboardNavigationClusters(Object p0, Object p1) {}
    public void addOnAttachStateChangeListener(Object p0) {}
    public void addOnLayoutChangeListener(Object p0) {}
    public void addOnUnhandledKeyEventListener(Object p0) {}
    public void addTouchables(Object p0) {}
    public Object animate() { return null; }
    public void announceForAccessibility(Object p0) {}
    public void autofill(Object p0) {}
    public boolean awakenScrollBars() { return false; }
    public boolean awakenScrollBars(Object p0) { return false; }
    public boolean awakenScrollBars(Object p0, Object p1) { return false; }
    public void bringToFront() {}
    public void buildLayer() {}
    public boolean callOnClick() { return false; }
    public boolean canResolveLayoutDirection() { return false; }
    public boolean canResolveTextAlignment() { return false; }
    public boolean canResolveTextDirection() { return false; }
    public boolean canScrollHorizontally(Object p0) { return false; }
    public boolean canScrollVertically(Object p0) { return false; }
    public void cancelDragAndDrop() {}
    public void cancelLongPress() {}
    public void cancelPendingInputEvents() {}
    public boolean checkInputConnectionProxy(Object p0) { return false; }
    public void clearAnimation() {}
    public void clearFocus() {}
    public static int combineMeasuredStates(Object p0, Object p1) { return 0; }
    public int computeHorizontalScrollExtent() { return 0; }
    public int computeHorizontalScrollOffset() { return 0; }
    public int computeHorizontalScrollRange() { return 0; }
    public void computeScroll() {}
    public Object computeSystemWindowInsets(Object p0, Object p1) { return null; }
    public int computeVerticalScrollExtent() { return 0; }
    public int computeVerticalScrollOffset() { return 0; }
    public int computeVerticalScrollRange() { return 0; }
    public Object createAccessibilityNodeInfo() { return null; }
    public void createContextMenu(Object p0) {}
    public Object dispatchApplyWindowInsets(Object p0) { return null; }
    public boolean dispatchCapturedPointerEvent(Object p0) { return false; }
    public void dispatchConfigurationChanged(Object p0) {}
    public void dispatchDisplayHint(Object p0) {}
    public boolean dispatchDragEvent(Object p0) { return false; }
    public void dispatchDraw(Object p0) {}
    public void dispatchDrawableHotspotChanged(Object p0, Object p1) {}
    public boolean dispatchGenericFocusedEvent(Object p0) { return false; }
    public boolean dispatchGenericMotionEvent(Object p0) { return false; }
    public boolean dispatchGenericPointerEvent(Object p0) { return false; }
    public boolean dispatchHoverEvent(Object p0) { return false; }
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Key listener gets first shot
        if (mKeyListener != null) {
            if (mKeyListener.onKey(this, event.getKeyCode(), event)) {
                return true;
            }
        }
        // Then delegate to onKeyDown/onKeyUp
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return onKeyDown(event.getKeyCode(), event);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            return onKeyUp(event.getKeyCode(), event);
        }
        return false;
    }
    public boolean dispatchKeyEvent(Object p0) {
        if (p0 instanceof KeyEvent) return dispatchKeyEvent((KeyEvent) p0);
        return false;
    }
    public boolean dispatchKeyEventPreIme(Object p0) { return false; }
    public boolean dispatchKeyShortcutEvent(Object p0) { return false; }
    public boolean dispatchNestedFling(Object p0, Object p1, Object p2) { return false; }
    public boolean dispatchNestedPreFling(Object p0, Object p1) { return false; }
    public boolean dispatchNestedPrePerformAccessibilityAction(Object p0, Object p1) { return false; }
    public boolean dispatchNestedPreScroll(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean dispatchNestedScroll(Object p0, Object p1, Object p2, Object p3, Object p4) { return false; }
    public void dispatchPointerCaptureChanged(Object p0) {}
    public boolean dispatchPopulateAccessibilityEvent(Object p0) { return false; }
    public void dispatchProvideAutofillStructure(Object p0, Object p1) {}
    public void dispatchProvideStructure(Object p0) {}
    public void dispatchRestoreInstanceState(Object p0) {}
    public void dispatchSaveInstanceState(Object p0) {}
    public void dispatchSetActivated(Object p0) {}
    public void dispatchSetPressed(Object p0) {}
    public void dispatchSetSelected(Object p0) {}
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Touch listener gets first shot
        if (mTouchListener != null) {
            if (mTouchListener.onTouch(this, event)) {
                return true;
            }
        }
        return onTouchEvent(event);
    }
    public boolean dispatchTouchEvent(Object p0) {
        if (p0 instanceof MotionEvent) return dispatchTouchEvent((MotionEvent) p0);
        return false;
    }
    public boolean dispatchTrackballEvent(Object p0) { return false; }
    public boolean dispatchUnhandledMove(Object p0, Object p1) { return false; }
    public void dispatchVisibilityChanged(Object p0, Object p1) {}
    public void dispatchWindowFocusChanged(Object p0) {}
    public void dispatchWindowInsetsAnimationEnd(Object p0) {}
    public void dispatchWindowInsetsAnimationPrepare(Object p0) {}
    public void dispatchWindowVisibilityChanged(Object p0) {}
    public Object findFocus() { return null; }
    public View findViewById(int id) {
        if (id == NO_ID) return null;
        if (mId == id) return this;
        return null;
    }
    public Object findViewById(Object p0) {
        if (p0 instanceof Integer) return findViewById(((Integer) p0).intValue());
        return null;
    }
    public Object findViewWithTag(Object p0) { return null; }
    public void findViewsWithText(Object p0, Object p1, Object p2) {}
    public Object focusSearch(Object p0) { return null; }
    public void forceHasOverlappingRendering(Object p0) {}
    public void requestLayout() {
        // Walk up to root and trigger measure + layout + draw
        View root = this;
        while (root.mParent != null) root = root.mParent;
        if (root.getWidth() > 0 || root.getHeight() > 0) {
            int wSpec = MeasureSpec.makeMeasureSpec(root.getWidth(), MeasureSpec.EXACTLY);
            int hSpec = MeasureSpec.makeMeasureSpec(root.getHeight(), MeasureSpec.EXACTLY);
            root.measure(wSpec, hSpec);
            root.layout(root.getLeft(), root.getTop(), root.getRight(), root.getBottom());
        }
        invalidate();
    }
    public void forceLayout() {}
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    public static int generateViewId() { return 0; }
    public Object getAccessibilityClassName() { return null; }
    public Object getAccessibilityDelegate() { return null; }
    public int getAccessibilityLiveRegion() { return 0; }
    public Object getAccessibilityNodeProvider() { return null; }
    public Object getAnimation() { return null; }
    public Object getApplicationWindowToken() { return null; }
    public Object getAutofillId() { return null; }
    public int getAutofillType() { return 0; }
    public Object getBackground() { return mBackground; }
    public float getBottomFadingEdgeStrength() { return 0f; }
    public int getBottomPaddingOffset() { return 0; }
    public float getCameraDistance() { return 0f; }
    public Object getClipBounds() { return null; }
    public boolean getClipBounds(Object p0) { return false; }
    public boolean getClipToOutline() { return false; }
    public Object getContextMenuInfo() { return null; }
    public static int getDefaultSize(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer)
            return getDefaultSize((int)(Integer)p0, (int)(Integer)p1);
        return 0;
    }
    public Object getDisplay() { return null; }
    public int getDrawableState() { return 0; }
    public void getDrawingRect(Object p0) {}
    public long getDrawingTime() { return 0L; }
    public Object getFocusables(Object p0) { return null; }
    public void getFocusedRect(Object p0) {}
    public Object getForeground() { return null; }
    public int getForegroundGravity() { return 0; }
    public boolean getGlobalVisibleRect(Object p0, Object p1) { return false; }
    public boolean getGlobalVisibleRect(Object p0) { return false; }
    public Object getHandler() { return null; }
    public boolean getHasOverlappingRendering() { return false; }
    public void getHitRect(Object p0) {}
    public int getHorizontalFadingEdgeLength() { return 0; }
    public int getHorizontalScrollbarHeight() { return 0; }
    public boolean getKeepScreenOn() { return false; }
    public Object getKeyDispatcherState() { return null; }
    public int getLayerType() { return 0; }
    public float getLeftFadingEdgeStrength() { return 0f; }
    public int getLeftPaddingOffset() { return 0; }
    public boolean getLocalVisibleRect(Object p0) { return false; }
    public void getLocationInSurface(Object p0) {}
    public void getLocationInWindow(Object p0) {}
    public void getLocationOnScreen(Object p0) {}
    public Object getMatrix() { return null; }
    public int getMeasuredHeight() { return mMeasuredHeight; }
    public int getMeasuredState() { return 0; }
    public int getMeasuredWidth() { return mMeasuredWidth; }
    public int getMinimumHeight() { return 0; }
    public int getMinimumWidth() { return 0; }
    public Object getOnFocusChangeListener() { return null; }
    public Object getOutlineProvider() { return null; }
    public int getOverScrollMode() { return 0; }
    public Object getOverlay() { return null; }
    public int getPaddingBottom() { return mPaddingBottom; }
    public int getPaddingEnd() { return mPaddingRight; }
    public int getPaddingLeft() { return mPaddingLeft; }
    public int getPaddingRight() { return mPaddingRight; }
    public int getPaddingStart() { return mPaddingLeft; }
    public int getPaddingTop() { return mPaddingTop; }
    public Object getParent() { return mParent; }
    public Object getParentForAccessibility() { return null; }
    public Object getPointerIcon() { return null; }
    public Object getResources() { return null; }
    public boolean getRevealOnFocusHint() { return false; }
    public float getRightFadingEdgeStrength() { return 0f; }
    public int getRightPaddingOffset() { return 0; }
    public Object getRootView() {
        View root = this;
        while (root.mParent != null) root = root.mParent;
        return root;
    }
    public Object getRootWindowInsets() { return null; }
    public int getScrollBarDefaultDelayBeforeFade() { return 0; }
    public int getScrollBarFadeDuration() { return 0; }
    public int getScrollBarSize() { return 0; }
    public int getScrollIndicators() { return 0; }
    public int getScrollX() { return mScrollX; }
    public int getScrollY() { return mScrollY; }
    public Object getStateListAnimator() { return null; }
    public int getSuggestedMinimumHeight() { return 0; }
    public int getSuggestedMinimumWidth() { return 0; }
    public Object getTag(int key) { return null; }
    public float getTopFadingEdgeStrength() { return 0f; }
    public int getTopPaddingOffset() { return 0; }
    public Object getTouchDelegate() { return null; }
    public Object getTouchables() { return null; }
    public long getUniqueDrawingId() { return 0L; }
    public int getVerticalFadingEdgeLength() { return 0; }
    public int getVerticalScrollbarPosition() { return 0; }
    public int getVerticalScrollbarWidth() { return 0; }
    public Object getViewTreeObserver() { return null; }
    public int getWindowAttachCount() { return 0; }
    public Object getWindowId() { return null; }
    public Object getWindowToken() { return null; }
    public int getWindowVisibility() { return 0; }
    public void getWindowVisibleDisplayFrame(Object p0) {}
    public boolean hasExplicitFocusable() { return false; }
    public boolean hasFocus() { return false; }
    public boolean hasFocusable() { return false; }
    public boolean isFocused() { return false; }
    public boolean hasNestedScrollingParent() { return false; }
    public boolean hasOnClickListeners() { return mClickListener != null; }
    public boolean hasOnLongClickListeners() { return false; }
    public boolean hasPointerCapture() { return false; }
    public boolean hasWindowFocus() { return false; }
    public static Object inflate(Object p0, Object p1, Object p2) { return null; }
    public void invalidate() {
        // Walk up to the root and find the hosting Activity to trigger a frame render
        View root = this;
        while (root.mParent != null) root = root.mParent;
        // The root's tag may hold the Activity reference (set by Window.setContentView)
        Object tag = root.getTag();
        if (tag instanceof android.app.Activity) {
            ((android.app.Activity) tag).renderFrame();
        }
    }
    public void invalidateDrawable(Object p0) {}
    public void invalidateOutline() {}
    public boolean isAccessibilityFocused() { return false; }
    public boolean isAccessibilityHeading() { return false; }
    public boolean isAttachedToWindow() { return false; }
    public boolean isContextClickable() { return false; }
    public boolean isDirty() { return false; }
    public boolean isDuplicateParentStateEnabled() { return false; }
    public boolean isHorizontalFadingEdgeEnabled() { return false; }
    public boolean isHorizontalScrollBarEnabled() { return false; }
    public boolean isImportantForAccessibility() { return false; }
    public boolean isImportantForAutofill() { return false; }
    public boolean isImportantForContentCapture() { return false; }
    public boolean isInEditMode() { return false; }
    public boolean isInLayout() { return false; }
    public boolean isLaidOut() { return false; }
    public boolean isLayoutDirectionResolved() { return false; }
    public boolean isLayoutRequested() { return false; }
    public boolean isLongClickable() { return mLongClickable; }
    public boolean isNestedScrollingEnabled() { return false; }
    public boolean isPaddingOffsetRequired() { return false; }
    public boolean isPaddingRelative() { return false; }
    public boolean isPivotSet() { return false; }
    public boolean isSaveEnabled() { return false; }
    public boolean isSaveFromParentEnabled() { return false; }
    public boolean isScreenReaderFocusable() { return false; }
    public boolean isScrollContainer() { return false; }
    public boolean isScrollbarFadingEnabled() { return false; }
    public boolean isShowingLayoutBounds() { return false; }
    public boolean isShown() { return false; }
    public boolean isTemporarilyDetached() { return false; }
    public boolean isTextAlignmentResolved() { return false; }
    public boolean isTextDirectionResolved() { return false; }
    public boolean isVerticalFadingEdgeEnabled() { return false; }
    public boolean isVerticalScrollBarEnabled() { return false; }
    public boolean isVisibleToUserForAutofill(Object p0) { return false; }
    public Object keyboardNavigationClusterSearch(Object p0, Object p1) { return null; }
    public void layout(int l, int t, int r, int b) {
        mLeft = l; mTop = t; mRight = r; mBottom = b;
    }
    public void layout(Object p0, Object p1, Object p2, Object p3) {
        if (p0 instanceof Integer && p1 instanceof Integer && p2 instanceof Integer && p3 instanceof Integer) {
            layout((int)(Integer)p0, (int)(Integer)p1, (int)(Integer)p2, (int)(Integer)p3);
        }
    }
    // ── MeasureSpec ──
    public static class MeasureSpec {
        public static final int UNSPECIFIED = 0;
        public static final int EXACTLY = 1 << 30;
        public static final int AT_MOST = 2 << 30;
        private static final int MODE_MASK = 0x3 << 30;

        public static int makeMeasureSpec(int size, int mode) { return (size & ~MODE_MASK) | (mode & MODE_MASK); }
        public static int getMode(int measureSpec) { return measureSpec & MODE_MASK; }
        public static int getSize(int measureSpec) { return measureSpec & ~MODE_MASK; }
    }

    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void measure(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) {
            measure((int)(Integer)p0, (int)(Integer)p1);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
            getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: return size;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY: return specSize;
            default: return size;
        }
    }

    protected void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        mMeasuredWidth = measuredWidth;
        mMeasuredHeight = measuredHeight;
    }
    public static int mergeDrawableStates(Object p0, Object p1) { return 0; }
    public void offsetLeftAndRight(Object p0) {}
    public void offsetTopAndBottom(Object p0) {}
    public Object onApplyWindowInsets(Object p0) { return null; }
    public void onCancelPendingInputEvents() {}
    public boolean onCapturedPointerEvent(Object p0) { return false; }
    public boolean onCheckIsTextEditor() { return false; }
    public void onConfigurationChanged(Object p0) {}
    public void onCreateContextMenu(Object p0) {}
    public int onCreateDrawableState(Object p0) { return 0; }
    public Object onCreateInputConnection(Object p0) { return null; }
    public void onDisplayHint(Object p0) {}
    public boolean onDragEvent(Object p0) { return false; }
    // ── Canvas-based draw traversal (AOSP pattern) ──
    public void draw(android.graphics.Canvas canvas) {
        // Step 0: Apply alpha via saveLayerAlpha if not fully opaque
        boolean needsAlphaRestore = false;
        if (mAlpha < 1.0f) {
            int alphaInt = Math.round(mAlpha * 255);
            canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), alphaInt);
            needsAlphaRestore = true;
        }
        // Step 1: Draw background
        if (mBackgroundColor != 0) {
            canvas.drawColor(mBackgroundColor);
        }
        if (mBackground != null) {
            mBackground.setBounds(0, 0, getWidth(), getHeight());
            mBackground.draw(canvas);
        }
        // Step 2: Apply scroll offset
        if (mScrollX != 0 || mScrollY != 0) {
            canvas.save();
            canvas.translate(-mScrollX, -mScrollY);
        }
        // Step 3: Draw content
        onDraw(canvas);
        // Step 4: Dispatch to children (ViewGroup overrides)
        dispatchDraw(canvas);
        // Restore scroll
        if (mScrollX != 0 || mScrollY != 0) {
            canvas.restore();
        }
        // Restore alpha
        if (needsAlphaRestore) {
            canvas.restore();
        }
    }
    protected void onDraw(android.graphics.Canvas canvas) {}
    protected void dispatchDraw(android.graphics.Canvas canvas) {}

    public void onDraw(Object p0) {}
    public void onDrawForeground(Object p0) {}
    public void onDrawScrollBars(Object p0) {}
    public boolean onFilterTouchEventForSecurity(Object p0) { return false; }
    public void onFinishTemporaryDetach() {}
    public boolean onGenericMotionEvent(Object p0) { return false; }
    public void onHoverChanged(Object p0) {}
    public boolean onHoverEvent(Object p0) { return false; }
    public boolean onKeyDown(int keyCode, KeyEvent event) { return false; }
    public boolean onKeyDown(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof KeyEvent) return onKeyDown((Integer) p0, (KeyEvent) p1);
        return false;
    }
    public boolean onKeyLongPress(Object p0, Object p1) { return false; }
    public boolean onKeyMultiple(Object p0, Object p1, Object p2) { return false; }
    public boolean onKeyPreIme(Object p0, Object p1) { return false; }
    public boolean onKeyShortcut(Object p0, Object p1) { return false; }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mClickable && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            performClick();
            return true;
        }
        return false;
    }
    public boolean onKeyUp(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof KeyEvent) return onKeyUp((Integer) p0, (KeyEvent) p1);
        return false;
    }
    public void onLayout(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void onMeasure(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) {
            onMeasure((int)(Integer)p0, (int)(Integer)p1);
        }
    }
    public void onOverScrolled(Object p0, Object p1, Object p2, Object p3) {}
    public void onProvideAutofillStructure(Object p0, Object p1) {}
    public void onProvideAutofillVirtualStructure(Object p0, Object p1) {}
    public void onProvideContentCaptureStructure(Object p0, Object p1) {}
    public void onProvideStructure(Object p0) {}
    public void onProvideVirtualStructure(Object p0) {}
    public Object onResolvePointerIcon(Object p0, Object p1) { return null; }
    public void onRtlPropertiesChanged(Object p0) {}
    public void onScreenStateChanged(Object p0) {}
    public void onScrollChanged(Object p0, Object p1, Object p2, Object p3) {}
    public boolean onSetAlpha(Object p0) { return false; }
    public void onSizeChanged(Object p0, Object p1, Object p2, Object p3) {}
    public void onStartTemporaryDetach() {}
    public boolean onTouchEvent(MotionEvent event) {
        if (mClickable || mLongClickable) {
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                performClick();
            }
            return true;
        }
        return false;
    }
    public boolean onTouchEvent(Object p0) {
        if (p0 instanceof MotionEvent) return onTouchEvent((MotionEvent) p0);
        return false;
    }
    public boolean onTrackballEvent(Object p0) { return false; }
    public void onVisibilityChanged(Object p0, Object p1) {}
    public void onWindowFocusChanged(Object p0) {}
    public void onWindowVisibilityChanged(Object p0) {}
    public boolean overScrollBy(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) { return false; }
    public boolean performAccessibilityAction(Object p0, Object p1) { return false; }
    public boolean performClick() {
        if (mClickListener != null) {
            mClickListener.onClick(this);
            return true;
        }
        return false;
    }
    public boolean performContextClick(Object p0, Object p1) { return false; }
    public boolean performContextClick() { return false; }
    public boolean performHapticFeedback(Object p0) { return false; }
    public boolean performHapticFeedback(Object p0, Object p1) { return false; }
    public boolean performLongClick() { return false; }
    public boolean performLongClick(Object p0, Object p1) { return false; }
    public void playSoundEffect(Object p0) {}
    public boolean post(Object p0) { return false; }
    public boolean postDelayed(Object p0, Object p1) { return false; }
    public void postInvalidate() {}
    public void postInvalidate(Object p0, Object p1, Object p2, Object p3) {}
    public void postInvalidateDelayed(Object p0) {}
    public void postInvalidateDelayed(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void postInvalidateOnAnimation() {}
    public void postInvalidateOnAnimation(Object p0, Object p1, Object p2, Object p3) {}
    public void postOnAnimation(Object p0) {}
    public void postOnAnimationDelayed(Object p0, Object p1) {}
    public void refreshDrawableState() {}
    public void releasePointerCapture() {}
    public boolean removeCallbacks(Object p0) { return false; }
    public void removeOnAttachStateChangeListener(Object p0) {}
    public void removeOnLayoutChangeListener(Object p0) {}
    public void removeOnUnhandledKeyEventListener(Object p0) {}
    public void requestApplyInsets() {}
    public boolean requestFocus() { return false; }
    public boolean requestFocus(Object p0) { return false; }
    public boolean requestFocus(Object p0, Object p1) { return false; }
    public boolean requestFocusFromTouch() { return false; }
    public void requestPointerCapture() {}
    public boolean requestRectangleOnScreen(Object p0) { return false; }
    public boolean requestRectangleOnScreen(Object p0, Object p1) { return false; }
    public void requestUnbufferedDispatch(Object p0) {}
    public void resetPivot() {}
    public static int resolveSize(Object p0, Object p1) { return 0; }
    public static int resolveSizeAndState(Object p0, Object p1, Object p2) { return 0; }
    public boolean restoreDefaultFocus() { return false; }
    public void restoreHierarchyState(Object p0) {}
    public void saveAttributeDataForStyleable(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void saveHierarchyState(Object p0) {}
    public void scheduleDrawable(Object p0, Object p1, Object p2) {}
    public void scrollTo(int x, int y) { mScrollX = x; mScrollY = y; }
    public void scrollBy(int dx, int dy) { scrollTo(mScrollX + dx, mScrollY + dy); }
    public void scrollBy(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) scrollBy((int)(Integer)p0, (int)(Integer)p1);
    }
    public void scrollTo(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) scrollTo((int)(Integer)p0, (int)(Integer)p1);
    }
    public void sendAccessibilityEvent(Object p0) {}
    public void sendAccessibilityEventUnchecked(Object p0) {}
    public void setAccessibilityDelegate(Object p0) {}
    public void setAccessibilityHeading(Object p0) {}
    public void setAccessibilityLiveRegion(Object p0) {}
    public void setAccessibilityPaneTitle(Object p0) {}
    public void setAccessibilityTraversalAfter(Object p0) {}
    public void setAccessibilityTraversalBefore(Object p0) {}
    public void setActivated(Object p0) {}
    // setAlpha(Object,Object) removed — use setAlpha(float)
    public void setAnimation(Object p0) {}
    public void setAnimationMatrix(Object p0) {}
    public void setAutofillHints(Object p0) {}
    public void setAutofillId(Object p0) {}
    public void setBackground(Object p0) {
        if (p0 instanceof android.graphics.drawable.Drawable) {
            mBackground = (android.graphics.drawable.Drawable) p0;
        }
    }
    public android.graphics.drawable.Drawable getBackgroundDrawable() { return mBackground; }
    public void setBackgroundDrawable(android.graphics.drawable.Drawable d) { mBackground = d; }
    public void setBackgroundResource(Object p0) {}
    public void setBackgroundTintBlendMode(Object p0) {}
    public void setBackgroundTintList(Object p0) {}
    public void setBackgroundTintMode(Object p0) {}
    public void setBottom(Object p0) {}
    public void setCameraDistance(Object p0) {}
    public void setClickable(Object p0) {
        if (p0 instanceof Boolean) mClickable = (Boolean) p0;
    }
    public void setClipBounds(Object p0) {}
    public void setClipToOutline(Object p0) {}
    public void setContentCaptureSession(Object p0) {}
    public void setContentDescription(Object p0) {}
    public void setContextClickable(Object p0) {}
    public void setDefaultFocusHighlightEnabled(Object p0) {}
    public void setDuplicateParentStateEnabled(Object p0) {}
    public void setElevation(Object p0) {}
    // setEnabled(Object) removed — use setEnabled(boolean)
    public void setFadingEdgeLength(Object p0) {}
    public void setFilterTouchesWhenObscured(Object p0) {}
    public void setFitsSystemWindows(Object p0) {}
    public void setFocusable(Object p0) {}
    public void setFocusableInTouchMode(Object p0) {}
    public void setFocusedByDefault(Object p0) {}
    public void setForceDarkAllowed(Object p0) {}
    public void setForeground(Object p0) {}
    public void setForegroundGravity(Object p0) {}
    public void setForegroundTintBlendMode(Object p0) {}
    public void setForegroundTintList(Object p0) {}
    public void setForegroundTintMode(Object p0) {}
    public void setHapticFeedbackEnabled(Object p0) {}
    public void setHasTransientState(Object p0) {}
    public void setHorizontalFadingEdgeEnabled(Object p0) {}
    public void setHorizontalScrollBarEnabled(Object p0) {}
    public void setHorizontalScrollbarThumbDrawable(Object p0) {}
    public void setHorizontalScrollbarTrackDrawable(Object p0) {}
    public void setHovered(Object p0) {}
    // setId(Object) removed — use setId(int)
    public void setImportantForAccessibility(Object p0) {}
    public void setImportantForAutofill(Object p0) {}
    public void setImportantForContentCapture(Object p0) {}
    public void setKeepScreenOn(Object p0) {}
    public void setKeyboardNavigationCluster(Object p0) {}
    public void setLabelFor(Object p0) {}
    public void setLayerPaint(Object p0) {}
    public void setLayerType(Object p0, Object p1) {}
    public void setLayoutDirection(Object p0) {}
    public void setLayoutParams(Object p0) { mLayoutParams = p0; }
    public Object getLayoutParams() { return mLayoutParams; }
    public void setLeft(Object p0) {}
    public void setLeftTopRightBottom(Object p0, Object p1, Object p2, Object p3) {}
    public void setLongClickable(Object p0) {
        if (p0 instanceof Boolean) mLongClickable = (Boolean) p0;
    }
    public void setMeasuredDimension(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) {
            setMeasuredDimension((int)(Integer)p0, (int)(Integer)p1);
        }
    }
    public void setMinimumHeight(Object p0) {}
    public void setMinimumWidth(Object p0) {}
    public void setNestedScrollingEnabled(Object p0) {}
    public void setNextClusterForwardId(Object p0) {}
    public void setNextFocusDownId(Object p0) {}
    public void setNextFocusForwardId(Object p0) {}
    public void setNextFocusLeftId(Object p0) {}
    public void setNextFocusRightId(Object p0) {}
    public void setNextFocusUpId(Object p0) {}
    public void setOnApplyWindowInsetsListener(Object p0) {}
    public void setOnCapturedPointerListener(Object p0) {}
    // setOnClickListener(Object) removed — use setOnClickListener(OnClickListener)
    public void setOnContextClickListener(Object p0) {}
    public void setOnCreateContextMenuListener(Object p0) {}
    public void setOnDragListener(Object p0) {}
    public void setOnFocusChangeListener(Object p0) {}
    public void setOnGenericMotionListener(Object p0) {}
    public void setOnHoverListener(Object p0) {}
    public void setOnKeyListener(Object p0) {
        if (p0 instanceof OnKeyListener) mKeyListener = (OnKeyListener) p0;
    }
    public void setOnLongClickListener(Object p0) {}
    public void setOnScrollChangeListener(Object p0) {}
    public void setOnTouchListener(Object p0) {
        if (p0 instanceof OnTouchListener) mTouchListener = (OnTouchListener) p0;
    }
    public void setOutlineAmbientShadowColor(Object p0) {}
    public void setOutlineProvider(Object p0) {}
    public void setOutlineSpotShadowColor(Object p0) {}
    public void setOverScrollMode(Object p0) {}
    // setPadding(Object...) removed — use setPadding(int,int,int,int)
    public void setPaddingRelative(Object p0, Object p1, Object p2, Object p3) {}
    public void setPivotX(Object p0) {}
    public void setPivotY(Object p0) {}
    public void setPointerIcon(Object p0) {}
    public void setPressed(Object p0) {}
    public void setRevealOnFocusHint(Object p0) {}
    public void setRight(Object p0) {}
    public void setRotation(Object p0) {}
    public void setRotationX(Object p0) {}
    public void setRotationY(Object p0) {}
    public void setSaveEnabled(Object p0) {}
    public void setSaveFromParentEnabled(Object p0) {}
    public void setScaleX(Object p0) {}
    public void setScaleY(Object p0) {}
    public void setScreenReaderFocusable(Object p0) {}
    public void setScrollBarDefaultDelayBeforeFade(Object p0) {}
    public void setScrollBarFadeDuration(Object p0) {}
    public void setScrollBarSize(Object p0) {}
    public void setScrollBarStyle(Object p0) {}
    public void setScrollContainer(Object p0) {}
    public void setScrollIndicators(Object p0) {}
    public void setScrollIndicators(Object p0, Object p1) {}
    public void setScrollX(int x) { mScrollX = x; }
    public void setScrollY(int y) { mScrollY = y; }
    public void setScrollX(Object p0) { if (p0 instanceof Integer) mScrollX = (Integer) p0; }
    public void setScrollY(Object p0) { if (p0 instanceof Integer) mScrollY = (Integer) p0; }
    public void setScrollbarFadingEnabled(Object p0) {}
    public void setSelected(Object p0) {}
    public void setSoundEffectsEnabled(Object p0) {}
    public void setStateDescription(Object p0) {}
    public void setStateListAnimator(Object p0) {}
    public void setSystemGestureExclusionRects(Object p0) {}
    // setTag(Object) defined above
    public void setTag(int key, Object tag) {}
    public void setTextAlignment(Object p0) {}
    public void setTextDirection(Object p0) {}
    public void setTooltipText(Object p0) {}
    public void setTop(Object p0) {}
    public void setTouchDelegate(Object p0) {}
    public void setTransitionAlpha(Object p0) {}
    public void setTransitionName(Object p0) {}
    public void setTransitionVisibility(Object p0) {}
    public float getTranslationX() { return mTranslationX; }
    public float getTranslationY() { return mTranslationY; }
    public void setTranslationX(float tx) { mTranslationX = tx; }
    public void setTranslationY(float ty) { mTranslationY = ty; }
    public void setTranslationX(Object p0) { if (p0 instanceof Number) mTranslationX = ((Number) p0).floatValue(); }
    public void setTranslationY(Object p0) { if (p0 instanceof Number) mTranslationY = ((Number) p0).floatValue(); }
    public void setTranslationZ(Object p0) {}
    public void setVerticalFadingEdgeEnabled(Object p0) {}
    public void setVerticalScrollBarEnabled(Object p0) {}
    public void setVerticalScrollbarPosition(Object p0) {}
    public void setVerticalScrollbarThumbDrawable(Object p0) {}
    public void setVerticalScrollbarTrackDrawable(Object p0) {}
    // setVisibility(Object) removed — use setVisibility(int)
    public void setWillNotDraw(Object p0) {}
    public void setWindowInsetsAnimationCallback(Object p0) {}
    public void setX(Object p0) {}
    public void setY(Object p0) {}
    public void setZ(Object p0) {}
    public boolean showContextMenu() { return false; }
    public boolean showContextMenu(Object p0, Object p1) { return false; }
    public Object startActionMode(Object p0) { return null; }
    public Object startActionMode(Object p0, Object p1) { return null; }
    public void startAnimation(Object p0) {}
    public boolean startDragAndDrop(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean startNestedScroll(Object p0) { return false; }
    public void stopNestedScroll() {}
    public void transformMatrixToGlobal(Object p0) {}
    public void transformMatrixToLocal(Object p0) {}
    public void unscheduleDrawable(Object p0, Object p1) {}
    public void unscheduleDrawable(Object p0) {}
    public void updateDragShadow(Object p0) {}
}
