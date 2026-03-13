package android.text.style;
import android.graphics.MaskFilter;
import android.text.TextPaint;
import android.graphics.MaskFilter;
import android.text.TextPaint;

/**
 * Android-compatible MaskFilterSpan stub for the A2OH shim layer.
 *
 * Applies a {@code MaskFilter} to the text it is attached to.
 * In this shim the filter is stored but {@link #updateDrawState} is a no-op.
 */
public class MaskFilterSpan extends CharacterStyle {

    private final Object mFilter;

    /**
     * Creates a MaskFilterSpan from the specified MaskFilter.
     *
     * @param filter the MaskFilter (android.graphics.MaskFilter) to apply
     */
    public MaskFilterSpan(Object filter) {
        mFilter = filter;
    }

    /**
     * Returns the MaskFilter for this span.
     *
     * @return the MaskFilter set in the constructor
     */
    public Object getMaskFilter() {
        return mFilter;
    }

    /**
     * Updates the draw state of the supplied TextPaint.
     * No-op in this shim implementation.
     *
     * @param tp the TextPaint to update
     */
    @Override
    public void updateDrawState(Object tp) {
        // no-op: shim stub
    }
}
