package android.text.style;

/**
 * Android-compatible BackgroundColorSpan shim. Marks a range of text with a background colour.
 */
public class BackgroundColorSpan {

    private final int mColor;

    public BackgroundColorSpan(int color) {
        mColor = color;
    }

    /** Returns the background colour of this span (ARGB). */
    public int getBackgroundColor() {
        return mColor;
    }
}
