package android.view;
import android.os.SystemClock;
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

    /** Mask to extract the base action from a combined action value. */
    public static final int ACTION_MASK          = 0xff;
    /** Bit shift to extract the pointer index from a combined action value. */
    public static final int ACTION_POINTER_INDEX_SHIFT = 8;
    public static final int ACTION_POINTER_INDEX_MASK  = 0xff00;

    // ── Fields ──

    private int    action;
    private long   eventTime;
    private float  x;
    private float  y;
    private float  rawX;
    private float  rawY;

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
        ev.eventTime   = eventTime;
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

    public long  getEventTime() { return eventTime; }

    /** No-op: preserved for Android API compatibility. */
    public void recycle() {}

    @Override
    public String toString() {
        return "MotionEvent{action=" + action + " x=" + x + " y=" + y
                + " ptrs=" + pointerCount + "}";
    }
}
