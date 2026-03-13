package android.graphics.drawable;

import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.AnimatedImageDrawable
 * OH mapping: media.AnimatedImage / image.PixelMap sequence (API 28+)
 *
 * Represents an animated image (e.g. an animated WebP or GIF) that exposes
 * the {@code Animatable} contract.  This shim models the playback-state
 * lifecycle; actual frame rendering requires the OH image bridge.
 *
 * Note: Android's real class implements android.graphics.drawable.Animatable2.
 * The Animatable interface is not yet in this shim tree, so the three
 * Animatable methods (start / stop / isRunning) are provided directly.
 */
public class AnimatedImageDrawable extends Drawable {

    // ── Playback state ────────────────────────────────────────────────────────

    private boolean mRunning   = false;
    private boolean mAutoPlay  = false;
    private int     mRepeatCount = -1;   // -1 = infinite, 0 = play once, n = n+1 plays
    private int     mAlpha     = 0xFF;

    // ── Constructor ───────────────────────────────────────────────────────────

    public AnimatedImageDrawable() {}

    // ── Animatable API ────────────────────────────────────────────────────────

    /**
     * Starts or resumes animation playback.
     */
    public void start() {
        mRunning = true;
        invalidateSelf();
    }

    /**
     * Stops animation playback and rewinds to the first frame.
     */
    public void stop() {
        mRunning = false;
        invalidateSelf();
    }

    /**
     * Returns {@code true} while the animation is playing.
     */
    public boolean isRunning() {
        return mRunning;
    }

    // ── Repeat count ─────────────────────────────────────────────────────────

    /**
     * Sets how many times the animation repeats after the first play-through.
     * Use {@code -1} for infinite looping (the default), {@code 0} for a
     * single play, or a positive value for {@code n} additional repetitions.
     */
    public void setRepeatCount(int repeatCount) {
        mRepeatCount = repeatCount;
    }

    /** Returns the current repeat count. */
    public int getRepeatCount() {
        return mRepeatCount;
    }

    // ── Auto-play ─────────────────────────────────────────────────────────────

    /**
     * If {@code true}, the animation starts as soon as it is drawn for the
     * first time.  Defaults to {@code false}.
     */
    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    // ── Alpha ─────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return mAlpha; }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha & 0xFF;
        invalidateSelf();
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    /**
     * Draw the current animation frame onto {@code canvas}.
     * In this shim, the draw is a no-op; real rendering requires the OH bridge.
     */
    @Override
    public void draw(Canvas canvas) {
        if (mAutoPlay && !mRunning) start();
        /* no-op: actual frame rendering requires OH image bridge */
    }

    // ── Opacity ───────────────────────────────────────────────────────────────

    @Override
    public int getOpacity() { return -3; /* PixelFormat.TRANSLUCENT */ }

    // ── Object overrides ──────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "AnimatedImageDrawable(running=" + mRunning
             + ", repeatCount=" + mRepeatCount + ")";
    }
}
