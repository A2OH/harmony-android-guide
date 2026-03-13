package android.view;
import android.animation.Animator;
import android.animation.Animator;

import android.animation.Animator;

/**
 * Android-compatible ViewAnimationUtils shim.
 */
public final class ViewAnimationUtils {

    private ViewAnimationUtils() {}

    public static Animator createCircularReveal(Object view,
                                                int centerX, int centerY,
                                                float startRadius, float endRadius) {
        return new NoOpAnimator();
    }

    private static final class NoOpAnimator extends Animator {
        @Override public void start() {}
        @Override public void cancel() {}
        @Override public void end() {}
        @Override public long getDuration() { return 0; }
        @Override public Animator setDuration(long duration) { return this; }
        @Override public boolean isRunning() { return false; }
        @Override public boolean isStarted() { return false; }
    }
}
