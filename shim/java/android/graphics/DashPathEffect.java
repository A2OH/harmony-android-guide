package android.graphics;

/**
 * Android-compatible DashPathEffect shim.
 * OH mapping: drawing.OH_Drawing_PathEffect (dash intervals)
 *
 * Strokes a path with alternating on/off dash segments defined by
 * {@code intervals} (must have even length, all values positive).
 * {@code phase} shifts the starting po(int within the dash pattern.
 */
public class DashPathEffect extends PathEffect {

    private final float[] intervals;
    private final float phase;

    /**
     * @param intervals array of even length: [on, off, on, off, ...]. All values > 0.
     * @param phase     offset (in pixels) into the dash pattern at which to start.
     */
    public DashPathEffect(float[] intervals, float phase) {
        if (intervals == null || intervals.length < 2 || (intervals.length & 1) != 0) {
            throw new IllegalArgumentException(
                    "DashPathEffect: intervals must be non-null with even length >= 2");
        }
        this.intervals = intervals.clone();
        this.phase     = phase;
    }

    /** Returns a copy of the dash-interval array. */
    public float[] getIntervals() { return intervals.clone(); }

    /** Returns the phase offset. */
    public float getPhase() { return phase; }
}
