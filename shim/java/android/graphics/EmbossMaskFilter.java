package android.graphics;

/**
 * Android-compatible EmbossMaskFilter shim. Stub; extends MaskFilter.
 * Applies an emboss effect to text/shapes.
 */
public class EmbossMaskFilter extends MaskFilter {

    private final float[] mDirection;
    private final float   mAmbient;
    private final float   mSpecular;
    private final float   mBlurRadius;

    /**
     * @param direction  a 3-element array [x, y, z] specifying the light direction
     * @param ambient    ambient light coefficient (0..1)
     * @param specular   specular highlight exponent
     * @param blurRadius the radius of the blur used to create the emboss look
     */
    public EmbossMaskFilter(float[] direction, float ambient, float specular, float blurRadius) {
        mDirection  = direction != null ? direction.clone() : new float[]{0, 1, 0};
        mAmbient    = ambient;
        mSpecular   = specular;
        mBlurRadius = blurRadius;
    }

    public float[] getDirection()  { return mDirection.clone(); }
    public float   getAmbient()    { return mAmbient; }
    public float   getSpecular()   { return mSpecular; }
    public float   getBlurRadius() { return mBlurRadius; }
}
