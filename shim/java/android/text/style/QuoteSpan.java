package android.text.style;

/**
 * Android shim: QuoteSpan
 * Draws a vertical stripe (quote bar) at the beginning of a paragraph.
 */
public class QuoteSpan {

    /** Default stripe color: opaque blue. */
    public static final int STANDARD_COLOR = 0xff0000ff;

    private final int mColor;

    /** Creates a QuoteSpan with the default color. */
    public QuoteSpan() {
        mColor = STANDARD_COLOR;
    }

    /**
     * Creates a QuoteSpan with the given ARGB color.
     *
     * @param color ARGB color of the vertical stripe
     */
    public QuoteSpan(int color) {
        mColor = color;
    }

    /** Returns the ARGB color of the vertical stripe. */
    public int getColor() {
        return mColor;
    }
}
