package android.text.style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Android-compatible ReplacementSpan shim.
 * Abstract base for spans that replace a run of text with custom drawing.
 *
 * Parameter types that don't exist in the shim (Paint, Canvas, Paint.FontMetricsInt)
 * are represented as Object to avoid dependency chains.
 */
public class ReplacementSpan extends MetricAffectingSpan {

    /**
     * Returns the width of the span. Called during measurement.
     *
     * @param pa(int the Paint being used for rendering (typed as Object)
     * @param text       the text being measured
     * @param start      start index (inclusive) of the span within text
     * @param end        end index (exclusive) of the span within text
     * @param fm         the font metrics (Paint.FontMetricsInt, typed as Object); may be null
     * @return           the measured width in pixels
     */
    public int getSize(Object paint, CharSequence text, int start, int end, Object fm) { return 0; }

    /**
     * Renders the span.
     *
     * @param canvas     the canvas to draw on (typed as Object)
     * @param text       the text being rendered
     * @param start      start index (inclusive) of the span
     * @param end        end index (exclusive) of the span
     * @param x          left edge of the replacement
     * @param top        top of the line
     * @param y          baseline of the text
     * @param bottom     bottom of the line
     * @param pa(int the Paint to use (typed as Object)
     */
    public void draw(Object canvas, CharSequence text, int start, int end,
            float x, int top, int y, int bottom, Object paint) {}

    /**
     * {@inheritDoc}
     * ReplacementSpan overrides updateMeasureState as a no-op; sizing is done via getSize.
     */
    @Override
    public void updateMeasureState(Object textPaint) {
        // no-op: replacement spans handle measurement via getSize()
    }
}
