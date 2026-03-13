package android.text.style;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;

/**
 * Android shim: TextAppearanceSpan
 * Sets the text appearance (family, style, size, colors) of the spanned text.
 * textColor and linkColor are typed as Object to avoid a dependency on
 * android.content.res.ColorStateList.
 */
public class TextAppearanceSpan {

    private final String mFamily;
    private final int    mStyle;
    private final int    mTextSize;
    private final Object mTextColor;
    private final Object mLinkColor;

    /**
     * @param family    font family name (e.g. "sans-serif"), or null
     * @param style     Typeface style flags (e.g. Typeface.BOLD)
     * @param textSize  text size in SP, or -1 to leave unchanged
     * @param textColor ColorStateList for text, or null
     * @param linkColor ColorStateList for links, or null
     */
    public TextAppearanceSpan(String family, int style, int textSize,
                              Object textColor, Object linkColor) {
        mFamily    = family;
        mStyle     = style;
        mTextSize  = textSize;
        mTextColor = textColor;
        mLinkColor = linkColor;
    }

    /** Returns the font family name, or null. */
    public String getFamily() {
        return mFamily;
    }

    /** Returns the Typeface style flags. */
    public int getTextStyle() {
        return mStyle;
    }

    /** Returns the text size in SP, or -1. */
    public int getTextSize() {
        return mTextSize;
    }

    /** Returns the text color (ColorStateList), or null. */
    public Object getTextColor() {
        return mTextColor;
    }

    /** Returns the link color (ColorStateList), or null. */
    public Object getLinkColor() {
        return mLinkColor;
    }
}
