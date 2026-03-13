package android.text.style;

/**
 * Android-compatible MetricAffectingSpan shim.
 * Abstract class for spans that affect text metrics (size, ascent, descent).
 * Extends CharacterStyle as in the Android framework.
 */
public abstract class MetricAffectingSpan extends CharacterStyle {

    /**
     * Implement this method to update the TextPaint used for measuring text.
     * @param textPaint the TextPaint to modify (typed as Object to avoid dependency chain)
     */
    public abstract void updateMeasureState(Object textPaint);

    /**
     * {@inheritDoc}
     *
     * Subclasses should override {@link #updateDrawState} to style the text for drawing.
     * The default no-op implementation satisfies the CharacterStyle contract.
     */
    @Override
    public void updateDrawState(Object tp) {
        // no-op shim default; real subclasses override
    }
}
