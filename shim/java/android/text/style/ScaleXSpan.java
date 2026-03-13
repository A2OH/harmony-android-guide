package android.text.style;

/**
 * Android shim: ScaleXSpan
 * Scales the x-axis of the text by a proportion.
 */
public class ScaleXSpan {

    private final float mProportion;

    public ScaleXSpan(float proportion) {
        mProportion = proportion;
    }

    /** Returns the horizontal scale factor. */
    public float getScaleX() {
        return mProportion;
    }
}
