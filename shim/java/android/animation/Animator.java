package android.animation;

import java.util.ArrayList;

/**
 * Shim: android.animation.Animator — pure Java abstract base.
 * No OH bridge calls; animation state is tracked in Java only.
 */
public class Animator implements Cloneable {

    /** Object list; subclasses may read this directly. */
    protected final ArrayList<AnimatorListener> mListeners = new ArrayList<>();

    // ── Abstract lifecycle ──

    public void start() {}
    public void cancel() {}
    public void end() {}

    // ── Abstract timing ──

    public long getDuration() { return 0; }
    public Animator setDuration(long duration) { return null; }

    // ── Abstract state queries ──

    public boolean isRunning() { return false; }
    public boolean isStarted() { return false; }

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

    // ── Clone ──

    @Override
    public Animator clone() {
        try {
            return (Animator) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    // ── AnimatorListener interface ──

    public interface AnimatorListener {
        void onAnimationStart(Animator animation);
        void onAnimationEnd(Animator animation);
        void onAnimationCancel(Animator animation);
        void onAnimationRepeat(Animator animation);
    }

    // ── AnimatorPauseListener interface (API 19+) ──

    public interface AnimatorPauseListener {
        void onAnimationPause(Animator animation);
        void onAnimationResume(Animator animation);
    }
}
