package android.text.style;

/**
 * Android-compatible ForegroundColorSpan shim. Marks a range of text with a foreground colour.
 */
public class ForegroundColorSpan {

    private final int mColor;

    public ForegroundColorSpan(int color) {
        mColor = color;
    }

    /** Returns the foreground colour of this span (ARGB). */
    public int getForegroundColor() {
        return mColor;
    }
}
