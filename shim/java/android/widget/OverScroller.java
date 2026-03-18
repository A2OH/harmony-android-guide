package android.widget;
import android.graphics.Interpolator;
import android.graphics.Interpolator;

/**
 * Shim: android.widget.OverScroller — smooth scrolling/flinging with overscroll.
 *
 * OverScroller is a drop-in replacement for {@code Scroller} that additionally
 * supports over-scroll (bouncing past the content edges). This no-op shim
 * returns sensible defaults so that callers that check {@link #isFinished()}
 * and {@link #computeScrollOffset()} behave correctly without crashing.
 */
public class OverScroller {

    /** Interpolator constant — passed by callers, ignored in this shim. */
    public static final int SCROLL_MODE   = 0;
    public static final int FLING_MODE    = 1;

    private boolean finished = true;
    private int     currX    = 0;
    private int     currY    = 0;
    private int     finalX   = 0;
    private int     finalY   = 0;
    private int     startX   = 0;
    private int     startY   = 0;
    private int     duration = 0;
    private long    startTime;

    /** Create an OverScroller using the default interpolator. */
    public OverScroller() {}

    /**
     * Create an OverScroller with a custom interpolator.
     *
     * @param interpolator ignored in this shim
     */
    public OverScroller(Object interpolator) {}

    // ── State ──

    /** Returns true when the scroll animation has finished. */
    public final boolean isFinished() { return finished; }

    /**
     * Force the finished state.
     *
     * @param finished the new finished value
     */
    public final void forceFinished(boolean finished) {
        this.finished = finished;
    }

    /** Returns the current X offset of the scroll. */
    public final int getCurrX() { return currX; }

    /** Returns the current Y offset of the scroll. */
    public final int getCurrY() { return currY; }

    /** Returns the current scroll velocity on the X axis (px/s). Always 0 in shim. */
    public float getCurrVelocity() { return 0f; }

    /** Returns the start X offset of the scroll. */
    public final int getStartX() { return startX; }

    /** Returns the start Y offset of the scroll. */
    public final int getStartY() { return startY; }

    /** Returns the final X position of the scroll. */
    public final int getFinalX() { return finalX; }

    /** Returns the final Y position of the scroll. */
    public final int getFinalY() { return finalY; }

    /** Returns the duration of the scroll animation in milliseconds. */
    public final int getDuration() { return duration; }

    // ── Compute ──

    /**
     * Update the current position based on elapsed time.
     *
     * In this shim the scroll is always considered finished; returns false
     * immediately, which causes callers to stop requesting redraws.
     *
     * @return false (always finished in this shim)
     */
    public boolean computeScrollOffset() {
        if (finished) return false;
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= duration) {
            currX    = finalX;
            currY    = finalY;
            finished = true;
            return false;
        }
        // Simple linear interpolation for a minimal working implementation
        float t = (float) elapsed / duration;
        currX = startX + Math.round(t * (finalX - startX));
        currY = startY + Math.round(t * (finalY - startY));
        return true;
    }

    // ── Scroll ──

    /**
     * Start a scroll with the given offset and duration.
     *
     * @param startX   starting horizontal scroll offset in pixels
     * @param startY   starting vertical scroll offset in pixels
     * @param dx       horizontal distance to scroll (positive = right)
     * @param dy       vertical distance to scroll (positive = down)
     * @param duration duration of the scroll animation in ms
     */
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.startX    = startX;
        this.startY    = startY;
        this.finalX    = startX + dx;
        this.finalY    = startY + dy;
        this.currX     = startX;
        this.currY     = startY;
        this.duration  = duration;
        this.startTime = System.currentTimeMillis();
        this.finished  = (duration <= 0);
    }

    /**
     * Start a scroll with the default duration (250 ms).
     */
    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, 250);
    }

    // ── Fling ──

    /**
     * Start a fling (velocity-driven scroll with deceleration).
     *
     * In this shim the fling is accepted but immediately considered finished.
     *
     * @param startX    initial X position
     * @param startY    initial Y position
     * @param velocityX initial velocity in px/s a(long X
     * @param velocityY initial velocity in px/s a(long Y
     * @param minX      minimum X value
     * @param maxX      maximum X value
     * @param minY      minimum Y value
     * @param maxY      maximum Y value
     */
    public void fling(int startX, int startY,
                      int velocityX, int velocityY,
                      int minX, int maxX,
                      int minY, int maxY) {
        this.startX   = startX;
        this.startY   = startY;
        this.currX    = startX;
        this.currY    = startY;
        this.finalX   = startX;
        this.finalY   = startY;
        this.duration = 0;
        this.finished = true;
    }

    /**
     * Variant that accepts over-scroll distance parameters.
     */
    public void fling(int startX, int startY,
                      int velocityX, int velocityY,
                      int minX, int maxX,
                      int minY, int maxY,
                      int overX, int overY) {
        fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    }

    // ── Spring-back ──

    /**
     * Animate from the over-scrolled position back to the valid range.
     *
     * @return false — no animation needed in this shim
     */
    public boolean springBack(int startX, int startY,
                              int minX, int maxX,
                              int minY, int maxY) {
        return false;
    }

    // ── Abort ──

    /**
     * Stops the animation. Equivalent to {@code forceFinished(true)}.
     */
    public void abortAnimation() {
        currX    = finalX;
        currY    = finalY;
        finished = true;
    }

    /** Notify that the over-scroll has been absorbed. No-op in this shim. */
    public void notifyHorizontalEdgeReached(int startX, int finalX, int overX) {}
    public void notifyVerticalEdgeReached(int startY, int finalY, int overY)   {}

    /** Returns true if the scroller is over the scroll limits. Always false. */
    public boolean isOverScrolled() { return false; }
}
