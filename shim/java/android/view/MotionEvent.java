package android.view;
import android.graphics.Matrix;
import android.os.SystemClock;

/**
 * Shim: android.view.MotionEvent — touch and pointer input event.
 *
 * Wraps a single touch event snapshot passed from the ArkUI touch event
 * callback. Created via MotionEvent.obtain() factory methods; the shim
 * holds a flat array of per-pointer coordinates copied at construction time
 * so the caller's buffer can be reused immediately.
 *
 * Lifecycle: callers should call recycle() when done (no-op here but
 * preserves Android API compatibility).
 */
public final class MotionEvent {

    // ── Action constants ──

    public static final int ACTION_DOWN         = 0;
    public static final int ACTION_UP           = 1;
    public static final int ACTION_MOVE         = 2;
    public static final int ACTION_CANCEL       = 3;
    public static final int ACTION_POINTER_DOWN = 5;
    public static final int ACTION_POINTER_UP   = 6;

    public static final int ACTION_OUTSIDE       = 4;
    public static final int ACTION_HOVER_MOVE    = 7;
    public static final int ACTION_HOVER_ENTER   = 9;
    public static final int ACTION_HOVER_EXIT    = 10;

    public static final int ACTION_BUTTON_PRESS   = 11;
    public static final int ACTION_BUTTON_RELEASE = 12;

    public static final int BUTTON_PRIMARY       = 1;
    public static final int BUTTON_SECONDARY     = 2;
    public static final int BUTTON_STYLUS_PRIMARY = 32;

    /** Mask to extract the base action from a combined action value. */
    public static final int ACTION_MASK          = 0xff;
    /** Bit shift to extract the pointer index from a combined action value. */
    public static final int ACTION_POINTER_INDEX_SHIFT = 8;
    public static final int ACTION_POINTER_INDEX_MASK  = 0xff00;

    // ── Flags ──
    public static final int FLAG_WINDOW_IS_OBSCURED = 0x1;

    // ── Classification constants ──
    public static final int CLASSIFICATION_NONE = 0;
    public static final int CLASSIFICATION_AMBIGUOUS_GESTURE = 1;
    public static final int CLASSIFICATION_DEEP_PRESS = 2;

    // ── Fields ──

    private int    action;
    private long   downTime;
    private long   eventTime;
    private float  x;
    private float  y;
    private float  rawX;
    private float  rawY;
    private int    metaState;

    // Per-pointer data (up to MAX_POINTERS)
    private static final int MAX_POINTERS = 10;
    private int   pointerCount;
    private int[] pointerIds  = new int[MAX_POINTERS];
    private float[] pointerX  = new float[MAX_POINTERS];
    private float[] pointerY  = new float[MAX_POINTERS];

    // Private constructor — use obtain()
    private MotionEvent() {}

    // ── Factory ──

    /**
     * Obtain a simple single-touch event.
     *
     * @param action    One of ACTION_DOWN, ACTION_MOVE, ACTION_UP, ACTION_CANCEL.
     * @param x         X coordinate in view pixels.
     * @param y         Y coordinate in view pixels.
     * @param eventTime Monotonic timestamp in ms (e.g. SystemClock.uptimeMillis()).
     */
    public static MotionEvent obtain(int action, float x, float y, long eventTime) {
        MotionEvent ev = new MotionEvent();
        ev.action      = action;
        ev.x           = x;
        ev.y           = y;
        ev.rawX        = x;
        ev.rawY        = y;
        ev.downTime    = eventTime;
        ev.eventTime   = eventTime;
        ev.pointerCount = 1;
        ev.pointerIds[0] = 0;
        ev.pointerX[0]   = x;
        ev.pointerY[0]   = y;
        return ev;
    }

    /**
     * Obtain an event with full timing parameters (matches AOSP signature).
     */
    public static MotionEvent obtain(long downTime, long eventTime, int action,
                                     float x, float y, int metaState) {
        MotionEvent ev = new MotionEvent();
        ev.action      = action;
        ev.x           = x;
        ev.y           = y;
        ev.rawX        = x;
        ev.rawY        = y;
        ev.downTime    = downTime;
        ev.eventTime   = eventTime;
        ev.metaState   = metaState;
        ev.pointerCount = 1;
        ev.pointerIds[0] = 0;
        ev.pointerX[0]   = x;
        ev.pointerY[0]   = y;
        return ev;
    }

    /**
     * Obtain a multi-pointer event.
     *
     * @param action       Combined action (may include pointer index in high byte).
     * @param pointerCount Number of active pointers.
     * @param pointerIds   Array of pointer IDs (length >= pointerCount).
     * @param xs           Array of X coords per pointer.
     * @param ys           Array of Y coords per pointer.
     * @param eventTime    Monotonic timestamp in ms.
     */
    public static MotionEvent obtain(int action, int pointerCount,
                                     int[] pointerIds, float[] xs, float[] ys,
                                     long eventTime) {
        MotionEvent ev   = new MotionEvent();
        ev.action        = action;
        ev.eventTime     = eventTime;
        int count        = Math.min(pointerCount, MAX_POINTERS);
        ev.pointerCount  = count;
        for (int i = 0; i < count; i++) {
            ev.pointerIds[i] = pointerIds[i];
            ev.pointerX[i]   = xs[i];
            ev.pointerY[i]   = ys[i];
        }
        ev.x    = count > 0 ? xs[0] : 0f;
        ev.y    = count > 0 ? ys[0] : 0f;
        ev.rawX = ev.x;
        ev.rawY = ev.y;
        return ev;
    }

    // ── Accessors ──

    /** Full action value (may include pointer index in high byte). */
    public int getAction() { return action; }

    /** Base action, with pointer-index bits stripped out. */
    public int getActionMasked() { return action & ACTION_MASK; }

    /** Index of the pointer associated with the action (for POINTER_DOWN/UP). */
    public int getActionIndex() {
        return (action & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;
    }

    /** X coordinate of the first pointer. */
    public float getX() { return x; }

    /** Y coordinate of the first pointer. */
    public float getY() { return y; }

    /** X coordinate of the pointer at the given index. */
    public float getX(int pointerIndex) {
        if (pointerIndex < 0 || pointerIndex >= pointerCount) return 0f;
        return pointerX[pointerIndex];
    }

    /** Y coordinate of the pointer at the given index. */
    public float getY(int pointerIndex) {
        if (pointerIndex < 0 || pointerIndex >= pointerCount) return 0f;
        return pointerY[pointerIndex];
    }

    /** Raw (screen) X coordinate of the first pointer. */
    public float getRawX() { return rawX; }

    /** Raw (screen) Y coordinate of the first pointer. */
    public float getRawY() { return rawY; }

    public int   getPointerCount() { return pointerCount; }

    public int   getPointerId(int pointerIndex) {
        if (pointerIndex < 0 || pointerIndex >= pointerCount) return -1;
        return pointerIds[pointerIndex];
    }

    public int   findPointerIndex(int pointerId) {
        for (int i = 0; i < pointerCount; i++) {
            if (pointerIds[i] == pointerId) return i;
        }
        return -1;
    }

    public long  getDownTime() { return downTime; }

    public long  getEventTime() { return eventTime; }

    public int   getMetaState() { return metaState; }

    /** Returns event flags (e.g. FLAG_WINDOW_IS_OBSCURED). */
    public int   getFlags() { return 0; }

    /** Returns the input source (e.g. InputDevice.SOURCE_TOUCHSCREEN). */
    public int   getSource() { return InputDevice.SOURCE_TOUCHSCREEN; }

    /** Returns the motion classification. */
    public int   getClassification() { return CLASSIFICATION_NONE; }

    /** Whether the event targets accessibility focus. */
    public boolean isTargetAccessibilityFocus() { return false; }

    /** Set whether this event targets accessibility focus. */
    public void setTargetAccessibilityFocus(boolean flag) {}

    /**
     * Offset the location of this event. Used during dispatch to translate
     * coordinates into a child view's coordinate space.
     */
    public void setLocation(float newX, float newY) {
        float dx = newX - x;
        float dy = newY - y;
        x = newX;
        y = newY;
        // Also offset per-pointer data for pointer 0
        if (pointerCount > 0) {
            pointerX[0] = newX;
            pointerY[0] = newY;
        }
    }

    /**
     * Offset all coordinates by (deltaX, deltaY). Used during ViewGroup dispatch
     * to translate into child coordinate space.
     */
    public void offsetLocation(float deltaX, float deltaY) {
        x += deltaX;
        y += deltaY;
        rawX += deltaX;
        rawY += deltaY;
        for (int i = 0; i < pointerCount; i++) {
            pointerX[i] += deltaX;
            pointerY[i] += deltaY;
        }
    }

    /** Change the action of this event. */
    public void setAction(int newAction) {
        this.action = newAction;
    }

    /**
     * Copy-obtain: create a new MotionEvent with the same data as the source.
     */
    public static MotionEvent obtain(MotionEvent source) {
        MotionEvent ev = new MotionEvent();
        ev.action = source.action;
        ev.x = source.x;
        ev.y = source.y;
        ev.rawX = source.rawX;
        ev.rawY = source.rawY;
        ev.downTime = source.downTime;
        ev.eventTime = source.eventTime;
        ev.metaState = source.metaState;
        ev.pointerCount = source.pointerCount;
        for (int i = 0; i < source.pointerCount; i++) {
            ev.pointerIds[i] = source.pointerIds[i];
            ev.pointerX[i] = source.pointerX[i];
            ev.pointerY[i] = source.pointerY[i];
        }
        return ev;
    }

    /** No-op: preserved for Android API compatibility. */
    public void recycle() {}

    // Axis constants
    public static final int AXIS_VSCROLL = 9;
    public static final int AXIS_HSCROLL = 10;
    public static final int AXIS_SCROLL = 26;

    // Additional action constants
    public static final int ACTION_SCROLL = 8;

    public int getActionButton() { return 0; }
    public int getButtonState() { return 0; }
    public boolean isTouchEvent() {
        int a = getActionMasked();
        return a == ACTION_DOWN || a == ACTION_UP || a == ACTION_MOVE
            || a == ACTION_CANCEL || a == ACTION_POINTER_DOWN || a == ACTION_POINTER_UP;
    }
    public float getAxisValue(int axis) { return 0f; }
    public float getAxisValue(int axis, int pointerIndex) { return 0f; }
    public boolean isFromSource(int source) { return false; }
    public int getHistorySize() { return 0; }
    public int getPointerIdBits() { return 0; }
    public MotionEvent split(int idBits) { return obtain(this); }
    public boolean isHoverExitPending() { return false; }
    public void setHoverExitPending(boolean hoverExitPending) {}
    public float getXCursorPosition() { return x; }
    public float getYCursorPosition() { return y; }
    public boolean isButtonPressed(int button) { return false; }
    public void transform(Matrix matrix) {}
    public void setSource(int source) {}
    public static MotionEvent obtainNoHistory(MotionEvent event) { return obtain(event); }
    public static String actionToString(int action) { return "ACTION_" + action; }
    public int getSequenceNumber() { return 0; }

    @Override
    public String toString() {
        return "MotionEvent{action=" + action + " x=" + x + " y=" + y
                + " ptrs=" + pointerCount + "}";
    }
}
