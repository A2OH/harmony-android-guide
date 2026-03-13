package android.view.animation;

/**
 * Shim: android.view.animation.Animation — pure Java stub.
 *
 * Represents a legacy view animation (tween). No actual rendering is performed
 * here; the shim is used for compilation compatibility only.
 */
public abstract class Animation {

    // ── Repeat constants ──
    public static final int INFINITE = -1;
    public static final int RESTART  = 1;
    public static final int REVERSE  = 2;

    // ── Relative-to-* constants (used by Translate/Scale/Rotate animations) ──
    public static final int ABSOLUTE      = 0;
    public static final int RELATIVE_TO_SELF   = 1;
    public static final int RELATIVE_TO_PARENT = 2;

    // ── State ──
    private long    mDuration    = 0;
    private int     mRepeatCount = 0;
    private int     mRepeatMode  = RESTART;
    private boolean mFillAfter   = false;
    private boolean mFillBefore  = true;
    private boolean mStarted     = false;
    private boolean mRunning     = false;
    private AnimationListener mListener;

    // ── Duration ──

    public void setDuration(long durationMillis) { mDuration = durationMillis; }
    public long getDuration()                    { return mDuration; }

    // ── Repeat ──

    public void setRepeatCount(int repeatCount) { mRepeatCount = repeatCount; }
    public int  getRepeatCount()               { return mRepeatCount; }

    public void setRepeatMode(int repeatMode)  { mRepeatMode = repeatMode; }
    public int  getRepeatMode()                { return mRepeatMode; }

    // ── Fill ──

    public void    setFillAfter(boolean fillAfter)   { mFillAfter = fillAfter; }
    public boolean getFillAfter()                    { return mFillAfter; }

    public void    setFillBefore(boolean fillBefore) { mFillBefore = fillBefore; }
    public boolean getFillBefore()                   { return mFillBefore; }

    // ── Listener ──

    public void setAnimationListener(AnimationListener listener) {
        mListener = listener;
    }

    // ── Lifecycle ──

    public void start() {
        mStarted = true;
        mRunning = true;
        if (mListener != null) {
            mListener.onAnimationStart(this);
        }
    }

    public void cancel() {
        mRunning = false;
        mStarted = false;
        // Android does not call onAnimationEnd on cancel for legacy Animation,
        // but we call the listener to keep apps consistent.
    }

    public void reset() {
        mStarted = false;
        mRunning = false;
    }

    public boolean hasStarted()  { return mStarted; }
    public boolean hasEnded()    { return !mRunning && mStarted; }

    // ── Transformation (subclasses override) ──

    protected void applyTransformation(float interpolatedTime, Object t) {
        // no-op — subclasses implement
    }

    // ── AnimationListener interface ──

    public interface AnimationListener {
        void onAnimationStart(Animation animation);
        void onAnimationEnd(Animation animation);
        void onAnimationRepeat(Animation animation);
    }
}
