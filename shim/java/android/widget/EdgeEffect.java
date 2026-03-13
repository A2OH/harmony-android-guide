package android.widget;

/**
 * Android-compatible EdgeEffect shim.
 * Provides the visual over-scroll edge glow effect API surface; stub — no actual drawing.
 */
public class EdgeEffect {

    private boolean mFinished = true;
    private int mWidth;
    private int mHeight;

    public EdgeEffect(Object context) {}

    /** Set the size of the surface the edge effect will render into. */
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    /**
     * A view should call this when content is pulled over an edge.
     * @param deltaDistance change in distance since last call, in range [-1, 1]
     */
    public void onPull(float deltaDistance) {
        mFinished = false;
    }

    /** Call this when a pull gesture has ended (finger lifted). */
    public void onRelease() {
        mFinished = true;
    }

    /**
     * Call this when the user over-scrolls with a velocity (e.g. fling).
     * @param velocity velocity in pixels per second
     */
    public void onAbsorb(int velocity) {
        mFinished = false;
    }

    /**
     * Draw the effect onto the provided canvas. Returns true if drawing should continue
     * on the next frame (i.e. the effect is still animating).
     */
    public boolean draw(Object canvas) {
        return !mFinished;
    }

    /** Returns true if the effect has finished animating and does not need to be drawn. */
    public boolean isFinished() { return mFinished; }

    /** Immediately stop the effect. */
    public void finish() { mFinished = true; }

    /** Returns the maximum height the effect can reach in pixels. */
    public int getMaxHeight() {
        return (int) (mHeight * 0.4f + 0.5f);
    }

    public int getColor() { return 0xFF000000; }

    public void setColor(int color) {}
}
