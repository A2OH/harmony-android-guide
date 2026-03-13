package android.animation;

import java.util.ArrayList;

/**
 * Shim: android.animation.Animator — pure Java abstract base.
 * No OH bridge calls; animation state is tracked in Java only.
 */
public abstract class Animator implements Cloneable {

    /** Listener list; subclasses may read this directly. */
    protected final ArrayList<AnimatorListener> mListeners = new ArrayList<>();

    // ── Abstract lifecycle ──

    public abstract void start();
    public abstract void cancel();
    public abstract void end();

    // ── Abstract timing ──

    public abstract long getDuration();
    public abstract Animator setDuration(long duration);

    // ── Abstract state queries ──

    public abstract boolean isRunning();
    public abstract boolean isStarted();

    // ── Listener management ──

    public void addListener(AnimatorListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeListener(AnimatorListener listener) {
        mListeners.remove(listener);
    }

    public void removeAllListeners() {
        mListeners.clear();
    }

    public ArrayList<AnimatorListener> getListeners() {
        return new ArrayList<>(mListeners);
    }

    // ── Pause / resume stubs (API 19+) ──

    public void pause()  {}
    public void resume() {}
    public boolean isPaused() { return false; }

    // ── AnimatorListener interface ──

    public interface AnimatorListener {
        void onAnimationStart(Animator animation);
        void onAnimationEnd(Animator animation);
        void onAnimationCancel(Animator animation);
        void onAnimationRepeat(Animator animation);
    }
}
