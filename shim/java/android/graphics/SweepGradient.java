package android.graphics;

/**
 * Android-compatible SweepGradient shim. Stub; extends Shader.
 */
public class SweepGradient extends Shader {

    private final float mCx;
    private final float mCy;
    private final int[] mColors;
    private final float[] mPositions;

    /**
     * A sweep gradient with an array of colors.
     *
     * @param cx        the x-coordinate of the center
     * @param cy        the y-coordinate of the center
     * @param colors    the colors distributed around the center
     * @param positions relative positions [0..1] for each color, or null for even distribution
     */
    public SweepGradient(float cx, float cy, int[] colors, float[] positions) {
        mCx = cx;
        mCy = cy;
        mColors = colors != null ? colors.clone() : new int[0];
        mPositions = positions != null ? positions.clone() : null;
    }

    /**
     * A sweep gradient from color0 to color1.
     *
     * @param cx     the x-coordinate of the center
     * @param cy     the y-coordinate of the center
     * @param color0 the start color
     * @param color1 the end color
     */
    public SweepGradient(float cx, float cy, int color0, int color1) {
        this(cx, cy, new int[]{color0, color1}, null);
    }

    public float getCenterX()   { return mCx; }
    public float getCenterY()   { return mCy; }
    public int[] getColors()    { return mColors != null ? mColors.clone() : null; }
    public float[] getPositions() { return mPositions != null ? mPositions.clone() : null; }
}
