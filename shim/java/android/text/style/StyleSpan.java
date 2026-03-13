package android.text.style;
import android.graphics.Typeface;
import android.graphics.Typeface;

import android.graphics.Typeface;

/**
 * Android-compatible StyleSpan shim. Marks a text range with a bold/italic style.
 * Use {@link Typeface#BOLD}, {@link Typeface#ITALIC}, or {@link Typeface#BOLD_ITALIC}.
 */
public class StyleSpan {

    private final int mStyle;

    /**
     * @param style One of {@link Typeface#NORMAL}, {@link Typeface#BOLD},
     *              {@link Typeface#ITALIC}, {@link Typeface#BOLD_ITALIC}.
     */
    public StyleSpan(int style) {
        mStyle = style;
    }

    /** Returns the typeface style constant for this span. */
    public int getStyle() {
        return mStyle;
    }
}
