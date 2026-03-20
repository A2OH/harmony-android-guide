package android.text.style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;

/**
 * Android shim: LeadingMarginSpan
 * Interface for paragraph spans that indent the leading edge of each line.
 * The Canvas and Paint parameters are typed as Object to avoid pulling in
 * android.graphics dependencies.
 */
public interface LeadingMarginSpan {

    /**
     * Returns the amount of indentation on the leading edge, in pixels.
     *
     * @param first true if this is the first line of the paragraph
     */
    int getLeadingMargin(boolean first);

    /**
     * Renders the leading margin decoration (e.g. a bullet or quote bar).
     *
     * @param canvas    the Canvas to draw onto (typed as Object for shim compatibility)
     * @param pa(int the Paint to use (typed as Object for shim compatibility)
     * @param x         x-coordinate of the leading margin
     * @param dir       paragraph direction: +1 for LTR, -1 for RTL
     * @param top       top of the first line in the paragraph
     * @param baseline  baseline of the first line in the paragraph
     * @param bottom    bottom of the last line in the paragraph
     * @param text      the full CharSequence being laid out
     * @param start     start offset of the spanned range
     * @param end       end offset of the spanned range
     * @param first     true if this is the first line of the paragraph
     * @param layout    the Layout object (typed as Object for shim compatibility)
     */
    void drawLeadingMargin(Object canvas, Object paint, int x, int dir,
                           int top, int baseline, int bottom,
                           CharSequence text, int start, int end,
                           boolean first, Object layout);

    /**
     * Default implementation of {@link LeadingMarginSpan} that stores fixed
     * first-line and rest-of-paragraph indent widths.
     */
    /**
     * Extension of LeadingMarginSpan that specifies the number of lines
     * to apply the first-line margin to.
     */
    interface LeadingMarginSpan2 extends LeadingMarginSpan {
        int getLeadingMarginLineCount();
    }

    class Standard implements LeadingMarginSpan {

        private final int mFirst;
        private final int mRest;

        /**
         * Creates a Standard leading margin with different first-line and
         * continuation-line indents.
         *
         * @param first indent for the first line of the paragraph, in pixels
         * @param rest  indent for subsequent lines, in pixels
         */
        public Standard(int first, int rest) {
            mFirst = first;
            mRest  = rest;
        }

        /**
         * Creates a Standard leading margin with the same indent for all lines.
         *
         * @param every indent for every line, in pixels
         */
        public Standard(int every) {
            this(every, every);
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return first ? mFirst : mRest;
        }

        @Override
        public void drawLeadingMargin(Object canvas, Object paint, int x, int dir,
                                      int top, int baseline, int bottom,
                                      CharSequence text, int start, int end,
                                      boolean first, Object layout) {
            // No decoration drawn by the Standard implementation.
        }
    }
}
