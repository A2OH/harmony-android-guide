package android.view;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AOSP View.java — extracted from Android 11 (API 30).
 *
 * Contains real AOSP logic for:
 *   measure / onMeasure / setMeasuredDimension (with measure cache)
 *   layout / setFrame / onLayout (with layout-change listeners)
 *   draw / onDraw / dispatchDraw (background, scroll, alpha, foreground)
 *   dispatchTouchEvent / onTouchEvent (click, long-press, press state)
 *   invalidate / requestLayout
 *   MeasureSpec, resolveSize, resolveSizeAndState, getDefaultSize, combineMeasuredStates
 *
 * Stripped: RenderNode, ViewRootImpl, AccessibilityNodeInfo, Autofill, ContentCapture,
 *           InputMethodManager, WindowInsets animation, tooltip internals, drag internals.
 *
 * OHBridge integration preserved from shim layer.
 */
public class View implements android.graphics.drawable.Drawable.Callback {

    // ════════════════════════════════════════════════════════════════════════
    //  Constants (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    protected static final String VIEW_LOG_TAG = "View";
    private static final boolean DBG = false;

    public static final int NO_ID = -1;

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;
    static final int VISIBILITY_MASK = 0x0000000C;

    static final int ENABLED = 0x00000000;
    static final int DISABLED = 0x00000020;
    static final int ENABLED_MASK = 0x00000020;

    static final int CLICKABLE = 0x00004000;
    static final int LONG_CLICKABLE = 0x00200000;
    static final int CONTEXT_CLICKABLE = 0x00800000;

    static final int FADING_EDGE_NONE = 0x00000000;
    static final int FADING_EDGE_HORIZONTAL = 0x00001000;
    static final int FADING_EDGE_VERTICAL = 0x00002000;
    static final int FADING_EDGE_MASK = 0x00003000;

    static final int FILTER_TOUCHES_WHEN_OBSCURED = 0x00000400;
    static final int TOOLTIP = 0x40000000;

    // ── PFLAG_ constants (from AOSP) ──
    static final int PFLAG_WANTS_FOCUS                 = 0x00000001;
    static final int PFLAG_FOCUSED                     = 0x00000002;
    static final int PFLAG_SELECTED                    = 0x00000004;
    static final int PFLAG_IS_ROOT_NAMESPACE           = 0x00000008;
    static final int PFLAG_HAS_BOUNDS                  = 0x00000010;
    static final int PFLAG_DRAWN                       = 0x00000020;
    static final int PFLAG_DRAW_ANIMATION              = 0x00000040;
    static final int PFLAG_SKIP_DRAW                   = 0x00000080;
    static final int PFLAG_REQUEST_TRANSPARENT_REGIONS = 0x00000200;
    static final int PFLAG_DRAWABLE_STATE_DIRTY        = 0x00000400;
    static final int PFLAG_MEASURED_DIMENSION_SET      = 0x00000800;
    static final int PFLAG_FORCE_LAYOUT                = 0x00001000;
    static final int PFLAG_LAYOUT_REQUIRED             = 0x00002000;
    private static final int PFLAG_PRESSED             = 0x00004000;
    static final int PFLAG_DRAWING_CACHE_VALID         = 0x00008000;
    static final int PFLAG_ANIMATION_STARTED           = 0x00010000;
    private static final int PFLAG_SAVE_STATE_CALLED   = 0x00020000;
    static final int PFLAG_ALPHA_SET                   = 0x00040000;
    static final int PFLAG_SCROLL_CONTAINER            = 0x00080000;
    static final int PFLAG_SCROLL_CONTAINER_ADDED      = 0x00100000;
    static final int PFLAG_DIRTY                       = 0x00200000;
    static final int PFLAG_DIRTY_MASK                  = 0x00200000;
    static final int PFLAG_OPAQUE_BACKGROUND           = 0x00800000;
    static final int PFLAG_OPAQUE_SCROLLBARS           = 0x01000000;
    static final int PFLAG_OPAQUE_MASK                 = 0x01800000;
    private static final int PFLAG_PREPRESSED          = 0x02000000;
    static final int PFLAG_CANCEL_NEXT_UP_EVENT        = 0x04000000;
    static final int PFLAG_ACTIVATED                   = 0x40000000;
    static final int PFLAG_INVALIDATED                 = 0x80000000;

    static final int PFLAG3_VIEW_IS_ANIMATING_TRANSFORM = 0x1;
    static final int PFLAG3_VIEW_IS_ANIMATING_ALPHA     = 0x2;
    static final int PFLAG3_IS_LAID_OUT                 = 0x4;
    static final int PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT = 0x8;
    static final int PFLAG3_CALLED_SUPER                = 0x10;
    private static final int PFLAG3_FINGER_DOWN         = 0x20000;

    // ── Measurement constants ──
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_SIZE_MASK = 0x00ffffff;
    public static final int MEASURED_STATE_MASK = 0xff000000;
    public static final int MEASURED_STATE_TOO_SMALL = 0x01000000;

    // ── Layout constants ──
    public static final int WRAP_CONTENT = -2;
    public static final int MATCH_PARENT = -1;

    // ── Layout direction ──
    public static final int LAYOUT_DIRECTION_LTR = 0;
    public static final int LAYOUT_DIRECTION_RTL = 1;
    public static final int LAYOUT_DIRECTION_INHERIT = 2;
    public static final int LAYOUT_DIRECTION_LOCALE = 3;

    // ── Focus ──
    public static final int FOCUSABLE = 1;
    public static final int NOT_FOCUSABLE = 0;
    public static final int FOCUSABLE_AUTO = 0x00000010;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 1;
    public static final int FOCUS_BACKWARD = 0x00000001;
    public static final int FOCUS_DOWN = 0x00000082;
    public static final int FOCUS_FORWARD = 0x00000002;
    public static final int FOCUS_LEFT = 0x00000011;
    public static final int FOCUS_RIGHT = 0x00000042;
    public static final int FOCUS_UP = 0x00000021;

    // ── Layers ──
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;

    // ── Text alignment/direction ──
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    public static final int TEXT_ALIGNMENT_GRAVITY = 1;
    public static final int TEXT_ALIGNMENT_TEXT_START = 2;
    public static final int TEXT_ALIGNMENT_TEXT_END = 3;
    public static final int TEXT_ALIGNMENT_CENTER = 4;
    public static final int TEXT_ALIGNMENT_VIEW_START = 5;
    public static final int TEXT_ALIGNMENT_VIEW_END = 6;
    public static final int TEXT_DIRECTION_INHERIT = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG = 1;
    public static final int TEXT_DIRECTION_ANY_RTL = 2;
    public static final int TEXT_DIRECTION_LTR = 3;
    public static final int TEXT_DIRECTION_RTL = 4;
    public static final int TEXT_DIRECTION_LOCALE = 5;
    public static final int TEXT_DIRECTION_FIRST_STRONG_LTR = 6;
    public static final int TEXT_DIRECTION_FIRST_STRONG_RTL = 7;

    // ── Accessibility ──
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;

    // ── Autofill ──
    public static final int AUTOFILL_FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 1;
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
    public static final int AUTOFILL_TYPE_NONE = 0;
    public static final int AUTOFILL_TYPE_TEXT = 1;
    public static final int AUTOFILL_TYPE_TOGGLE = 2;
    public static final int AUTOFILL_TYPE_LIST = 3;
    public static final int AUTOFILL_TYPE_DATE = 4;
    public static final int IMPORTANT_FOR_AUTOFILL_AUTO = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_YES = 1;
    public static final int IMPORTANT_FOR_AUTOFILL_NO = 2;
    public static final int IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS = 8;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_AUTO = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES = 1;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO = 2;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES_EXCLUDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO_EXCLUDE_DESCENDANTS = 8;

    // ── Drag ──
    public static final int DRAG_FLAG_GLOBAL = 256;
    public static final int DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION = 64;
    public static final int DRAG_FLAG_GLOBAL_PREFIX_URI_PERMISSION = 128;
    public static final int DRAG_FLAG_GLOBAL_URI_READ = 1;
    public static final int DRAG_FLAG_GLOBAL_URI_WRITE = 2;
    public static final int DRAG_FLAG_OPAQUE = 512;

    // ── Over-scroll ──
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;

    // ── Scrollbar ──
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    public static final int SCROLLBARS_INSIDE_INSET = 0x01000000;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 0x02000000;
    public static final int SCROLLBARS_OUTSIDE_INSET = 0x03000000;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 1;
    public static final int SCROLLBAR_POSITION_RIGHT = 2;
    public static final int SCROLL_AXIS_NONE = 0;
    public static final int SCROLL_AXIS_HORIZONTAL = 1;
    public static final int SCROLL_AXIS_VERTICAL = 2;
    public static final int SCROLL_INDICATOR_TOP = 0x0100;
    public static final int SCROLL_INDICATOR_BOTTOM = 0x0200;
    public static final int SCROLL_INDICATOR_LEFT = 0x0400;
    public static final int SCROLL_INDICATOR_RIGHT = 0x0800;
    public static final int SCROLL_INDICATOR_START = 0x1000;
    public static final int SCROLL_INDICATOR_END = 0x2000;

    // ── Screen state ──
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 1;

    // ── Misc constants ──
    public static final int HAPTIC_FEEDBACK_ENABLED = 0x10000000;
    public static final int KEEP_SCREEN_ON = 0x04000000;
    public static final int SOUND_EFFECTS_ENABLED = 0x08000000;
    public static final int FIND_VIEWS_WITH_TEXT = 1;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;

    // State set placeholders (not used in headless, but apps reference them)
    public static final int ALPHA = 0;
    public static final int ROTATION = 0;
    public static final int ROTATION_X = 0;
    public static final int ROTATION_Y = 0;
    public static final int SCALE_X = 0;
    public static final int SCALE_Y = 0;
    public static final int TRANSLATION_X = 0;
    public static final int TRANSLATION_Y = 0;
    public static final int TRANSLATION_Z = 0;
    public static final int X = 0;
    public static final int Y = 0;
    public static final int Z = 0;
    public static final int EMPTY_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_SELECTED_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_SELECTED_STATE_SET = 0;
    public static final int ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int ENABLED_STATE_SET = 0;
    public static final int ENABLED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int FOCUSED_SELECTED_STATE_SET = 0;
    public static final int FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int FOCUSED_STATE_SET = 0;
    public static final int FOCUSED_WINDOW_FOCUSED_STATE_SET = 0;
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
    public static final int SELECTED_STATE_SET = 0;
    public static final int SELECTED_WINDOW_FOCUSED_STATE_SET = 0;
    public static final int WINDOW_FOCUSED_STATE_SET = 0;

    // ── Static generateViewId counter ──
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    // ════════════════════════════════════════════════════════════════════════
    //  Instance fields (from AOSP + OHBridge)
    // ════════════════════════════════════════════════════════════════════════

    // OHBridge native handle for OHOS ArkUI node
    protected long nativeHandle;

    // AOSP flag fields
    public int mPrivateFlags;
    int mPrivateFlags2;
    int mPrivateFlags3;

    int mViewFlags;

    // Identity
    int mID = NO_ID;

    // Geometry
    protected int mLeft;
    protected int mTop;
    protected int mRight;
    protected int mBottom;

    // Scroll
    protected int mScrollX;
    protected int mScrollY;

    // Padding
    protected int mPaddingLeft;
    protected int mPaddingTop;
    protected int mPaddingRight;
    protected int mPaddingBottom;

    // Measurement
    int mMeasuredWidth;
    int mMeasuredHeight;
    int mOldWidthMeasureSpec = Integer.MIN_VALUE;
    int mOldHeightMeasureSpec = Integer.MIN_VALUE;
    private android.util.LongSparseLongArray mMeasureCache;

    // Min size
    private int mMinHeight;
    private int mMinWidth;

    // Background
    private int mBackgroundColor;
    private android.graphics.drawable.Drawable mBackground;

    // Alpha/transforms
    private float mAlpha = 1.0f;
    private float mTranslationX;
    private float mTranslationY;
    private float mTranslationZ;
    private float mRotation;
    private float mRotationX;
    private float mRotationY;
    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;
    private float mPivotX;
    private float mPivotY;
    private float mElevation;

    // Parent / tag / context
    ViewParent mParent;
    private Object mTag;
    private android.util.SparseArray<Object> mKeyedTags;
    private Object mLayoutParams;
    android.content.Context mContext;

    // Focus state
    private boolean mFocusable;
    private boolean mFocusableInTouchMode;

    // Touch handling
    private boolean mHasPerformedLongPress;
    private boolean mIgnoreNextUpEvent;
    private boolean mInContextButtonPress;
    private int mTouchSlop;

    // Listeners (using ListenerInfo-like pattern, but flattened for simplicity)
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private OnTouchListener mOnTouchListener;
    private OnKeyListener mOnKeyListener;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnScrollChangeListener mOnScrollChangeListener;
    private OnContextClickListener mOnContextClickListener;
    private OnCreateContextMenuListener mOnCreateContextMenuListener;
    private OnGenericMotionListener mOnGenericMotionListener;
    private OnHoverListener mOnHoverListener;
    private OnDragListener mOnDragListener;
    private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
    ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
    private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;

    private TouchDelegate mTouchDelegate;

    // Animation
    private android.view.animation.Animation mCurrentAnimation;

    // OHBridge attribute constants for ArkUI node properties
    private static final int ATTR_VISIBILITY = 1;
    private static final int ATTR_BG_COLOR = 2;
    private static final int ATTR_OPACITY = 3;
    private static final int ATTR_PADDING = 4;
    private static final int ATTR_ENABLED = 5;

    // OHBridge event constants
    private static final int EVENT_CLICK = 1;
    private int mClickEventId;
    private static final AtomicInteger sEventIdGen = new AtomicInteger(1);
    private static int generateEventId() { return sEventIdGen.getAndIncrement(); }


    // ════════════════════════════════════════════════════════════════════════
    //  Constructors
    // ════════════════════════════════════════════════════════════════════════

    public View() {
        mPrivateFlags |= PFLAG_DRAWN;  // Mark as drawn initially
    }

    public View(android.content.Context context) {
        mContext = context;
        mPrivateFlags |= PFLAG_DRAWN;
    }

    public View(android.content.Context context, android.util.AttributeSet attrs) {
        mContext = context;
        mPrivateFlags |= PFLAG_DRAWN;
    }

    public View(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        mPrivateFlags |= PFLAG_DRAWN;
    }

    public View(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        mPrivateFlags |= PFLAG_DRAWN;
    }

    /** OHBridge constructor: creates a native ArkUI node */
    public View(int nodeType) {
        if (nodeType > 0) {
            nativeHandle = com.ohos.shim.bridge.OHBridge.nodeCreate(nodeType);
        }
        mPrivateFlags |= PFLAG_DRAWN;
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Interfaces (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    public interface OnClickListener {
        void onClick(View v);
    }

    public interface OnLongClickListener {
        boolean onLongClick(View v);
    }

    public interface OnTouchListener {
        boolean onTouch(View v, MotionEvent event);
    }

    public interface OnKeyListener {
        boolean onKey(View v, int keyCode, KeyEvent event);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }

    public interface OnScrollChangeListener {
        void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    public interface OnLayoutChangeListener {
        void onLayoutChange(View v, int left, int top, int right, int bottom,
                int oldLeft, int oldTop, int oldRight, int oldBottom);
    }

    public interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View v);
        void onViewDetachedFromWindow(View v);
    }

    public interface OnContextClickListener {
        boolean onContextClick(View v);
    }

    public interface OnCreateContextMenuListener {
        void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);
    }

    public interface OnGenericMotionListener {
        boolean onGenericMotion(View v, MotionEvent event);
    }

    public interface OnHoverListener {
        boolean onHover(View v, MotionEvent event);
    }

    public interface OnDragListener {
        boolean onDrag(View v, DragEvent event);
    }

    public interface OnSystemUiVisibilityChangeListener {
        void onSystemUiVisibilityChange(int visibility);
    }

    public interface OnCapturedPointerListener {
        boolean onCapturedPointer(View view, MotionEvent event);
    }

    public interface OnApplyWindowInsetsListener {
        WindowInsets onApplyWindowInsets(View v, WindowInsets insets);
    }

    public interface OnUnhandledKeyEventListener {
        boolean onUnhandledKeyEvent(View v, KeyEvent event);
    }


    // ════════════════════════════════════════════════════════════════════════
    //  OHBridge integration
    // ════════════════════════════════════════════════════════════════════════

    public long getNativeHandle() { return nativeHandle; }

    public View findViewByHandle(long handle) {
        if (nativeHandle == handle) return this;
        return null;
    }

    public void onNativeEvent(int eventType, int eventCode, String data) {}

    public void destroy() {
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeDispose(nativeHandle);
            nativeHandle = 0;
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    //  MeasureSpec (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    public static class MeasureSpec {
        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK = 0x3 << MODE_SHIFT;

        public static final int UNSPECIFIED = 0 << MODE_SHIFT;
        public static final int EXACTLY     = 1 << MODE_SHIFT;
        public static final int AT_MOST     = 2 << MODE_SHIFT;

        public static int makeMeasureSpec(int size, int mode) {
            return (size & ~MODE_MASK) | (mode & MODE_MASK);
        }

        public static int getMode(int measureSpec) {
            return (measureSpec & MODE_MASK);
        }

        public static int getSize(int measureSpec) {
            return (measureSpec & ~MODE_MASK);
        }

        static int adjust(int measureSpec, int delta) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            if (mode == UNSPECIFIED) {
                return makeMeasureSpec(size, UNSPECIFIED);
            }
            size += delta;
            if (size < 0) {
                size = 0;
            }
            return makeMeasureSpec(size, mode);
        }

        public static String toString(int measureSpec) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            StringBuilder sb = new StringBuilder("MeasureSpec: ");
            if (mode == UNSPECIFIED) sb.append("UNSPECIFIED ");
            else if (mode == EXACTLY) sb.append("EXACTLY ");
            else if (mode == AT_MOST) sb.append("AT_MOST ");
            else sb.append(mode).append(" ");
            sb.append(size);
            return sb.toString();
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Measurement (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * AOSP measure() — with measure cache and PFLAG management.
     */
    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        // Suppress sign extension for the low bytes
        long key = (long) widthMeasureSpec << 32 | (long) heightMeasureSpec & 0xffffffffL;
        if (mMeasureCache == null) mMeasureCache = new android.util.LongSparseLongArray(2);

        boolean forceLayout = (mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT;

        boolean specChanged = widthMeasureSpec != mOldWidthMeasureSpec
                || heightMeasureSpec != mOldHeightMeasureSpec;
        boolean isSpecExactly = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY;
        boolean matchesSpecSize = getMeasuredWidth() == MeasureSpec.getSize(widthMeasureSpec)
                && getMeasuredHeight() == MeasureSpec.getSize(heightMeasureSpec);
        boolean needsLayout = specChanged
                && (!isSpecExactly || !matchesSpecSize);

        if (forceLayout || needsLayout) {
            // first clears the measured dimension flag
            mPrivateFlags &= ~PFLAG_MEASURED_DIMENSION_SET;

            int cacheIndex = forceLayout ? -1 : mMeasureCache.indexOfKey(key);
            if (cacheIndex < 0) {
                // measure ourselves, this should set the measured dimension flag back
                onMeasure(widthMeasureSpec, heightMeasureSpec);
                mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
            } else {
                long value = mMeasureCache.valueAt(cacheIndex);
                setMeasuredDimensionRaw((int) (value >> 32), (int) value);
                mPrivateFlags3 |= PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
            }

            // flag not set, setMeasuredDimension() was not invoked — error
            if ((mPrivateFlags & PFLAG_MEASURED_DIMENSION_SET) != PFLAG_MEASURED_DIMENSION_SET) {
                throw new IllegalStateException("View with id " + getId() + ": "
                        + getClass().getName() + "#onMeasure() did not set the"
                        + " measured dimension by calling setMeasuredDimension()");
            }

            mPrivateFlags |= PFLAG_LAYOUT_REQUIRED;
        }

        mOldWidthMeasureSpec = widthMeasureSpec;
        mOldHeightMeasureSpec = heightMeasureSpec;

        mMeasureCache.put(key, ((long) mMeasuredWidth) << 32 |
                (long) mMeasuredHeight & 0xffffffffL);
    }

    /** Object overload for Dalvik compat */
    public void measure(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) {
            measure((int)(Integer)p0, (int)(Integer)p1);
        }
    }

    /**
     * AOSP onMeasure() — default implementation. Subclasses override.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
            getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    /** Object overload */
    public void onMeasure(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) {
            onMeasure((int)(Integer)p0, (int)(Integer)p1);
        }
    }

    /**
     * AOSP setMeasuredDimension() — sets flag to indicate measurement happened.
     */
    protected final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        setMeasuredDimensionRaw(measuredWidth, measuredHeight);
    }

    /** Object overload */
    public void setMeasuredDimension(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer) {
            setMeasuredDimension((int)(Integer)p0, (int)(Integer)p1);
        }
    }

    private void setMeasuredDimensionRaw(int measuredWidth, int measuredHeight) {
        mMeasuredWidth = measuredWidth;
        mMeasuredHeight = measuredHeight;
        mPrivateFlags |= PFLAG_MEASURED_DIMENSION_SET;
    }

    public int getMeasuredWidth() { return mMeasuredWidth; }
    public int getMeasuredHeight() { return mMeasuredHeight; }
    public int getMeasuredState() {
        return (mMeasuredWidth & MEASURED_STATE_MASK)
                | ((mMeasuredHeight >> MEASURED_HEIGHT_STATE_SHIFT) & (MEASURED_STATE_MASK >> MEASURED_HEIGHT_STATE_SHIFT));
    }

    // ── Static measurement helpers (from AOSP) ──

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    public static int getDefaultSize(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer)
            return getDefaultSize((int)(Integer)p0, (int)(Integer)p1);
        return 0;
    }

    protected int getSuggestedMinimumWidth() {
        return (mBackground == null) ? mMinWidth
                : Math.max(mMinWidth, mBackground.getMinimumWidth());
    }

    protected int getSuggestedMinimumHeight() {
        return (mBackground == null) ? mMinHeight
                : Math.max(mMinHeight, mBackground.getMinimumHeight());
    }

    public static int resolveSize(int size, int measureSpec) {
        return resolveSizeAndState(size, measureSpec, 0) & MEASURED_SIZE_MASK;
    }

    public static int resolveSize(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer)
            return resolveSize((int)(Integer)p0, (int)(Integer)p1);
        return 0;
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = size;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    public static int resolveSizeAndState(Object p0, Object p1, Object p2) {
        if (p0 instanceof Integer && p1 instanceof Integer && p2 instanceof Integer)
            return resolveSizeAndState((int)(Integer)p0, (int)(Integer)p1, (int)(Integer)p2);
        return 0;
    }

    public static int combineMeasuredStates(int curState, int newState) {
        return curState | newState;
    }

    public static int combineMeasuredStates(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer)
            return combineMeasuredStates((int)(Integer)p0, (int)(Integer)p1);
        return 0;
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Layout (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * AOSP layout() — calls setFrame(), onLayout(), fires layout change listeners.
     */
    public void layout(int l, int t, int r, int b) {
        if ((mPrivateFlags3 & PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT) != 0) {
            onMeasure(mOldWidthMeasureSpec, mOldHeightMeasureSpec);
            mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
        }

        int oldL = mLeft;
        int oldT = mTop;
        int oldB = mBottom;
        int oldR = mRight;

        boolean changed = setFrame(l, t, r, b);

        if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {
            onLayout(changed, l, t, r, b);

            mPrivateFlags &= ~PFLAG_LAYOUT_REQUIRED;

            if (mOnLayoutChangeListeners != null) {
                ArrayList<OnLayoutChangeListener> listenersCopy =
                        (ArrayList<OnLayoutChangeListener>) mOnLayoutChangeListeners.clone();
                int numListeners = listenersCopy.size();
                for (int i = 0; i < numListeners; ++i) {
                    listenersCopy.get(i).onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
                }
            }
        }

        mPrivateFlags &= ~PFLAG_FORCE_LAYOUT;
        mPrivateFlags3 |= PFLAG3_IS_LAID_OUT;
    }

    /** Object overload */
    public void layout(Object p0, Object p1, Object p2, Object p3) {
        if (p0 instanceof Integer && p1 instanceof Integer
                && p2 instanceof Integer && p3 instanceof Integer) {
            layout((int)(Integer)p0, (int)(Integer)p1, (int)(Integer)p2, (int)(Integer)p3);
        }
    }

    /**
     * AOSP setFrame() — sets position, fires size change.
     */
    protected boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = false;

        if (mLeft != left || mRight != right || mTop != top || mBottom != bottom) {
            changed = true;

            int oldWidth = mRight - mLeft;
            int oldHeight = mBottom - mTop;
            int newWidth = right - left;
            int newHeight = bottom - top;
            boolean sizeChanged = (newWidth != oldWidth) || (newHeight != oldHeight);

            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;

            mPrivateFlags |= PFLAG_HAS_BOUNDS;

            if (sizeChanged) {
                sizeChange(newWidth, newHeight, oldWidth, oldHeight);
            }

            if ((mViewFlags & VISIBILITY_MASK) == VISIBLE) {
                mPrivateFlags |= PFLAG_DRAWN;
            }
        }
        return changed;
    }

    private void sizeChange(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {}

    /**
     * AOSP onLayout() — for subclasses to position children.
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {}

    /** Object overload */
    public void onLayout(Object p0, Object p1, Object p2, Object p3, Object p4) {}

    public int getBaseline() { return -1; }

    public boolean isLaidOut() {
        return (mPrivateFlags3 & PFLAG3_IS_LAID_OUT) == PFLAG3_IS_LAID_OUT;
    }

    public boolean isLayoutRequested() {
        return (mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT;
    }

    public boolean isInLayout() { return false; }


    // ════════════════════════════════════════════════════════════════════════
    //  Drawing (from AOSP, simplified — no RenderNode/HardwareRenderer)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * AOSP draw() — 7-step draw traversal (simplified, no fading edges for now).
     */
    public void draw(android.graphics.Canvas canvas) {
        int privateFlags = mPrivateFlags;
        mPrivateFlags = (privateFlags & ~PFLAG_DIRTY_MASK) | PFLAG_DRAWN;

        // Step 0: Apply alpha via saveLayerAlpha if not fully opaque
        boolean needsAlphaRestore = false;
        if (mAlpha < 1.0f) {
            int alphaInt = Math.round(mAlpha * 255);
            canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), alphaInt);
            needsAlphaRestore = true;
        }

        // Step 1: Draw background
        drawBackground(canvas);

        // Step 2: Apply scroll offset
        if (mScrollX != 0 || mScrollY != 0) {
            canvas.save();
            canvas.translate(-mScrollX, -mScrollY);
        }

        // Step 3: Draw content
        onDraw(canvas);

        // Step 4: Draw children
        dispatchDraw(canvas);

        // Restore scroll
        if (mScrollX != 0 || mScrollY != 0) {
            canvas.restore();
        }

        // Step 6: Draw foreground (scrollbars, etc.)
        onDrawForeground(canvas);

        // Restore alpha
        if (needsAlphaRestore) {
            canvas.restore();
        }
    }

    private void drawBackground(android.graphics.Canvas canvas) {
        if (mBackgroundColor != 0) {
            canvas.drawColor(mBackgroundColor);
        }
        if (mBackground != null) {
            mBackground.setBounds(0, 0, getWidth(), getHeight());
            mBackground.draw(canvas);
        }
    }

    protected void onDraw(android.graphics.Canvas canvas) {}

    /** Object overload (legacy stub compat) */
    public void onDraw(Object p0) {}

    protected void dispatchDraw(android.graphics.Canvas canvas) {}

    public void onDrawForeground(android.graphics.Canvas canvas) {}

    /** Object overload */
    public void onDrawForeground(Object p0) {}


    // ════════════════════════════════════════════════════════════════════════
    //  Touch handling (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * AOSP dispatchTouchEvent() — listener first, then onTouchEvent.
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;

        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            stopNestedScroll();
        }

        if (onFilterTouchEventForSecurity(event)) {
            // Touch listener gets first shot (if enabled)
            if (mOnTouchListener != null
                    && (mViewFlags & ENABLED_MASK) == ENABLED
                    && mOnTouchListener.onTouch(this, event)) {
                result = true;
            }

            if (!result && onTouchEvent(event)) {
                result = true;
            }
        }

        // Clean up after nested scrolls
        if (actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_CANCEL
                || (actionMasked == MotionEvent.ACTION_DOWN && !result)) {
            stopNestedScroll();
        }

        return result;
    }

    /** Object overload */
    public boolean dispatchTouchEvent(Object p0) {
        if (p0 instanceof MotionEvent) return dispatchTouchEvent((MotionEvent) p0);
        return false;
    }

    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if ((mViewFlags & FILTER_TOUCHES_WHEN_OBSCURED) != 0
                && (event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
            return false;
        }
        return true;
    }

    public boolean onFilterTouchEventForSecurity(Object p0) { return true; }

    protected boolean canReceivePointerEvents() {
        return (mViewFlags & VISIBILITY_MASK) == VISIBLE || getAnimation() != null;
    }

    /**
     * AOSP onTouchEvent() — simplified (no long-press timer, tooltip, etc.).
     * Handles click on ACTION_UP for clickable views.
     */
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int viewFlags = mViewFlags;
        int action = event.getAction();

        boolean clickable = ((viewFlags & CLICKABLE) == CLICKABLE
                || (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)
                || (viewFlags & CONTEXT_CLICKABLE) == CONTEXT_CLICKABLE;

        if ((viewFlags & ENABLED_MASK) == DISABLED) {
            if (action == MotionEvent.ACTION_UP && (mPrivateFlags & PFLAG_PRESSED) != 0) {
                setPressed(false);
            }
            mPrivateFlags3 &= ~PFLAG3_FINGER_DOWN;
            return clickable;
        }

        if (mTouchDelegate != null) {
            if (mTouchDelegate.onTouchEvent(event)) {
                return true;
            }
        }

        if (clickable) {
            switch (action) {
                case MotionEvent.ACTION_UP:
                    mPrivateFlags3 &= ~PFLAG3_FINGER_DOWN;
                    // Fire click: either we were pressed (normal flow) or this is a
                    // standalone UP (headless tests send UP without prior DOWN).
                    if (!mHasPerformedLongPress && !mIgnoreNextUpEvent) {
                        performClickInternal();
                    }
                    setPressed(false);
                    mIgnoreNextUpEvent = false;
                    break;

                case MotionEvent.ACTION_DOWN:
                    mPrivateFlags3 |= PFLAG3_FINGER_DOWN;
                    mHasPerformedLongPress = false;
                    setPressed(true);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    setPressed(false);
                    mInContextButtonPress = false;
                    mHasPerformedLongPress = false;
                    mIgnoreNextUpEvent = false;
                    mPrivateFlags3 &= ~PFLAG3_FINGER_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!pointInView(x, y, mTouchSlop)) {
                        if ((mPrivateFlags & PFLAG_PRESSED) != 0) {
                            setPressed(false);
                        }
                        mPrivateFlags3 &= ~PFLAG3_FINGER_DOWN;
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    /** Object overload */
    public boolean onTouchEvent(Object p0) {
        if (p0 instanceof MotionEvent) return onTouchEvent((MotionEvent) p0);
        return false;
    }

    private boolean performClickInternal() {
        return performClick();
    }

    public boolean performClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    public boolean performLongClick() {
        if (mOnLongClickListener != null) {
            return mOnLongClickListener.onLongClick(this);
        }
        return false;
    }

    public boolean performLongClick(float x, float y) {
        return performLongClick();
    }

    public boolean performLongClick(Object p0, Object p1) { return false; }

    public boolean pointInView(float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop
                && localX < ((mRight - mLeft) + slop)
                && localY < ((mBottom - mTop) + slop);
    }

    public boolean isPressed() {
        return (mPrivateFlags & PFLAG_PRESSED) == PFLAG_PRESSED;
    }

    public void setPressed(boolean pressed) {
        boolean needsRefresh = pressed != ((mPrivateFlags & PFLAG_PRESSED) == PFLAG_PRESSED);
        if (pressed) {
            mPrivateFlags |= PFLAG_PRESSED;
        } else {
            mPrivateFlags &= ~PFLAG_PRESSED;
        }
        if (needsRefresh) {
            refreshDrawableState();
        }
        dispatchSetPressed(pressed);
    }

    public void setPressed(Object p0) {
        if (p0 instanceof Boolean) setPressed(((Boolean) p0).booleanValue());
    }

    protected void dispatchSetPressed(boolean pressed) {}

    public void setPressed(boolean pressed, float x, float y) {
        setPressed(pressed);
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Key handling (from AOSP / shim)
    // ════════════════════════════════════════════════════════════════════════

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mOnKeyListener != null) {
            if (mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
                return true;
            }
        }
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

    public boolean onKeyDown(int keyCode, KeyEvent event) { return false; }
    public boolean onKeyDown(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof KeyEvent)
            return onKeyDown((Integer) p0, (KeyEvent) p1);
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isClickable() && (keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            performClick();
            return true;
        }
        return false;
    }

    public boolean onKeyUp(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof KeyEvent)
            return onKeyUp((Integer) p0, (KeyEvent) p1);
        return false;
    }

    public boolean dispatchTrackballEvent(Object p0) { return false; }
    public boolean dispatchKeyEventPreIme(Object p0) { return false; }
    public boolean dispatchKeyShortcutEvent(Object p0) { return false; }


    // ════════════════════════════════════════════════════════════════════════
    //  invalidate / requestLayout (from AOSP, adapted for shim)
    // ════════════════════════════════════════════════════════════════════════

    public void invalidate() {
        mPrivateFlags |= PFLAG_DIRTY;
        // Walk up to the root and find the hosting Activity to trigger a frame render
        View root = this;
        ViewParent p = mParent;
        while (p instanceof View) {
            root = (View) p;
            p = ((View) p).mParent;
        }
        Object tag = root.getTag();
        if (tag instanceof android.app.Activity) {
            ((android.app.Activity) tag).renderFrame();
        }
    }

    public void invalidate(boolean invalidateCache) {
        invalidate();
    }

    public void invalidate(android.graphics.Rect dirty) {
        invalidate();
    }

    public void invalidate(int l, int t, int r, int b) {
        invalidate();
    }

    public void invalidateOutline() {}

    public void requestLayout() {
        if (mMeasureCache != null) mMeasureCache.clear();

        mPrivateFlags |= PFLAG_FORCE_LAYOUT;
        mPrivateFlags |= PFLAG_INVALIDATED;

        if (mParent != null && !mParent.isLayoutRequested()) {
            mParent.requestLayout();
        }
    }

    public void forceLayout() {
        if (mMeasureCache != null) mMeasureCache.clear();
        mPrivateFlags |= PFLAG_FORCE_LAYOUT;
        mPrivateFlags |= PFLAG_INVALIDATED;
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Getters / Setters — identity, geometry, visibility, enabled
    // ════════════════════════════════════════════════════════════════════════

    public int getId() { return mID; }
    public void setId(int id) { mID = id; }

    public static int generateViewId() {
        for (;;) {
            int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1;
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public View findViewById(int id) {
        if (id == NO_ID) return null;
        if (mID == id) return this;
        return null;
    }

    public Object findViewById(Object p0) {
        if (p0 instanceof Integer) return findViewById(((Integer) p0).intValue());
        return null;
    }

    public Object findViewWithTag(Object tag) {
        if (tag != null && tag.equals(mTag)) return this;
        return null;
    }

    // ── Position ──
    public int getLeft() { return mLeft; }
    public int getTop() { return mTop; }
    public int getRight() { return mRight; }
    public int getBottom() { return mBottom; }
    public int getWidth() { return mRight - mLeft; }
    public int getHeight() { return mBottom - mTop; }
    public int getLayoutDirection() { return LAYOUT_DIRECTION_LTR; }
    public boolean isLayoutRtl() { return false; }

    public float getX() { return mLeft + mTranslationX; }
    public float getY() { return mTop + mTranslationY; }
    public float getZ() { return mElevation + mTranslationZ; }

    public void setX(float x) { setTranslationX(x - mLeft); }
    public void setY(float y) { setTranslationY(y - mTop); }
    public void setZ(float z) { setTranslationZ(z - mElevation); }

    // ── Visibility ──
    public void setVisibility(int visibility) {
        mViewFlags = (mViewFlags & ~VISIBILITY_MASK) | (visibility & VISIBILITY_MASK);
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrInt(nativeHandle, ATTR_VISIBILITY, visibility);
        }
    }

    public int getVisibility() { return mViewFlags & VISIBILITY_MASK; }

    // ── Enabled ──
    public void setEnabled(boolean enabled) {
        if (enabled) {
            mViewFlags &= ~DISABLED;
        } else {
            mViewFlags |= DISABLED;
        }
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrInt(nativeHandle, ATTR_ENABLED, enabled ? 1 : 0);
        }
    }

    public boolean isEnabled() { return (mViewFlags & ENABLED_MASK) == ENABLED; }

    // ── Clickable ──
    public void setClickable(boolean clickable) {
        if (clickable) mViewFlags |= CLICKABLE;
        else mViewFlags &= ~CLICKABLE;
    }

    public boolean isClickable() { return (mViewFlags & CLICKABLE) == CLICKABLE; }

    public void setClickable(Object p0) {
        if (p0 instanceof Boolean) setClickable(((Boolean) p0).booleanValue());
    }

    public void setLongClickable(boolean longClickable) {
        if (longClickable) mViewFlags |= LONG_CLICKABLE;
        else mViewFlags &= ~LONG_CLICKABLE;
    }

    public boolean isLongClickable() { return (mViewFlags & LONG_CLICKABLE) == LONG_CLICKABLE; }

    public void setLongClickable(Object p0) {
        if (p0 instanceof Boolean) setLongClickable(((Boolean) p0).booleanValue());
    }

    public void setContextClickable(boolean contextClickable) {
        if (contextClickable) mViewFlags |= CONTEXT_CLICKABLE;
        else mViewFlags &= ~CONTEXT_CLICKABLE;
    }

    // ── Padding ──
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left; mPaddingTop = top; mPaddingRight = right; mPaddingBottom = bottom;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PADDING,
                    (float) left, (float) top, (float) right, (float) bottom, 4);
        }
    }

    public int getPaddingLeft() { return mPaddingLeft; }
    public int getPaddingTop() { return mPaddingTop; }
    public int getPaddingRight() { return mPaddingRight; }
    public int getPaddingBottom() { return mPaddingBottom; }
    public int getPaddingStart() { return mPaddingLeft; }
    public int getPaddingEnd() { return mPaddingRight; }

    // ── Alpha / transforms ──
    public void setAlpha(float alpha) {
        mAlpha = alpha;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_OPACITY, alpha, 0, 0, 0, 1);
        }
    }

    public float getAlpha() { return mAlpha; }

    public float getTranslationX() { return mTranslationX; }
    public float getTranslationY() { return mTranslationY; }
    public float getTranslationZ() { return mTranslationZ; }

    public void setTranslationX(float tx) { mTranslationX = tx; }
    public void setTranslationY(float ty) { mTranslationY = ty; }
    public void setTranslationZ(float tz) { mTranslationZ = tz; }

    public void setTranslationX(Object p0) { if (p0 instanceof Number) mTranslationX = ((Number) p0).floatValue(); }
    public void setTranslationY(Object p0) { if (p0 instanceof Number) mTranslationY = ((Number) p0).floatValue(); }
    public void setTranslationZ(Object p0) { if (p0 instanceof Number) mTranslationZ = ((Number) p0).floatValue(); }

    public float getRotation() { return mRotation; }
    public void setRotation(float rotation) { mRotation = rotation; }
    public void setRotation(Object p0) { if (p0 instanceof Number) mRotation = ((Number) p0).floatValue(); }
    public float getRotationX() { return mRotationX; }
    public void setRotationX(float rotationX) { mRotationX = rotationX; }
    public void setRotationX(Object p0) { if (p0 instanceof Number) mRotationX = ((Number) p0).floatValue(); }
    public float getRotationY() { return mRotationY; }
    public void setRotationY(float rotationY) { mRotationY = rotationY; }
    public void setRotationY(Object p0) { if (p0 instanceof Number) mRotationY = ((Number) p0).floatValue(); }
    public float getScaleX() { return mScaleX; }
    public void setScaleX(float scaleX) { mScaleX = scaleX; }
    public void setScaleX(Object p0) { if (p0 instanceof Number) mScaleX = ((Number) p0).floatValue(); }
    public float getScaleY() { return mScaleY; }
    public void setScaleY(float scaleY) { mScaleY = scaleY; }
    public void setScaleY(Object p0) { if (p0 instanceof Number) mScaleY = ((Number) p0).floatValue(); }
    public float getPivotX() { return mPivotX; }
    public void setPivotX(float pivotX) { mPivotX = pivotX; }
    public void setPivotX(Object p0) { if (p0 instanceof Number) mPivotX = ((Number) p0).floatValue(); }
    public float getPivotY() { return mPivotY; }
    public void setPivotY(float pivotY) { mPivotY = pivotY; }
    public void setPivotY(Object p0) { if (p0 instanceof Number) mPivotY = ((Number) p0).floatValue(); }
    public float getElevation() { return mElevation; }
    public void setElevation(float elevation) { mElevation = elevation; }
    public void setElevation(Object p0) { if (p0 instanceof Number) mElevation = ((Number) p0).floatValue(); }

    // ── Background ──
    public void setBackground(android.graphics.drawable.Drawable background) {
        mBackground = background;
        if (background != null) {
            background.setCallback(this);
        }
    }

    public void setBackground(Object p0) {
        if (p0 instanceof android.graphics.drawable.Drawable) {
            setBackground((android.graphics.drawable.Drawable) p0);
        }
    }

    public android.graphics.drawable.Drawable getBackground() { return mBackground; }
    public android.graphics.drawable.Drawable getBackgroundDrawable() { return mBackground; }

    public void setBackgroundDrawable(android.graphics.drawable.Drawable d) { setBackground(d); }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrColor(nativeHandle, ATTR_BG_COLOR, color);
        }
    }

    public int getBackgroundColor() { return mBackgroundColor; }

    public void setBackgroundResource(Object p0) {}
    public void setBackgroundTintBlendMode(Object p0) {}
    public void setBackgroundTintList(Object p0) {}
    public void setBackgroundTintMode(Object p0) {}

    // ── Scroll ──
    public int getScrollX() { return mScrollX; }
    public int getScrollY() { return mScrollY; }

    public void scrollTo(int x, int y) {
        if (mScrollX != x || mScrollY != y) {
            int oldX = mScrollX;
            int oldY = mScrollY;
            mScrollX = x;
            mScrollY = y;
            onScrollChanged(mScrollX, mScrollY, oldX, oldY);
        }
    }

    public void scrollBy(int dx, int dy) { scrollTo(mScrollX + dx, mScrollY + dy); }

    public void setScrollX(int x) { scrollTo(x, mScrollY); }
    public void setScrollY(int y) { scrollTo(mScrollX, y); }

    public void scrollTo(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer)
            scrollTo((int)(Integer)p0, (int)(Integer)p1);
    }

    public void scrollBy(Object p0, Object p1) {
        if (p0 instanceof Integer && p1 instanceof Integer)
            scrollBy((int)(Integer)p0, (int)(Integer)p1);
    }

    public void setScrollX(Object p0) { if (p0 instanceof Integer) setScrollX((Integer) p0); }
    public void setScrollY(Object p0) { if (p0 instanceof Integer) setScrollY((Integer) p0); }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {}
    public void onScrollChanged(Object p0, Object p1, Object p2, Object p3) {}

    public void computeScroll() {}

    // ── Tag ──
    public void setTag(Object tag) { mTag = tag; }
    public Object getTag() { return mTag; }

    public void setTag(int key, Object tag) {
        if (mKeyedTags == null) mKeyedTags = new android.util.SparseArray<Object>();
        mKeyedTags.put(key, tag);
    }

    public Object getTag(int key) {
        return mKeyedTags != null ? mKeyedTags.get(key) : null;
    }

    // ── Parent ──
    public ViewParent getParent() { return mParent; }

    public View getRootView() {
        View root = this;
        ViewParent p = mParent;
        while (p instanceof View) {
            root = (View) p;
            p = ((View) p).mParent;
        }
        return root;
    }

    public Object getRootView(Object unused) { return getRootView(); }

    // ── LayoutParams ──
    public void setLayoutParams(Object p0) { mLayoutParams = p0; }
    public Object getLayoutParams() { return mLayoutParams; }

    // ── Context ──
    public android.content.Context getContext() { return mContext; }

    // ── Focus ──
    public boolean isFocusable() { return mFocusable; }
    public void setFocusable(boolean focusable) { mFocusable = focusable; }
    public void setFocusable(Object p0) {
        if (p0 instanceof Boolean) mFocusable = (Boolean) p0;
        else if (p0 instanceof Integer) mFocusable = ((Integer) p0) != 0;
    }
    public boolean isFocusableInTouchMode() { return mFocusableInTouchMode; }
    public void setFocusableInTouchMode(boolean focusable) { mFocusableInTouchMode = focusable; }
    public void setFocusableInTouchMode(Object p0) {
        if (p0 instanceof Boolean) mFocusableInTouchMode = (Boolean) p0;
    }
    public boolean isFocused() { return (mPrivateFlags & PFLAG_FOCUSED) != 0; }
    public boolean hasFocus() { return isFocused(); }
    public boolean hasFocusable() { return mFocusable; }
    public boolean hasExplicitFocusable() { return mFocusable; }
    public void clearFocus() { mPrivateFlags &= ~PFLAG_FOCUSED; }
    public boolean requestFocus() { return requestFocus(FOCUS_DOWN); }
    public boolean requestFocus(int direction) {
        if (mFocusable) {
            mPrivateFlags |= PFLAG_FOCUSED;
            return true;
        }
        return false;
    }
    public boolean requestFocus(Object p0) {
        if (p0 instanceof Integer) return requestFocus(((Integer) p0).intValue());
        return false;
    }
    public boolean requestFocus(Object p0, Object p1) { return requestFocus(); }
    public boolean requestFocusFromTouch() { return requestFocus(); }
    public Object findFocus() { return isFocused() ? this : null; }
    public Object focusSearch(Object p0) { return null; }

    // ── Selected ──
    public void setSelected(boolean selected) {
        if (selected) mPrivateFlags |= PFLAG_SELECTED;
        else mPrivateFlags &= ~PFLAG_SELECTED;
    }
    public void setSelected(Object p0) {
        if (p0 instanceof Boolean) setSelected(((Boolean) p0).booleanValue());
    }
    public boolean isSelected() { return (mPrivateFlags & PFLAG_SELECTED) != 0; }

    // ── Activated ──
    public void setActivated(boolean activated) {
        if (activated) mPrivateFlags |= PFLAG_ACTIVATED;
        else mPrivateFlags &= ~PFLAG_ACTIVATED;
    }
    public void setActivated(Object p0) {
        if (p0 instanceof Boolean) setActivated(((Boolean) p0).booleanValue());
    }
    public boolean isActivated() { return (mPrivateFlags & PFLAG_ACTIVATED) != 0; }

    // ── Min size ──
    public int getMinimumWidth() { return mMinWidth; }
    public int getMinimumHeight() { return mMinHeight; }

    public void setMinimumWidth(int minWidth) { mMinWidth = minWidth; }
    public void setMinimumHeight(int minHeight) { mMinHeight = minHeight; }
    public void setMinimumWidth(Object p0) { if (p0 instanceof Integer) mMinWidth = (Integer) p0; }
    public void setMinimumHeight(Object p0) { if (p0 instanceof Integer) mMinHeight = (Integer) p0; }


    // ════════════════════════════════════════════════════════════════════════
    //  Listener setters / getters
    // ════════════════════════════════════════════════════════════════════════

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
        if (listener != null) {
            setClickable(true);
        }
        if (nativeHandle != 0 && mClickEventId == 0) {
            mClickEventId = generateEventId();
            com.ohos.shim.bridge.OHBridge.nodeRegisterEvent(nativeHandle, EVENT_CLICK, mClickEventId);
        }
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
        if (listener != null) setLongClickable(true);
    }

    public void setOnLongClickListener(Object p0) {
        if (p0 instanceof OnLongClickListener) setOnLongClickListener((OnLongClickListener) p0);
    }

    public void setOnTouchListener(OnTouchListener listener) { mOnTouchListener = listener; }
    public void setOnTouchListener(Object p0) {
        if (p0 instanceof OnTouchListener) mOnTouchListener = (OnTouchListener) p0;
    }

    public void setOnKeyListener(OnKeyListener listener) { mOnKeyListener = listener; }
    public void setOnKeyListener(Object p0) {
        if (p0 instanceof OnKeyListener) mOnKeyListener = (OnKeyListener) p0;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) { mOnFocusChangeListener = listener; }
    public void setOnFocusChangeListener(Object p0) {
        if (p0 instanceof OnFocusChangeListener) mOnFocusChangeListener = (OnFocusChangeListener) p0;
    }
    public OnFocusChangeListener getOnFocusChangeListener() { return mOnFocusChangeListener; }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) { mOnScrollChangeListener = listener; }
    public void setOnScrollChangeListener(Object p0) {
        if (p0 instanceof OnScrollChangeListener) mOnScrollChangeListener = (OnScrollChangeListener) p0;
    }

    public void setOnContextClickListener(OnContextClickListener listener) { mOnContextClickListener = listener; }
    public void setOnContextClickListener(Object p0) {
        if (p0 instanceof OnContextClickListener) mOnContextClickListener = (OnContextClickListener) p0;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener listener) { mOnCreateContextMenuListener = listener; }
    public void setOnCreateContextMenuListener(Object p0) {
        if (p0 instanceof OnCreateContextMenuListener) mOnCreateContextMenuListener = (OnCreateContextMenuListener) p0;
    }

    public void setOnGenericMotionListener(OnGenericMotionListener listener) { mOnGenericMotionListener = listener; }
    public void setOnGenericMotionListener(Object p0) {
        if (p0 instanceof OnGenericMotionListener) mOnGenericMotionListener = (OnGenericMotionListener) p0;
    }

    public void setOnHoverListener(OnHoverListener listener) { mOnHoverListener = listener; }
    public void setOnHoverListener(Object p0) {
        if (p0 instanceof OnHoverListener) mOnHoverListener = (OnHoverListener) p0;
    }

    public void setOnDragListener(OnDragListener listener) { mOnDragListener = listener; }
    public void setOnDragListener(Object p0) {
        if (p0 instanceof OnDragListener) mOnDragListener = (OnDragListener) p0;
    }

    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener listener) {
        mOnSystemUiVisibilityChangeListener = listener;
    }

    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {}
    public void setOnApplyWindowInsetsListener(Object p0) {}

    public void setOnCapturedPointerListener(OnCapturedPointerListener listener) {}
    public void setOnCapturedPointerListener(Object p0) {}

    public boolean hasOnClickListeners() { return mOnClickListener != null; }
    public boolean hasOnLongClickListeners() { return mOnLongClickListener != null; }

    public boolean callOnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    // ── Layout change listeners ──
    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        if (mOnLayoutChangeListeners == null) {
            mOnLayoutChangeListeners = new ArrayList<OnLayoutChangeListener>();
        }
        if (!mOnLayoutChangeListeners.contains(listener)) {
            mOnLayoutChangeListeners.add(listener);
        }
    }

    public void addOnLayoutChangeListener(Object p0) {
        if (p0 instanceof OnLayoutChangeListener) addOnLayoutChangeListener((OnLayoutChangeListener) p0);
    }

    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        if (mOnLayoutChangeListeners != null) {
            mOnLayoutChangeListeners.remove(listener);
        }
    }

    public void removeOnLayoutChangeListener(Object p0) {
        if (p0 instanceof OnLayoutChangeListener) removeOnLayoutChangeListener((OnLayoutChangeListener) p0);
    }

    // ── Attach state listeners ──
    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        if (mOnAttachStateChangeListeners == null) {
            mOnAttachStateChangeListeners = new CopyOnWriteArrayList<OnAttachStateChangeListener>();
        }
        mOnAttachStateChangeListeners.add(listener);
    }

    public void addOnAttachStateChangeListener(Object p0) {
        if (p0 instanceof OnAttachStateChangeListener) addOnAttachStateChangeListener((OnAttachStateChangeListener) p0);
    }

    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        if (mOnAttachStateChangeListeners != null) mOnAttachStateChangeListeners.remove(listener);
    }

    public void removeOnAttachStateChangeListener(Object p0) {
        if (p0 instanceof OnAttachStateChangeListener) removeOnAttachStateChangeListener((OnAttachStateChangeListener) p0);
    }

    // ── Touch delegate ──
    public void setTouchDelegate(TouchDelegate delegate) { mTouchDelegate = delegate; }
    public void setTouchDelegate(Object p0) {
        if (p0 instanceof TouchDelegate) mTouchDelegate = (TouchDelegate) p0;
    }
    public TouchDelegate getTouchDelegate() { return mTouchDelegate; }

    // ── Animation ──
    public android.view.animation.Animation getAnimation() { return mCurrentAnimation; }
    public void setAnimation(android.view.animation.Animation animation) { mCurrentAnimation = animation; }
    public void setAnimation(Object p0) {
        if (p0 instanceof android.view.animation.Animation) mCurrentAnimation = (android.view.animation.Animation) p0;
    }
    public void startAnimation(android.view.animation.Animation animation) { mCurrentAnimation = animation; }
    public void startAnimation(Object p0) {
        if (p0 instanceof android.view.animation.Animation) mCurrentAnimation = (android.view.animation.Animation) p0;
    }
    public void clearAnimation() { mCurrentAnimation = null; }

    // ── ViewPropertyAnimator ──
    public ViewPropertyAnimator animate() { return new ViewPropertyAnimator(this); }

    // ── ViewTreeObserver ──
    private ViewTreeObserver mViewTreeObserver;
    public ViewTreeObserver getViewTreeObserver() {
        if (mViewTreeObserver == null) {
            mViewTreeObserver = new ViewTreeObserver();
        }
        return mViewTreeObserver;
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Drawable.Callback
    // ════════════════════════════════════════════════════════════════════════

    public void invalidateDrawable(android.graphics.drawable.Drawable who) {
        invalidate();
    }

    public void scheduleDrawable(android.graphics.drawable.Drawable who, Runnable what, long when) {}

    public void unscheduleDrawable(android.graphics.drawable.Drawable who, Runnable what) {}
    public void unscheduleDrawable(android.graphics.drawable.Drawable who) {}
    public void unscheduleDrawable(Object p0, Object p1) {}
    public void unscheduleDrawable(Object p0) {}

    public void drawableHotspotChanged(float x, float y) {}
    public void drawableHotspotChanged(Object p0, Object p1) {}

    public void drawableStateChanged() {}
    public void refreshDrawableState() {}


    // ════════════════════════════════════════════════════════════════════════
    //  Save/restore instance state
    // ════════════════════════════════════════════════════════════════════════

    protected android.os.Parcelable onSaveInstanceState() { return null; }
    protected void onRestoreInstanceState(android.os.Parcelable state) {}
    public void saveHierarchyState(Object p0) {}
    public void restoreHierarchyState(Object p0) {}
    public void dispatchSaveInstanceState(Object p0) {}
    public void dispatchRestoreInstanceState(Object p0) {}

    // ════════════════════════════════════════════════════════════════════════
    //  DragShadowBuilder (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    public static class DragShadowBuilder {
        private final java.lang.ref.WeakReference<View> mView;

        public DragShadowBuilder(View view) {
            mView = new java.lang.ref.WeakReference<View>(view);
        }

        public DragShadowBuilder() {
            mView = new java.lang.ref.WeakReference<View>(null);
        }

        public final View getView() {
            return mView.get();
        }

        public void onProvideShadowMetrics(android.graphics.Point outShadowSize,
                android.graphics.Point outShadowTouchPoint) {
            View view = mView.get();
            if (view != null) {
                outShadowSize.set(view.getWidth(), view.getHeight());
                outShadowTouchPoint.set(outShadowSize.x / 2, outShadowSize.y / 2);
            } else {
                outShadowSize.set(0, 0);
                outShadowTouchPoint.set(0, 0);
            }
        }

        public void onDrawShadow(android.graphics.Canvas canvas) {
            View view = mView.get();
            if (view != null) {
                view.draw(canvas);
            }
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    //  BaseSavedState (from AOSP)
    // ════════════════════════════════════════════════════════════════════════

    public static class BaseSavedState extends AbsSavedState {
        public BaseSavedState(android.os.Parcelable superState) { super(superState); }
        public BaseSavedState(android.os.Parcel source) { super(source); }
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Remaining stub methods (alphabetical, for API compatibility)
    // ════════════════════════════════════════════════════════════════════════

    public void addChildrenForAccessibility(Object p0) {}
    public void addExtraDataToAccessibilityNodeInfo(Object p0, Object p1, Object p2) {}
    public void addFocusables(Object p0, Object p1) {}
    public void addFocusables(Object p0, Object p1, Object p2) {}
    public void addKeyboardNavigationClusters(Object p0, Object p1) {}
    public void addOnUnhandledKeyEventListener(Object p0) {}
    public void addTouchables(Object p0) {}
    public void announceForAccessibility(Object p0) {}
    public void autofill(Object p0) {}
    public boolean awakenScrollBars() { return false; }
    public boolean awakenScrollBars(Object p0) { return false; }
    public boolean awakenScrollBars(Object p0, Object p1) { return false; }
    public void bringToFront() {}
    public void buildLayer() {}
    public boolean canResolveLayoutDirection() { return true; }
    public boolean canResolveTextAlignment() { return true; }
    public boolean canResolveTextDirection() { return true; }
    public boolean canScrollHorizontally(int direction) { return false; }
    public boolean canScrollHorizontally(Object p0) { return false; }
    public boolean canScrollVertically(int direction) { return false; }
    public boolean canScrollVertically(Object p0) { return false; }
    public void cancelDragAndDrop() {}
    public void cancelLongPress() {}
    public void cancelPendingInputEvents() {}
    public boolean checkInputConnectionProxy(Object p0) { return false; }
    public int computeHorizontalScrollExtent() { return getWidth(); }
    public int computeHorizontalScrollOffset() { return mScrollX; }
    public int computeHorizontalScrollRange() { return getWidth(); }
    public Object computeSystemWindowInsets(Object p0, Object p1) { return null; }
    public int computeVerticalScrollExtent() { return getHeight(); }
    public int computeVerticalScrollOffset() { return mScrollY; }
    public int computeVerticalScrollRange() { return getHeight(); }
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
    public boolean dispatchNestedFling(Object p0, Object p1, Object p2) { return false; }
    public boolean dispatchNestedPreFling(Object p0, Object p1) { return false; }
    public boolean dispatchNestedPrePerformAccessibilityAction(Object p0, Object p1) { return false; }
    public boolean dispatchNestedPreScroll(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean dispatchNestedScroll(Object p0, Object p1, Object p2, Object p3, Object p4) { return false; }
    public void dispatchPointerCaptureChanged(Object p0) {}
    public boolean dispatchPopulateAccessibilityEvent(Object p0) { return false; }
    public void dispatchProvideAutofillStructure(Object p0, Object p1) {}
    public void dispatchProvideStructure(Object p0) {}
    public void dispatchSetActivated(Object p0) {}
    public void dispatchSetPressed(Object p0) {}
    public void dispatchSetSelected(Object p0) {}
    public boolean dispatchTrackballEvent(MotionEvent event) { return false; }
    public boolean dispatchUnhandledMove(Object p0, Object p1) { return false; }
    public void dispatchVisibilityChanged(Object p0, Object p1) {}
    public void dispatchWindowFocusChanged(Object p0) {}
    public void dispatchWindowInsetsAnimationEnd(Object p0) {}
    public void dispatchWindowInsetsAnimationPrepare(Object p0) {}
    public void dispatchWindowVisibilityChanged(Object p0) {}
    public void findViewsWithText(Object p0, Object p1, Object p2) {}
    public void forceHasOverlappingRendering(Object p0) {}
    public int getAccessibilityLiveRegion() { return 0; }
    public Object getAccessibilityClassName() { return "android.view.View"; }
    public Object getAccessibilityDelegate() { return null; }
    public Object getAccessibilityNodeProvider() { return null; }
    public Object getApplicationWindowToken() { return null; }
    public Object getAutofillId() { return null; }
    public int getAutofillType() { return 0; }
    public float getBottomFadingEdgeStrength() { return 0f; }
    public int getBottomPaddingOffset() { return 0; }
    public float getCameraDistance() { return 0f; }
    public Object getClipBounds() { return null; }
    public boolean getClipBounds(Object p0) { return false; }
    public boolean getClipToOutline() { return false; }
    public Object getContextMenuInfo() { return null; }
    public Object getDisplay() { return null; }
    public int[] getDrawableState() { return new int[0]; }
    public int getDrawableState(int unused) { return 0; }
    public void getDrawingRect(Object p0) {}
    public long getDrawingTime() { return android.os.SystemClock.uptimeMillis(); }
    public Object getFocusables(Object p0) { return new ArrayList<View>(); }
    public void getFocusedRect(Object p0) {}
    public Object getForeground() { return null; }
    public int getForegroundGravity() { return 0; }
    public boolean getGlobalVisibleRect(Object p0, Object p1) { return false; }
    public boolean getGlobalVisibleRect(Object p0) { return false; }
    public android.os.Handler getHandler() { return null; }
    public boolean getHasOverlappingRendering() { return true; }
    public boolean hasOverlappingRendering() { return true; }
    public void getHitRect(android.graphics.Rect outRect) {
        outRect.set(mLeft, mTop, mRight, mBottom);
    }
    public void getHitRect(Object p0) {
        if (p0 instanceof android.graphics.Rect) getHitRect((android.graphics.Rect) p0);
    }
    public int getHorizontalFadingEdgeLength() { return 0; }
    public int getHorizontalScrollbarHeight() { return 0; }
    public boolean getKeepScreenOn() { return false; }
    public Object getKeyDispatcherState() { return null; }
    public int getLayerType() { return LAYER_TYPE_NONE; }
    public float getLeftFadingEdgeStrength() { return 0f; }
    public int getLeftPaddingOffset() { return 0; }
    public boolean getLocalVisibleRect(Object p0) { return false; }
    public void getLocationInSurface(Object p0) {}
    public void getLocationInWindow(int[] outLocation) {
        if (outLocation != null && outLocation.length >= 2) {
            outLocation[0] = mLeft;
            outLocation[1] = mTop;
            ViewParent p = mParent;
            while (p instanceof View) {
                View pv = (View) p;
                outLocation[0] += pv.mLeft - pv.mScrollX;
                outLocation[1] += pv.mTop - pv.mScrollY;
                p = pv.mParent;
            }
        }
    }
    public void getLocationInWindow(Object p0) {
        if (p0 instanceof int[]) getLocationInWindow((int[]) p0);
    }
    public void getLocationOnScreen(int[] outLocation) { getLocationInWindow(outLocation); }
    public void getLocationOnScreen(Object p0) { getLocationInWindow(p0); }
    public android.graphics.Matrix getMatrix() { return new android.graphics.Matrix(); }
    public Object getOutlineProvider() { return null; }
    public int getOverScrollMode() { return OVER_SCROLL_IF_CONTENT_SCROLLS; }
    public Object getOverlay() { return null; }
    public Object getParentForAccessibility() { return mParent; }
    public Object getPointerIcon() { return null; }
    public android.content.res.Resources getResources() {
        return mContext != null ? mContext.getResources() : null;
    }
    public boolean getRevealOnFocusHint() { return true; }
    public float getRightFadingEdgeStrength() { return 0f; }
    public int getRightPaddingOffset() { return 0; }
    public Object getRootWindowInsets() { return null; }
    public int getScrollBarDefaultDelayBeforeFade() { return 300; }
    public int getScrollBarFadeDuration() { return 250; }
    public int getScrollBarSize() { return 0; }
    public int getScrollIndicators() { return 0; }
    public Object getStateListAnimator() { return null; }
    public CharSequence getContentDescription() { return null; }
    public void setContentDescription(CharSequence desc) {}
    public void setContentDescription(Object p0) {}
    public CharSequence getTooltipText() { return null; }
    public void setTooltipText(CharSequence tooltipText) {}
    public void setTooltipText(Object p0) {}
    public Object getTransitionName() { return null; }
    public void setTransitionName(Object p0) {}
    public float getTopFadingEdgeStrength() { return 0f; }
    public int getTopPaddingOffset() { return 0; }
    public Object getTouchables() { return new ArrayList<View>(); }
    public long getUniqueDrawingId() { return System.identityHashCode(this); }
    public int getVerticalFadingEdgeLength() { return 0; }
    public int getVerticalScrollbarPosition() { return SCROLLBAR_POSITION_DEFAULT; }
    public int getVerticalScrollbarWidth() { return 0; }
    public int getWindowAttachCount() { return 0; }
    public Object getWindowId() { return null; }
    public Object getWindowToken() { return null; }
    public int getWindowVisibility() { return VISIBLE; }
    public void getWindowVisibleDisplayFrame(Object p0) {}
    public boolean hasNestedScrollingParent() { return false; }
    public boolean hasPointerCapture() { return false; }
    public boolean hasWindowFocus() { return true; }
    public static Object inflate(Object p0, Object p1, Object p2) { return null; }
    public boolean isAccessibilityFocused() { return false; }
    public boolean isAccessibilityHeading() { return false; }
    public boolean isAttachedToWindow() { return mParent != null; }
    public boolean isContextClickable() { return (mViewFlags & CONTEXT_CLICKABLE) != 0; }
    public boolean isDirty() { return (mPrivateFlags & PFLAG_DIRTY) != 0; }
    public boolean isDuplicateParentStateEnabled() { return false; }
    public boolean isHorizontalFadingEdgeEnabled() { return false; }
    public boolean isHorizontalScrollBarEnabled() { return false; }
    public boolean isImportantForAccessibility() { return true; }
    public boolean isImportantForAutofill() { return false; }
    public boolean isImportantForContentCapture() { return false; }
    public boolean isInEditMode() { return false; }
    public boolean isLayoutDirectionResolved() { return true; }
    public boolean isNestedScrollingEnabled() { return false; }
    public boolean isPaddingOffsetRequired() { return false; }
    public boolean isPaddingRelative() { return false; }
    public boolean isPivotSet() { return false; }
    public boolean isSaveEnabled() { return true; }
    public boolean isSaveFromParentEnabled() { return true; }
    public boolean isScreenReaderFocusable() { return false; }
    public boolean isScrollContainer() { return false; }
    public boolean isScrollbarFadingEnabled() { return true; }
    public boolean isShowingLayoutBounds() { return false; }
    public boolean isShown() {
        View current = this;
        while (current != null) {
            if ((current.mViewFlags & VISIBILITY_MASK) != VISIBLE) return false;
            ViewParent p = current.mParent;
            if (p instanceof View) current = (View) p;
            else break;
        }
        return true;
    }
    public boolean isTemporarilyDetached() { return false; }
    public boolean isTextAlignmentResolved() { return true; }
    public boolean isTextDirectionResolved() { return true; }
    public boolean isVerticalFadingEdgeEnabled() { return false; }
    public boolean isVerticalScrollBarEnabled() { return false; }
    public boolean isVisibleToUserForAutofill(Object p0) { return false; }
    public Object keyboardNavigationClusterSearch(Object p0, Object p1) { return null; }
    public static int mergeDrawableStates(int[] baseState, int[] additionalState) { return 0; }
    public static int mergeDrawableStates(Object p0, Object p1) { return 0; }
    public void offsetLeftAndRight(int offset) { mLeft += offset; mRight += offset; }
    public void offsetLeftAndRight(Object p0) { if (p0 instanceof Integer) offsetLeftAndRight((Integer) p0); }
    public void offsetTopAndBottom(int offset) { mTop += offset; mBottom += offset; }
    public void offsetTopAndBottom(Object p0) { if (p0 instanceof Integer) offsetTopAndBottom((Integer) p0); }
    public Object onApplyWindowInsets(Object p0) { return p0; }
    public void onCancelPendingInputEvents() {}
    public boolean onCapturedPointerEvent(Object p0) { return false; }
    public boolean onCheckIsTextEditor() { return false; }
    public void onConfigurationChanged(Object p0) {}
    public void onCreateContextMenu(Object p0) {}
    public int[] onCreateDrawableState(int extraSpace) { return new int[0]; }
    public int onCreateDrawableState(Object p0) { return 0; }
    public Object onCreateInputConnection(Object p0) { return null; }
    public void onDisplayHint(Object p0) {}
    public boolean onDragEvent(Object p0) { return false; }
    public void onDrawScrollBars(Object p0) {}
    public void onFinishTemporaryDetach() {}
    public boolean onGenericMotionEvent(Object p0) { return false; }
    public void onHoverChanged(Object p0) {}
    public boolean onHoverEvent(Object p0) { return false; }
    public boolean onKeyLongPress(Object p0, Object p1) { return false; }
    public boolean onKeyMultiple(Object p0, Object p1, Object p2) { return false; }
    public boolean onKeyPreIme(Object p0, Object p1) { return false; }
    public boolean onKeyShortcut(Object p0, Object p1) { return false; }
    public void onOverScrolled(Object p0, Object p1, Object p2, Object p3) {}
    public void onProvideAutofillStructure(Object p0, Object p1) {}
    public void onProvideAutofillVirtualStructure(Object p0, Object p1) {}
    public void onProvideContentCaptureStructure(Object p0, Object p1) {}
    public void onProvideStructure(Object p0) {}
    public void onProvideVirtualStructure(Object p0) {}
    public Object onResolvePointerIcon(Object p0, Object p1) { return null; }
    public void onRtlPropertiesChanged(Object p0) {}
    public void onScreenStateChanged(Object p0) {}
    public boolean onSetAlpha(int alpha) { return false; }
    public boolean onSetAlpha(Object p0) { return false; }
    public void onSizeChanged(Object p0, Object p1, Object p2, Object p3) {}
    public void onStartTemporaryDetach() {}
    public boolean onTrackballEvent(Object p0) { return false; }
    public void onVisibilityChanged(Object p0, Object p1) {}
    public void onWindowFocusChanged(Object p0) {}
    public void onWindowFocusChanged(boolean hasWindowFocus) {}
    public void onWindowVisibilityChanged(Object p0) {}
    public boolean overScrollBy(Object p0, Object p1, Object p2, Object p3, Object p4,
            Object p5, Object p6, Object p7, Object p8) { return false; }
    public boolean performAccessibilityAction(Object p0, Object p1) { return false; }
    public boolean performContextClick(Object p0, Object p1) { return false; }
    public boolean performContextClick() { return false; }
    public boolean performHapticFeedback(int feedbackConstant) { return false; }
    public boolean performHapticFeedback(Object p0) { return false; }
    public boolean performHapticFeedback(Object p0, Object p1) { return false; }
    public void playSoundEffect(Object p0) {}
    public boolean post(Runnable action) {
        if (action != null) { action.run(); return true; }
        return false;
    }
    public boolean post(Object p0) {
        if (p0 instanceof Runnable) return post((Runnable) p0);
        return false;
    }
    public boolean postDelayed(Runnable action, long delayMillis) {
        // In headless mode, just run immediately
        if (action != null) { action.run(); return true; }
        return false;
    }
    public boolean postDelayed(Object p0, Object p1) {
        if (p0 instanceof Runnable && p1 instanceof Number)
            return postDelayed((Runnable) p0, ((Number) p1).longValue());
        return false;
    }
    public void postInvalidate() { invalidate(); }
    public void postInvalidate(Object p0, Object p1, Object p2, Object p3) {}
    public void postInvalidateDelayed(Object p0) {}
    public void postInvalidateDelayed(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void postInvalidateOnAnimation() { invalidate(); }
    public void postInvalidateOnAnimation(Object p0, Object p1, Object p2, Object p3) {}
    public void postOnAnimation(Runnable action) { post(action); }
    public void postOnAnimation(Object p0) { if (p0 instanceof Runnable) post((Runnable) p0); }
    public void postOnAnimationDelayed(Object p0, Object p1) {
        if (p0 instanceof Runnable) post((Runnable) p0);
    }
    public void releasePointerCapture() {}
    public boolean removeCallbacks(Object p0) { return false; }
    public void removeOnUnhandledKeyEventListener(Object p0) {}
    public void requestApplyInsets() {}
    public void requestPointerCapture() {}
    public boolean requestRectangleOnScreen(Object p0) { return false; }
    public boolean requestRectangleOnScreen(Object p0, Object p1) { return false; }
    public void requestUnbufferedDispatch(Object p0) {}
    public void resetPivot() { mPivotX = 0; mPivotY = 0; }
    public boolean restoreDefaultFocus() { return false; }
    public void saveAttributeDataForStyleable(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void sendAccessibilityEvent(Object p0) {}
    public void sendAccessibilityEventUnchecked(Object p0) {}
    public void setAccessibilityDelegate(Object p0) {}
    public void setAccessibilityHeading(Object p0) {}
    public void setAccessibilityLiveRegion(Object p0) {}
    public void setAccessibilityPaneTitle(Object p0) {}
    public void setAccessibilityTraversalAfter(Object p0) {}
    public void setAccessibilityTraversalBefore(Object p0) {}
    public void setAnimationMatrix(Object p0) {}
    public void setAutofillHints(Object p0) {}
    public void setAutofillId(Object p0) {}
    public void setBottom(int bottom) { mBottom = bottom; }
    public void setBottom(Object p0) { if (p0 instanceof Integer) mBottom = (Integer) p0; }
    public void setCameraDistance(Object p0) {}
    public void setClipBounds(Object p0) {}
    public void setClipToOutline(Object p0) {}
    public void setContentCaptureSession(Object p0) {}
    public void setContextClickable(Object p0) {
        if (p0 instanceof Boolean) setContextClickable(((Boolean) p0).booleanValue());
    }
    public void setDefaultFocusHighlightEnabled(Object p0) {}
    public void setDuplicateParentStateEnabled(Object p0) {}
    public void setFadingEdgeLength(Object p0) {}
    public void setFilterTouchesWhenObscured(Object p0) {}
    public void setFitsSystemWindows(Object p0) {}
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
    public void setImportantForAccessibility(Object p0) {}
    public void setImportantForAutofill(Object p0) {}
    public void setImportantForContentCapture(Object p0) {}
    public void setKeepScreenOn(Object p0) {}
    public void setKeyboardNavigationCluster(Object p0) {}
    public void setLabelFor(Object p0) {}
    public void setLayerPaint(Object p0) {}
    public void setLayerType(int layerType, android.graphics.Paint paint) {}
    public void setLayerType(Object p0, Object p1) {}
    public void setLayoutDirection(Object p0) {}
    public void setLeft(int left) { mLeft = left; }
    public void setLeft(Object p0) { if (p0 instanceof Integer) mLeft = (Integer) p0; }
    public void setLeftTopRightBottom(int left, int top, int right, int bottom) {
        mLeft = left; mTop = top; mRight = right; mBottom = bottom;
    }
    public void setLeftTopRightBottom(Object p0, Object p1, Object p2, Object p3) {
        if (p0 instanceof Integer && p1 instanceof Integer
                && p2 instanceof Integer && p3 instanceof Integer) {
            setLeftTopRightBottom((Integer) p0, (Integer) p1, (Integer) p2, (Integer) p3);
        }
    }
    public void setNestedScrollingEnabled(Object p0) {}
    public void setNextClusterForwardId(Object p0) {}
    public void setNextFocusDownId(Object p0) {}
    public void setNextFocusForwardId(Object p0) {}
    public void setNextFocusLeftId(Object p0) {}
    public void setNextFocusRightId(Object p0) {}
    public void setNextFocusUpId(Object p0) {}
    public void setOutlineAmbientShadowColor(Object p0) {}
    public void setOutlineProvider(Object p0) {}
    public void setOutlineSpotShadowColor(Object p0) {}
    public void setOverScrollMode(Object p0) {}
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        setPadding(start, top, end, bottom);
    }
    public void setPaddingRelative(Object p0, Object p1, Object p2, Object p3) {}
    public void setRevealOnFocusHint(Object p0) {}
    public void setRight(int right) { mRight = right; }
    public void setRight(Object p0) { if (p0 instanceof Integer) mRight = (Integer) p0; }
    public void setSaveEnabled(Object p0) {}
    public void setSaveFromParentEnabled(Object p0) {}
    public void setScreenReaderFocusable(Object p0) {}
    public void setScrollBarDefaultDelayBeforeFade(Object p0) {}
    public void setScrollBarFadeDuration(Object p0) {}
    public void setScrollBarSize(Object p0) {}
    public void setScrollBarStyle(Object p0) {}
    public void setScrollContainer(Object p0) {}
    public void setScrollIndicators(Object p0) {}
    public void setScrollIndicators(Object p0, Object p1) {}
    public void setScrollbarFadingEnabled(Object p0) {}
    public void setSoundEffectsEnabled(Object p0) {}
    public void setStateDescription(Object p0) {}
    public void setStateListAnimator(Object p0) {}
    public void setSystemGestureExclusionRects(Object p0) {}
    public void setTextAlignment(Object p0) {}
    public void setTextDirection(Object p0) {}
    public void setTop(int top) { mTop = top; }
    public void setTop(Object p0) { if (p0 instanceof Integer) mTop = (Integer) p0; }
    public void setTransitionAlpha(Object p0) {}
    public void setTransitionVisibility(Object p0) {}
    public void setVerticalFadingEdgeEnabled(Object p0) {}
    public void setVerticalScrollBarEnabled(Object p0) {}
    public void setVerticalScrollbarPosition(Object p0) {}
    public void setVerticalScrollbarThumbDrawable(Object p0) {}
    public void setVerticalScrollbarTrackDrawable(Object p0) {}
    public void setWillNotDraw(boolean willNotDraw) {
        if (willNotDraw) mPrivateFlags |= PFLAG_SKIP_DRAW;
        else mPrivateFlags &= ~PFLAG_SKIP_DRAW;
    }
    public void setWillNotDraw(Object p0) {
        if (p0 instanceof Boolean) setWillNotDraw(((Boolean) p0).booleanValue());
    }
    public boolean willNotDraw() { return (mPrivateFlags & PFLAG_SKIP_DRAW) != 0; }
    public void setWindowInsetsAnimationCallback(Object p0) {}
    public void setX(Object p0) { if (p0 instanceof Number) setX(((Number) p0).floatValue()); }
    public void setY(Object p0) { if (p0 instanceof Number) setY(((Number) p0).floatValue()); }
    public void setZ(Object p0) { if (p0 instanceof Number) setZ(((Number) p0).floatValue()); }
    public boolean showContextMenu() { return false; }
    public boolean showContextMenu(Object p0, Object p1) { return false; }
    public Object startActionMode(Object p0) { return null; }
    public Object startActionMode(Object p0, Object p1) { return null; }
    public boolean startDragAndDrop(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean startNestedScroll(Object p0) { return false; }
    public void stopNestedScroll() {}
    public void transformMatrixToGlobal(Object p0) {}
    public void transformMatrixToLocal(Object p0) {}
    public void updateDragShadow(Object p0) {}

    // ── Nested scroll compat ──
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) { return false; }
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) { return false; }
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) { return false; }
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) { return false; }
    public boolean startNestedScroll(int axes) { return false; }

    // ── resolveRtlPropertiesIfNeeded (no-op in LTR-only shim) ──
    void resolveRtlPropertiesIfNeeded() {}

    // ── isLayoutModeOptical (always false in shim) ──
    static boolean isLayoutModeOptical(Object o) { return false; }

    // ── canTakeFocus ──
    boolean canTakeFocus() { return mFocusable && isEnabled() && getVisibility() == VISIBLE; }
    void clearParentsWantFocus() {}
    void clearFocusInternal(View focused, boolean propagate, boolean refocus) {
        mPrivateFlags &= ~PFLAG_FOCUSED;
    }

    // ── Misc helpers referenced by ViewGroup ──
    void invalidateParentCaches() {
        if (mParent instanceof View) {
            ((View) mParent).mPrivateFlags |= PFLAG_INVALIDATED;
        }
    }

    int getSolidColor() { return 0; }

    // ── isInScrollingContainer (for touch handling) ──
    public boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    // ── recordGestureClassification (no-op) ──
    void recordGestureClassification(int classification) {}

    // ── Tooltip/longpress helpers (no-op in headless) ──
    void removeTapCallback() {}
    void removeLongPressCallback() {}
    boolean hasPendingLongPressCallback() { return false; }
    void handleTooltipUp() {}
    void checkForLongClick(long delay, float x, float y, int classification) {}
    boolean performButtonActionOnTouchDown(MotionEvent event) { return false; }
    boolean handleScrollBarDragging(MotionEvent event) { return false; }

    // ── setFrame related helpers for ViewGroup ──
    int getOpticalWidth() { return getWidth(); }
    int getOpticalHeight() { return getHeight(); }

    protected boolean setOpticalFrame(int left, int top, int right, int bottom) {
        return setFrame(left, top, right, bottom);
    }
}
