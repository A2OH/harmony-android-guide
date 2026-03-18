package android.view.animation;
import android.icu.number.Scale;
import android.service.autofill.Transformation;
import android.icu.number.Scale;
import android.service.autofill.Transformation;

/**
 * Shim: android.view.animation.Animation — pure Java stub.
 *
 * Represents a legacy view animation (tween). No actual rendering is performed
 * here; the shim is used for compilation compatibility only.
 */
public class Animation {

    // ── Repeat constants ──
    public static final int INFINITE = -1;
    public static final int RESTART  = 1;
    public static final int REVERSE  = 2;

    // ── Relative-to-* constants (used by Translate/Scale/Rotate animations) ──
    public static final int ABSOLUTE      = 0;
    public static final int RELATIVE_TO_SELF   = 1;
    public static final int RELATIVE_TO_PARENT = 2;

    public static final long START_ON_FIRST_FRAME = -1;

    // ── State ──
    private long    mDuration    = 0;
    private long    mStartTime   = START_ON_FIRST_FRAME;
    private int     mRepeatCount = 0;
    private int     mRepeatMode  = RESTART;
    private boolean mFillAfter   = false;
    private boolean mFillBefore  = true;
    private boolean mStarted     = false;
    private boolean mRunning     = false;
    private boolean mInitialized = false;
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

    // Missing methods for View.java compilation
    public long getStartTime() { return mStartTime; }
    public void setStartTime(long startTimeMillis) { mStartTime = startTimeMillis; }
    public boolean isInitialized() { return mInitialized; }
    public void initialize(int width, int height, int parentWidth, int parentHeight) { mInitialized = true; }
    public void initializeInvalidateRegion(int left, int top, int right, int bottom) {}
    public void getInvalidateRegion(int left, int top, int right, int bottom, android.graphics.RectF region, android.view.animation.Transformation t) {}
    public boolean getTransformation(long currentTime, android.view.animation.Transformation outTransformation) { return false; }
    public boolean getTransformation(long currentTime, android.view.animation.Transformation outTransformation, float scale) { return false; }
    public boolean hasAlpha() { return false; }
    public boolean willChangeBounds() { return false; }
    public boolean willChangeTransformationMatrix() { return false; }
    public void setListenerHandler(android.os.Handler handler) {}
    public void detach() {}

    // ── AnimationListener interface ──

    public interface AnimationListener {
        void onAnimationStart(Animation animation);
        void onAnimationEnd(Animation animation);
        void onAnimationRepeat(Animation animation);
    }
}
