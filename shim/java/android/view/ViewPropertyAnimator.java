package android.view;

/**
 * Android-compatible ViewPropertyAnimator shim.
 * Fluent API — all setters store values but perform no actual animation.
 */
public class ViewPropertyAnimator {
    private float mAlpha = 1f;
    private float mTranslationX = 0f;
    private float mTranslationY = 0f;
    private float mTranslationZ = 0f;
    private float mScaleX = 1f;
    private float mScaleY = 1f;
    private float mRotation = 0f;
    private float mRotationX = 0f;
    private float mRotationY = 0f;
    private float mX = 0f;
    private float mY = 0f;
    private float mZ = 0f;

    private long mDuration = 300;
    private long mStartDelay = 0;
    private Object mInterpolator = null;
    private Runnable mStartAction = null;
    private Runnable mEndAction = null;

    /** Package-private: created by View.animate() */
    public ViewPropertyAnimator() {}

    // ── Property targets ──

    public ViewPropertyAnimator alpha(float value) { mAlpha = value; return this; }
    public ViewPropertyAnimator translationX(float value) { mTranslationX = value; return this; }
    public ViewPropertyAnimator translationY(float value) { mTranslationY = value; return this; }
    public ViewPropertyAnimator translationZ(float value) { mTranslationZ = value; return this; }
    public ViewPropertyAnimator scaleX(float value) { mScaleX = value; return this; }
    public ViewPropertyAnimator scaleY(float value) { mScaleY = value; return this; }
    public ViewPropertyAnimator rotation(float value) { mRotation = value; return this; }
    public ViewPropertyAnimator rotationX(float value) { mRotationX = value; return this; }
    public ViewPropertyAnimator rotationY(float value) { mRotationY = value; return this; }
    public ViewPropertyAnimator x(float value) { mX = value; return this; }
    public ViewPropertyAnimator y(float value) { mY = value; return this; }
    public ViewPropertyAnimator z(float value) { mZ = value; return this; }

    // ── Configuration ──

    public ViewPropertyAnimator setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public long getDuration() { return mDuration; }

    public ViewPropertyAnimator setStartDelay(long startDelay) {
        mStartDelay = startDelay;
        return this;
    }

    public long getStartDelay() { return mStartDelay; }

    public ViewPropertyAnimator setInterpolator(Object interpolator) {
        mInterpolator = interpolator;
        return this;
    }

    public ViewPropertyAnimator withStartAction(Runnable runnable) {
        mStartAction = runnable;
        return this;
    }

    public ViewPropertyAnimator withEndAction(Runnable runnable) {
        mEndAction = runnable;
        return this;
    }

    // ── Lifecycle ──

    /** Start the animation. Stub: immediately fires end action if set. */
    public void start() {
        if (mStartAction != null) mStartAction.run();
        if (mEndAction != null) mEndAction.run();
    }

    /** Cancel any pending animation. No-op in stub. */
    public void cancel() {}
}
