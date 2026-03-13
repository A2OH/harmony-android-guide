package android.animation;

import android.view.animation.TimeInterpolator;
import java.util.ArrayList;

/**
 * Shim: android.animation.ValueAnimator — pure Java stub.
 *
 * Tracks animation state (running, duration, repeat, interpolator) in Java.
 * No actual animation loop is driven — the shim is used only for compile
 * compatibility and structural fidelity; real animation on OpenHarmony is
 * handled via ArkUI native animation APIs invoked from the bridge layer.
 */
public class ValueAnimator extends Animator {

    // ── Repeat mode constants ──
    public static final int RESTART  = 1;
    public static final int REVERSE  = 2;

    // ── Repeat count sentinel ──
    public static final int INFINITE = -1;

    // ── State ──
    private long     mDuration      = 300;
    private int      mRepeatCount   = 0;
    private int      mRepeatMode    = RESTART;
    private boolean  mRunning       = false;
    private boolean  mStarted       = false;
    protected Object   mAnimatedValue = null;
    private float    mAnimatedFraction = 0f;
    private TimeInterpolator mInterpolator = null;

    /** Values supplied to ofFloat / ofInt — stored for potential bridge use. */
    protected Object[] mValues;

    private final ArrayList<AnimatorUpdateListener> mUpdateListeners = new ArrayList<>();

    // ── Factory methods ──

    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            anim.mValues[i] = values[i];
        }
        if (values.length > 0) {
            anim.mAnimatedValue = values[0];
        }
        return anim;
    }

    public static ValueAnimator ofInt(int... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            anim.mValues[i] = values[i];
        }
        if (values.length > 0) {
            anim.mAnimatedValue = values[0];
        }
        return anim;
    }

    // ── Lifecycle ──

    @Override
    public void start() {
        mStarted = true;
        mRunning = true;
        mAnimatedFraction = 0f;
        for (AnimatorListener l : mListeners) {
            l.onAnimationStart(this);
        }
    }

    @Override
    public void cancel() {
        if (mStarted) {
            mRunning = false;
            mStarted = false;
            for (AnimatorListener l : mListeners) {
                l.onAnimationCancel(this);
            }
            for (AnimatorListener l : mListeners) {
                l.onAnimationEnd(this);
            }
        }
    }

    @Override
    public void end() {
        mRunning = false;
        mStarted = false;
        mAnimatedFraction = 1f;
        for (AnimatorListener l : mListeners) {
            l.onAnimationEnd(this);
        }
    }

    // ── Duration ──

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public Animator setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    // ── State queries ──

    @Override
    public boolean isRunning() { return mRunning; }

    @Override
    public boolean isStarted() { return mStarted; }

    // ── Repeat ──

    public void setRepeatCount(int count) { mRepeatCount = count; }
    public int  getRepeatCount()          { return mRepeatCount; }

    public void setRepeatMode(int mode)   { mRepeatMode = mode; }
    public int  getRepeatMode()           { return mRepeatMode; }

    // ── Interpolator ──

    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    public TimeInterpolator getInterpolator() { return mInterpolator; }

    // ── Animated value / fraction ──

    public Object getAnimatedValue() { return mAnimatedValue; }
    public float  getAnimatedFraction() { return mAnimatedFraction; }

    // ── Update listeners ──

    public void addUpdateListener(AnimatorUpdateListener listener) {
        if (listener != null && !mUpdateListeners.contains(listener)) {
            mUpdateListeners.add(listener);
        }
    }

    public void removeUpdateListener(AnimatorUpdateListener listener) {
        mUpdateListeners.remove(listener);
    }

    public void removeAllUpdateListeners() {
        mUpdateListeners.clear();
    }

    // ── AnimatorUpdateListener interface ──

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator animation);
    }
}
