package android.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shim: android.animation.AnimatorSet — pure Java stub.
 *
 * Manages a collection of child Animator instances that play together or
 * sequentially. Actual orchestration on OpenHarmony is delegated to the
 * bridge; this shim provides structural fidelity for compilation.
 */
public class AnimatorSet extends Animator {

    private final List<Animator> mPlayTogether    = new ArrayList<>();
    private final List<Animator> mPlaySequentially = new ArrayList<>();

    private long    mDuration   = -1; // -1 = use each child's own duration
    private boolean mRunning    = false;
    private boolean mStarted    = false;

    // ── Builder returned by play() ──

    public Builder play(Animator anim) {
        return new Builder(anim);
    }

    // ── Convenience sequencing ──

    public void playTogether(Animator... animators) {
        mPlayTogether.addAll(Arrays.asList(animators));
    }

    public void playTogether(Iterable<Animator> animators) {
        for (Animator a : animators) {
            mPlayTogether.add(a);
        }
    }

    public void playSequentially(Animator... animators) {
        mPlaySequentially.addAll(Arrays.asList(animators));
    }

    public void playSequentially(List<Animator> animators) {
        mPlaySequentially.addAll(animators);
    }

    // ── Lifecycle ──

    @Override
    public void start() {
        mStarted = true;
        mRunning = true;
        for (AnimatorListener l : mListeners) {
            l.onAnimationStart(this);
        }
        for (Animator a : mPlayTogether)    { a.start(); }
        for (Animator a : mPlaySequentially) { a.start(); }
    }

    @Override
    public void cancel() {
        if (mStarted) {
            for (Animator a : mPlayTogether)    { a.cancel(); }
            for (Animator a : mPlaySequentially) { a.cancel(); }
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
        for (Animator a : mPlayTogether)    { a.end(); }
        for (Animator a : mPlaySequentially) { a.end(); }
        mRunning = false;
        mStarted = false;
        for (AnimatorListener l : mListeners) {
            l.onAnimationEnd(this);
        }
    }

    // ── Duration ──

    /** Sets a unified duration on the set (and all children already added). */
    @Override
    public AnimatorSet setDuration(long duration) {
        mDuration = duration;
        for (Animator a : mPlayTogether)    { a.setDuration(duration); }
        for (Animator a : mPlaySequentially) { a.setDuration(duration); }
        return this;
    }

    @Override
    public long getDuration() { return mDuration; }

    // ── State queries ──

    @Override
    public boolean isRunning() { return mRunning; }

    @Override
    public boolean isStarted() { return mStarted; }

    // ── Builder inner class ──

    public final class Builder {
        private final Animator mAnim;

        Builder(Animator anim) {
            mAnim = anim;
            // The focal animator participates in together-group by default
            if (!mPlayTogether.contains(anim)) {
                mPlayTogether.add(anim);
            }
        }

        /** Play this animator at the same time as another. */
        public Builder with(Animator anim) {
            if (!mPlayTogether.contains(anim)) {
                mPlayTogether.add(anim);
            }
            return this;
        }

        /** Play this animator before another (the other starts when this ends). */
        public Builder before(Animator anim) {
            if (!mPlaySequentially.contains(anim)) {
                mPlaySequentially.add(anim);
            }
            return this;
        }

        /** Play this animator after another finishes. */
        public Builder after(Animator anim) {
            if (!mPlaySequentially.contains(mAnim)) {
                mPlaySequentially.add(mAnim);
            }
            return this;
        }

        /** Delay before playing the focal animator (in milliseconds). */
        public Builder after(long delay) {
            // Stub: store delay intent; actual delay driven by bridge
            return this;
        }
    }
}
