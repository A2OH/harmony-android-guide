package android.graphics.drawable;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible AnimatedVectorDrawable shim. Stub for vector drawables
 * that animate via registered AnimationCallbacks.
 */
public class AnimatedVectorDrawable extends Drawable {

    private boolean mRunning = false;
    private final List<AnimationCallback> mCallbacks = new ArrayList<>();

    /**
     * Callback interface for AnimatedVectorDrawable animation events.
     */
    public abstract static class AnimationCallback {
        /** Called when the animation starts. */
        public abstract void onAnimationStart(Drawable drawable);
        /** Called when the animation ends. */
        public abstract void onAnimationEnd(Drawable drawable);
    }

    public AnimatedVectorDrawable() {}

    /** Start animating this drawable. */
    public void start() {
        if (!mRunning) {
            mRunning = true;
            for (AnimationCallback cb : mCallbacks) {
                cb.onAnimationStart(this);
            }
            invalidateSelf();
        }
    }

    /** Stop animating this drawable. */
    public void stop() {
        if (mRunning) {
            mRunning = false;
            for (AnimationCallback cb : mCallbacks) {
                cb.onAnimationEnd(this);
            }
            invalidateSelf();
        }
    }

    /** Returns true if the animation is currently running. */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Registers an animation callback to receive start/end notifications.
     */
    public void registerAnimationCallback(AnimationCallback callback) {
        if (callback != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    /**
     * Unregisters a previously registered animation callback.
     *
     * @return true if the callback was removed
     */
    public boolean unregisterAnimationCallback(AnimationCallback callback) {
        return mCallbacks.remove(callback);
    }

    /** Removes all registered animation callbacks. */
    public void clearAnimationCallbacks() {
        mCallbacks.clear();
    }

    @Override
    public void draw(Canvas canvas) {
        // Stub: no actual vector rendering.
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public int getAlpha() { return 255; }

    @Override
    public int getOpacity() { return -3; /* PixelFormat.TRANSLUCENT */ }
}
