package android.graphics.drawable;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.graphics.drawable.AnimationDrawable
 * OH mapping: frame-by-frame animation via PixelMap sequences
 *
 * Pure Java stub — stores frames and durations; animation callbacks are
 * no-ops (no actual timer scheduling).
 */
public class AnimationDrawable extends Drawable {

    // ── Frame record ─────────────────────────────────────────────────────────

    private static final class Frame {
        final Drawable drawable;
        final int      duration; // ms

        Frame(Drawable drawable, int duration) {
            this.drawable = drawable;
            this.duration = duration;
        }
    }

    // ── State ────────────────────────────────────────────────────────────────

    private final List<Frame> frames      = new ArrayList<>();
    private       boolean     running     = false;
    private       boolean     oneShot     = false;
    private       int         currentFrame = 0;
    private       int         alpha       = 0xFF;

    // ── Constructors ─────────────────────────────────────────────────────────

    public AnimationDrawable() {}

    // ── Frames ───────────────────────────────────────────────────────────────

    /**
     * Adds a frame to the end of the animation.
     *
     * @param frame    drawable for this frame
     * @param duration display duration in milliseconds
     */
    public void addFrame(Drawable frame, int duration) {
        if (frame == null) throw new IllegalArgumentException("frame must not be null");
        frames.add(new Frame(frame, duration));
    }

    public int getNumberOfFrames() { return frames.size(); }

    public Drawable getFrame(int index) {
        checkIndex(index);
        return frames.get(index).drawable;
    }

    public int getDuration(int index) {
        checkIndex(index);
        return frames.get(index).duration;
    }

    // ── Playback control ─────────────────────────────────────────────────────

    public void start() {
        if (!frames.isEmpty()) {
            running      = true;
            currentFrame = 0;
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() { return running; }

    public void setOneShot(boolean oneShot) { this.oneShot = oneShot; }

    public boolean isOneShot() { return oneShot; }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw (delegates to current frame, no-op if empty) ───────────────────

    @Override
    public void draw(Canvas canvas) {
        if (!frames.isEmpty()) {
            frames.get(currentFrame).drawable.draw(canvas);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void checkIndex(int index) {
        if (index < 0 || index >= frames.size()) {
            throw new IndexOutOfBoundsException(
                "Frame index " + index + " out of range [0, " + frames.size() + ")");
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "AnimationDrawable(frames=" + frames.size()
             + ", running=" + running
             + ", oneShot=" + oneShot + ")";
    }
}
