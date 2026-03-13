package android.view;

import android.content.Context;

/**
 * Shim: android.view.ScaleGestureDetector — two-finger pinch-to-zoom recognizer.
 *
 * Detects a scale gesture from a stream of MotionEvents. When at least two
 * pointers are active, the detector tracks the distance between pointer 0 and
 * pointer 1 and fires the OnScaleGestureListener callbacks.
 *
 * Scale factor is computed as currentSpan / previousSpan; values > 1.0 mean
 * fingers moving apart (zoom in), values < 1.0 mean fingers approaching (zoom out).
 *
 * Usage:
 * <pre>
 *   ScaleGestureDetector sgd = new ScaleGestureDetector(context, myListener);
 *   // inside onTouchEvent(MotionEvent ev):
 *   sgd.onTouchEvent(ev);
 * </pre>
 */
public class ScaleGestureDetector {

    private static final float MIN_SPAN = 1f; // prevent division by zero

    // ── Listener ──
    private final OnScaleGestureListener mListener;

    // ── State ──
    private boolean mInProgress;
    private float   mFocusX;
    private float   mFocusY;
    private float   mCurrentSpan;
    private float   mPreviousSpan;
    private float   mScaleFactor;

    // Span at gesture start (for cumulative scale)
    private float   mInitialSpan;

    // ── Constructor ──

    public ScaleGestureDetector(Context context, OnScaleGestureListener listener) {
        if (listener == null) throw new NullPointerException("listener must not be null");
        mListener    = listener;
        mScaleFactor = 1f;
    }

    // ── Main dispatch ──

    /**
     * Analyze a MotionEvent for scale gestures.
     *
     * @param event The MotionEvent to process.
     * @return {@code true} if a scale gesture is in progress.
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) return false;

        int action = event.getActionMasked();
        int ptrs   = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mInProgress) {
                    mListener.onScaleEnd(this);
                    mInProgress = false;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (ptrs >= 2) {
                    updateFocusAndSpan(event);
                    mPreviousSpan = mCurrentSpan;
                    mInitialSpan  = mCurrentSpan;
                    mScaleFactor  = 1f;
                    mInProgress = mListener.onScaleBegin(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mInProgress && ptrs >= 2) {
                    float prevSpan = mCurrentSpan;
                    updateFocusAndSpan(event);
                    mPreviousSpan = prevSpan;
                    if (mPreviousSpan > MIN_SPAN) {
                        mScaleFactor = mCurrentSpan / mPreviousSpan;
                    } else {
                        mScaleFactor = 1f;
                    }
                    boolean wantContinue = mListener.onScale(this);
                    if (!wantContinue) {
                        mListener.onScaleEnd(this);
                        mInProgress = false;
                    }
                }
                break;

            default:
                break;
        }

        return mInProgress;
    }

    /** Update mFocusX, mFocusY and mCurrentSpan from the two primary pointers. */
    private void updateFocusAndSpan(MotionEvent event) {
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);

        mFocusX = (x0 + x1) * 0.5f;
        mFocusY = (y0 + y1) * 0.5f;

        float dx = x1 - x0;
        float dy = y1 - y0;
        mCurrentSpan = (float) Math.sqrt(dx * dx + dy * dy);
        if (mCurrentSpan < MIN_SPAN) mCurrentSpan = MIN_SPAN;
    }

    // ── Accessors ──

    /** Returns {@code true} while a scale gesture is in progress. */
    public boolean isInProgress() { return mInProgress; }

    /**
     * Scale factor since the previous scale event.
     * Multiply your current scale by this value each time {@link OnScaleGestureListener#onScale}
     * is called to accumulate the total zoom.
     */
    public float getScaleFactor() { return mScaleFactor; }

    /** X coordinate of the mid-point between the two active pointers. */
    public float getFocusX() { return mFocusX; }

    /** Y coordinate of the mid-point between the two active pointers. */
    public float getFocusY() { return mFocusY; }

    /** Current finger span (distance between pointers), in pixels. */
    public float getCurrentSpan() { return mCurrentSpan; }

    /** Finger span from the previous scale event, in pixels. */
    public float getPreviousSpan() { return mPreviousSpan; }

    // ── Listener interface ──

    /**
     * Listener for scale gesture events.
     *
     * <ul>
     *   <li>{@link #onScaleBegin} — first called when a two-finger gesture starts.
     *       Return {@code false} to reject the gesture.</li>
     *   <li>{@link #onScale} — called for each movement update.
     *       Return {@code false} to end the gesture early.</li>
     *   <li>{@link #onScaleEnd} — called when the gesture is over (pointer lifted
     *       or cancelled). Use {@link #getScaleFactor()} for the final factor.</li>
     * </ul>
     */
    public interface OnScaleGestureListener {
        boolean onScaleBegin(ScaleGestureDetector detector);
        boolean onScale(ScaleGestureDetector detector);
        void    onScaleEnd(ScaleGestureDetector detector);
    }

    /**
     * Convenience adapter with empty {@link #onScaleBegin} returning {@code true}
     * and no-op {@link #onScaleEnd}.
     */
    public static class SimpleOnScaleGestureListener implements OnScaleGestureListener {
        @Override public boolean onScaleBegin(ScaleGestureDetector detector) { return true; }
        @Override public boolean onScale(ScaleGestureDetector detector)      { return true; }
        @Override public void    onScaleEnd(ScaleGestureDetector detector)   {}
    }
}
