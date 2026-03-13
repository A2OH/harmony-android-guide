package android.text.style;
import android.icu.number.Scale;
import android.icu.number.Scale;

/**
 * Android-compatible RelativeSizeSpan shim.
 * Scales the text size by a relative proportion.
 */
public class RelativeSizeSpan {

    private final float mProportion;

    /**
     * @param proportion Scale factor relative to the current text size, e.g. {@code 1.5f}
     *                   for text 50 % larger.
     */
    public RelativeSizeSpan(float proportion) {
        mProportion = proportion;
    }

    /** Returns the scale factor applied to the text size. */
    public float getSizeChange() {
        return mProportion;
    }
}
