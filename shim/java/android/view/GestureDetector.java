package android.view;

import android.content.Context;

/**
 * Shim: android.view.GestureDetector — recognizes common touch gestures.
 *
 * Routes raw MotionEvent objects to the registered OnGestureListener and
 * OnDoubleTapListener. This shim performs very lightweight gesture
 * recognition in pure Java (no ArkUI gesture recognizer is consulted —
 * OpenHarmony apps that want native gesture recognition should use ArkUI's
 * built-in gesture APIs instead). The implementation is intentionally
 * minimal: it is sufficient to allow existing Android code to compile and
 * exercise basic callback paths.
 *
 * Tap detection: a tap is recognized when ACTION_UP follows ACTION_DOWN
 * within TAP_TIMEOUT ms and within TOUCH_SLOP pixels.
 *
 * Double-tap detection: two consecutive single taps within DOUBLE_TAP_TIMEOUT ms.
 *
 * Long-press detection: ACTION_DOWN held for > LONG_PRESS_TIMEOUT ms without
 * significant movement; detection is approximated at the next ACTION_MOVE/UP
 * poll rather than via a background thread.
 */
public class GestureDetector {

    // ── Thresholds ──
    private static final long  LONG_PRESS_TIMEOUT  = 500L;   // ms
    private static final long  TAP_TIMEOUT         = 300L;   // ms
    private static final long  DOUBLE_TAP_TIMEOUT  = 300L;   // ms
    private static final float TOUCH_SLOP           = 8f;    // pixels

    // ── Listeners ──
    private final OnGestureListener  mGestureListener;
    private OnDoubleTapListener      mDoubleTapListener;

    // ── State ──
    private float mDownX;
    private float mDownY;
    private long  mDownTime;
    private boolean mLongPressDispatched;
    private boolean mInLongPress;

    // Double-tap tracking
    private long  mLastTapTime;
    private float mLastTapX;
    private float mLastTapY;

    // Previous move position for scroll delta computation
    private float mLastMoveX;
    private float mLastMoveY;

    // ── Constructors ──

    public GestureDetector(OnGestureListener listener) {
        this(null, listener);
    }

    public GestureDetector(Context context, OnGestureListener listener) {
        if (listener == null) throw new NullPointerException("listener must not be null");
        mGestureListener = listener;
        if (listener instanceof OnDoubleTapListener) {
            mDoubleTapListener = (OnDoubleTapListener) listener;
        }
    }

    // ── Configuration ──

    /**
     * Set a separate double-tap listener.
     * Pass {@code null} to remove an existing double-tap listener.
     */
    public void setOnDoubleTapListener(OnDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    /** Control whether long-press detection is enabled (default: true). */
    private boolean mIsLongpressEnabled = true;
    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        mIsLongpressEnabled = isLongpressEnabled;
    }
    public boolean isLongpressEnabled() { return mIsLongpressEnabled; }

    // ── Main dispatch ──

    /**
     * Analyze a MotionEvent and fire the appropriate listener callbacks.
     *
     * @param ev The MotionEvent to analyze.
     * @return {@code true} if the event was consumed by a gesture listener.
     */
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev == null) return false;

        int action = ev.getActionMasked();
        float x    = ev.getX();
        float y    = ev.getY();
        long  time = ev.getEventTime();
        boolean handled = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX  = x;
                mDownY  = y;
                mDownTime = time;
                mLastMoveX = x;
                mLastMoveY = y;
                mLongPressDispatched = false;
                mInLongPress = false;
                handled = mGestureListener.onDown(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                // Long-press check (approximated without a Timer/Handler)
                if (mIsLongpressEnabled && !mLongPressDispatched) {
                    if ((time - mDownTime) >= LONG_PRESS_TIMEOUT) {
                        float dx = x - mDownX;
                        float dy = y - mDownY;
                        if (dx * dx + dy * dy <= TOUCH_SLOP * TOUCH_SLOP) {
                            mLongPressDispatched = true;
                            mInLongPress = true;
                            mGestureListener.onLongPress(ev);
                        }
                    }
                }
                // Scroll
                float distX = x - mLastMoveX;
                float distY = y - mLastMoveY;
                if (distX != 0f || distY != 0f) {
                    // Fabricate a MotionEvent for the "previous" position
                    MotionEvent prev = MotionEvent.obtain(
                            MotionEvent.ACTION_MOVE, mLastMoveX, mLastMoveY, time);
                    handled = mGestureListener.onScroll(prev, ev, distX, distY);
                    prev.recycle();
                    mLastMoveX = x;
                    mLastMoveY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!mInLongPress) {
                    long elapsed = time - mDownTime;
                    float dx = x - mDownX;
                    float dy = y - mDownY;
                    float dist2 = dx * dx + dy * dy;

                    if (elapsed < TAP_TIMEOUT && dist2 <= TOUCH_SLOP * TOUCH_SLOP) {
                        // Potential tap
                        if (mDoubleTapListener != null) {
                            long sinceLastTap = time - mLastTapTime;
                            float dtx = x - mLastTapX;
                            float dty = y - mLastTapY;
                            float dt2 = dtx * dtx + dty * dty;
                            if (sinceLastTap < DOUBLE_TAP_TIMEOUT
                                    && dt2 <= (TOUCH_SLOP * 2) * (TOUCH_SLOP * 2)) {
                                // Double tap
                                handled = mDoubleTapListener.onDoubleTap(ev);
                                mLastTapTime = 0; // reset after double-tap
                            } else {
                                // Single tap confirmed (no double-tap follow-up expected
                                // in this approximation — fire immediately)
                                handled = mDoubleTapListener.onSingleTapConfirmed(ev);
                                mLastTapTime = time;
                                mLastTapX    = x;
                                mLastTapY    = y;
                            }
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mInLongPress = false;
                mLongPressDispatched = false;
                break;

            default:
                break;
        }

        return handled;
    }

    // ── Interfaces ──

    /**
     * Listener for basic gesture events.
     *
     * <ul>
     *   <li>{@link #onDown} — called on every ACTION_DOWN; return true to claim the event.</li>
     *   <li>{@link #onSingleTapUp} — called on a quick tap (ACTION_UP within TAP_TIMEOUT).</li>
     *   <li>{@link #onLongPress} — called when the finger is held for LONG_PRESS_TIMEOUT ms.</li>
     *   <li>{@link #onScroll} — called for drag/scroll movements.</li>
     *   <li>{@link #onFling} — called when the finger is released with velocity; shim stub.</li>
     *   <li>{@link #onShowPress} — called shortly after down before a scroll; shim stub.</li>
     * </ul>
     */
    public interface OnGestureListener {
        boolean onDown(MotionEvent e);
        void    onShowPress(MotionEvent e);
        boolean onSingleTapUp(MotionEvent e);
        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
        void    onLongPress(MotionEvent e);
        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }

    /**
     * Listener for double-tap and confirmed single-tap events.
     */
    public interface OnDoubleTapListener {
        boolean onSingleTapConfirmed(MotionEvent e);
        boolean onDoubleTap(MotionEvent e);
        boolean onDoubleTapEvent(MotionEvent e);
    }

    /**
     * Convenience adapter that provides empty implementations of
     * {@link OnGestureListener} so subclasses can override only what they need.
     */
    public static class SimpleOnGestureListener
            implements OnGestureListener, OnDoubleTapListener {
        @Override public boolean onDown(MotionEvent e)            { return false; }
        @Override public void    onShowPress(MotionEvent e)       {}
        @Override public boolean onSingleTapUp(MotionEvent e)     { return false; }
        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                          float distX, float distY) { return false; }
        @Override public void    onLongPress(MotionEvent e)       {}
        @Override public boolean onFling(MotionEvent e1, MotionEvent e2,
                                         float velX, float velY)  { return false; }
        @Override public boolean onSingleTapConfirmed(MotionEvent e) { return false; }
        @Override public boolean onDoubleTap(MotionEvent e)       { return false; }
        @Override public boolean onDoubleTapEvent(MotionEvent e)  { return false; }
    }
}
