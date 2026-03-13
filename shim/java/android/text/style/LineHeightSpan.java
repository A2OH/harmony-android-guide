package android.text.style;
import android.graphics.Paint;
import android.graphics.Paint;

/**
 * Android-compatible LineHeightSpan shim.
 * Interface for spans that can modify the line height of a paragraph.
 *
 * Paint.FontMetricsInt is typed as Object because android.graphics.Paint
 * does not yet exist in the shim tree.
 */
public interface LineHeightSpan {

    /**
     * Called to adjust the line metrics for a line of text.
     *
     * @param text      the full text being laid out
     * @param start     start of the line (inclusive)
     * @param end       end of the line (exclusive)
     * @param spanstartv the top of the span on this line
     * @param lineHeight the line height in pixels
     * @param fm        the font metrics to modify (Paint.FontMetricsInt, typed as Object)
     */
    void chooseHeight(CharSequence text, int start, int end,
            int spanstartv, int lineHeight, Object fm);

    /**
     * Extended interface for line-height spans that are density-aware.
     * Implementations receive the display density so they can scale pixel values correctly.
     */
    interface WithDensity extends LineHeightSpan {

        /**
         * Called to adjust the line metrics for a line of text, with display density.
         *
         * @param text      the full text being laid out
         * @param start     start of the line (inclusive)
         * @param end       end of the line (exclusive)
         * @param spanstartv the top of the span on this line
         * @param lineHeight the line height in pixels
         * @param fm        the font metrics to modify (Paint.FontMetricsInt, typed as Object)
         * @param pa(int the Paint used for the line (typed as Object)
         */
        void chooseHeight(CharSequence text, int start, int end,
                int spanstartv, int lineHeight, Object fm, Object paint);
    }
}
