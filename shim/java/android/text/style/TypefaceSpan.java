package android.text.style;

/**
 * Android shim: TypefaceSpan
 * Changes the typeface family of the spanned text.
 */
public class TypefaceSpan {

    private final String mFamily;

    public TypefaceSpan(String family) {
        mFamily = family;
    }

    /**
     * Returns the font family name, or null if a Typeface was set directly.
     */
    public String getFamily() {
        return mFamily;
    }
}
