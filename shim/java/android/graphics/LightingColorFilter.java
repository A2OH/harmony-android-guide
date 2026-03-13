package android.graphics;

/**
 * Android-compatible LightingColorFilter shim. Stub; extends ColorFilter.
 * Applies a per-channel multiply+add operation: result = src * mul + add.
 */
public class LightingColorFilter extends ColorFilter {

    private final int mColorMultiply;
    private final int mColorAdd;

    /**
     * @param mul the per-channel multiplier (RGB packed as 0xRRGGBB)
     * @param add the per-channel addend     (RGB packed as 0xRRGGBB)
     */
    public LightingColorFilter(int mul, int add) {
        mColorMultiply = mul;
        mColorAdd      = add;
    }

    /** Returns the color-multiply value set in the constructor. */
    public int getColorMultiply() { return mColorMultiply; }

    /** Returns the color-add value set in the constructor. */
    public int getColorAdd() { return mColorAdd; }
}
