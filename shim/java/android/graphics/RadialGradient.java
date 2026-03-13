package android.graphics;

/**
 * Shim: android.graphics.RadialGradient
 * OH mapping: drawing.OH_Drawing_ShaderEffect (radial gradient)
 *
 * Extends Shader.  Stores gradient parameters; no actual rendering.
 */
public class RadialGradient extends Shader {

    private final float   centerX, centerY, radius;
    private final int[]   colors;
    private final float[] stops;
    private final TileMode tileMode;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * Create a radial gradient shader.
     *
     * @param centerX   x-coordinate of the center
     * @param centerY   y-coordinate of the center
     * @param radius    radius of the gradient circle (must be > 0)
     * @param colors    color array (at least 2 entries)
     * @param stops     relative stop positions in [0,1], or null for even spacing
     * @param tileMode  tile mode for areas outside the gradient
     */
    public RadialGradient(float centerX, float centerY, float radius,
                          int[] colors, float[] stops, TileMode tileMode) {
        if (radius <= 0) throw new IllegalArgumentException("radius must be > 0");
        if (colors == null || colors.length < 2) {
            throw new IllegalArgumentException("needs >= 2 colors");
        }
        this.centerX  = centerX;
        this.centerY  = centerY;
        this.radius   = radius;
        this.colors   = colors.clone();
        this.stops     = (stops != null) ? stops.clone() : null;
        this.tileMode  = (tileMode != null) ? tileMode : TileMode.CLAMP;
    }

    /**
     * Convenience two-color constructor.
     *
     * @param centerX   x-coordinate of the center
     * @param centerY   y-coordinate of the center
     * @param radius    radius of the gradient circle
     * @param centerColor color at the center
     * @param edgeColor   color at the edge
     * @param tileMode  tile mode
     */
    public RadialGradient(float centerX, float centerY, float radius,
                          int centerColor, int edgeColor, TileMode tileMode) {
        this(centerX, centerY, radius, new int[]{centerColor, edgeColor}, null, tileMode);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getRadius()  { return radius; }
    public int[]   getColors() { return colors.clone(); }
    public float[] getStops()  { return (stops != null) ? stops.clone() : null; }
    public TileMode getTileMode() { return tileMode; }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "RadialGradient(center=(" + centerX + "," + centerY + ")"
                + ", radius=" + radius + ", colors=" + colors.length
                + ", tile=" + tileMode + ")";
    }
}
