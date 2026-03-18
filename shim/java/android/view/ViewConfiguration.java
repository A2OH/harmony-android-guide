package android.view;

/**
 * Shim: android.view.ViewConfiguration — pure Java stub.
 * Contains methods to standard constants used in the UI for timeouts,
 * sizes, and distances.
 */
public class ViewConfiguration {

    private static final ViewConfiguration sInstance = new ViewConfiguration();

    /** Default touch slop in pixels. */
    private static final int TOUCH_SLOP = 8;
    /** Default minimum fling velocity in pixels/second. */
    private static final int MINIMUM_FLING_VELOCITY = 50;
    /** Default maximum fling velocity in pixels/second. */
    private static final int MAXIMUM_FLING_VELOCITY = 8000;
    /** Default long-press timeout in milliseconds. */
    private static final int LONG_PRESS_TIMEOUT = 500;
    /** Default tap timeout in milliseconds. */
    private static final int TAP_TIMEOUT = 100;
    /** Default double-tap timeout in milliseconds. */
    private static final int DOUBLE_TAP_TIMEOUT = 300;
    /** Default paging touch slop in pixels. */
    private static final int PAGING_TOUCH_SLOP = 16;
    /** Default overscroll distance in pixels. */
    private static final int OVERSCROLL_DISTANCE = 0;
    /** Default overfling distance in pixels. */
    private static final int OVERFLING_DISTANCE = 6;

    private ViewConfiguration() {}

    /**
     * Returns a ViewConfiguration instance for the given context.
     * @param context the application context (Object to avoid dependency chains)
     */
    public static ViewConfiguration get(Object context) {
        return sInstance;
    }

    /** Distance in pixels a touch can wander before being considered a scroll. */
    public int getScaledTouchSlop() {
        return TOUCH_SLOP;
    }

    /** Minimum velocity for a fling in pixels per second. */
    public int getScaledMinimumFlingVelocity() {
        return MINIMUM_FLING_VELOCITY;
    }

    /** Maximum velocity for a fling in pixels per second. */
    public int getScaledMaximumFlingVelocity() {
        return MAXIMUM_FLING_VELOCITY;
    }

    /** Distance in pixels between two taps for it to be a double-tap. */
    public int getScaledDoubleTapSlop() {
        return TOUCH_SLOP;
    }

    /** Distance a touch can wander before a paging gesture starts. */
    public int getScaledPagingTouchSlop() {
        return PAGING_TOUCH_SLOP;
    }

    /** Max distance to overscroll for edge effects. */
    public int getScaledOverscrollDistance() {
        return OVERSCROLL_DISTANCE;
    }

    /** Max distance to overfling for edge effects. */
    public int getScaledOverflingDistance() {
        return OVERFLING_DISTANCE;
    }

    /** Return the duration before a press turns into a long press (ms). */
    public static int getLongPressTimeout() {
        return LONG_PRESS_TIMEOUT;
    }

    /** Return the timeout for a tap (ms). */
    public static int getTapTimeout() {
        return TAP_TIMEOUT;
    }

    /** Return the timeout for a double-tap (ms). */
    public static int getDoubleTapTimeout() {
        return DOUBLE_TAP_TIMEOUT;
    }

    /** Return the maximum drawing cache size in bytes. */
    public static int getMaximumDrawingCacheSize() {
        return 480 * 800 * 4; // ARGB_8888, typical screen
    }

    public float getScaledVerticalScrollFactor() { return 64.0f; }
    public float getScaledHorizontalScrollFactor() { return 64.0f; }
    public int getScaledWindowTouchSlop() { return TOUCH_SLOP * 2; }
    public static int getScrollBarSize() { return 10; }
    public int getScaledScrollBarSize() { return 10; }
    public static int getScrollDefaultDelay() { return 300; }
    public static int getScrollBarFadeDuration() { return 250; }
    public static int getPressedStateDuration() { return 64; }
    public static int getEdgeSlop() { return 12; }
    public static boolean isFadingMarqueeEnabled() { return true; }
    public int getScaledFadingEdgeLength() { return 12; }
    public int getScaledMaximumDrawingCacheSize() { return 480 * 800 * 4; }
    public int getScaledHoverSlop() { return TOUCH_SLOP; }
    public int getScaledMinScrollbarTouchTarget() { return 48; }
    public float getScaledAmbiguousGestureMultiplier() { return 2.0f; }
    public static int getHoverTooltipShowTimeout() { return 500; }
    public static int getHoverTooltipHideTimeout() { return 15000; }
    public static int getHoverTooltipHideShortTimeout() { return 3000; }
    public static int getLongPressTooltipHideTimeout() { return 1500; }
    public static long getSendRecurringAccessibilityEventsInterval() { return 100; }
}
