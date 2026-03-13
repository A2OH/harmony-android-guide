package android.graphics;

/**
 * Android-compatible CornerPathEffect shim.
 * OH mapping: drawing.OH_Drawing_PathEffect (corner rounding)
 *
 * Transforms sharp corners in a path into rounded arcs of the given radius.
 */
public class CornerPathEffect extends PathEffect {

    private final float radius;

    /**
     * @param radius  the radius (in pixels) of the rounding arc applied to each corner.
     *                Must be > 0.
     */
    public CornerPathEffect(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("CornerPathEffect: radius must be > 0, got " + radius);
        }
        this.radius = radius;
    }

    /** Returns the corner-rounding radius. */
    public float getRadius() { return radius; }
}
