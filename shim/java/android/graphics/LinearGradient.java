package android.graphics;

/**
 * Shim: android.graphics.LinearGradient
 * OH mapping: drawing.OH_Drawing_ShaderEffect (linear gradient)
 *
 * Extends Shader.  Stores gradient parameters; no actual rendering.
 */
public class LinearGradient extends Shader {

    private final float   x0, y0, x1, y1;
    private final int[]   colors;
    private final float[] positions;
    private final TileMode tileMode;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * Create a linear gradient shader.
     *
     * @param x0        x-coordinate of start po(int * @param y0        y-coordinate of start po(int * @param x1        x-coordinate of end po(int * @param y1        y-coordinate of end po(int * @param colors    color array (at least 2 entries)
     * @param positions relative position of each color in [0,1], or null for even spacing
     * @param tile      tile mode for areas outside the gradient
     */
    public LinearGradient(float x0, float y0, float x1, float y1,
                          int[] colors, float[] positions, TileMode tile) {
        if (colors == null || colors.length < 2) {
            throw new IllegalArgumentException("needs >= 2 colors");
        }
        this.x0        = x0;
        this.y0        = y0;
        this.x1        = x1;
        this.y1        = y1;
        this.colors    = colors.clone();
        this.positions = (positions != null) ? positions.clone() : null;
        this.tileMode  = (tile != null) ? tile : TileMode.CLAMP;
    }

    /**
     * Convenience two-color constructor.
     *
     * @param x0        x-coordinate of start po(int * @param y0        y-coordinate of start po(int * @param x1        x-coordinate of end po(int * @param y1        y-coordinate of end po(int * @param color0    start color
     * @param color1    end color
     * @param tile      tile mode
     */
    public LinearGradient(float x0, float y0, float x1, float y1,
                          int color0, int color1, TileMode tile) {
        this(x0, y0, x1, y1, new int[]{color0, color1}, null, tile);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public float getX0() { return x0; }
    public float getY0() { return y0; }
    public float getX1() { return x1; }
    public float getY1() { return y1; }
    public int[] getColors()     { return colors.clone(); }
    public float[] getPositions(){ return (positions != null) ? positions.clone() : null; }
    public TileMode getTileMode(){ return tileMode; }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "LinearGradient((" + x0 + "," + y0 + ")->(" + x1 + "," + y1 + ")"
                + ", colors=" + colors.length + ", tile=" + tileMode + ")";
    }
}
