package android.graphics;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible Typeface shim. Stores font metadata; no actual rendering.
 */
public class Typeface {
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int BOLD_ITALIC = 3;

    public static final Typeface DEFAULT = new Typeface("sans-serif", NORMAL);
    public static final Typeface DEFAULT_BOLD = new Typeface("sans-serif", BOLD);
    public static final Typeface SANS_SERIF = new Typeface("sans-serif", NORMAL);
    public static final Typeface SERIF = new Typeface("serif", NORMAL);
    public static final Typeface MONOSPACE = new Typeface("monospace", NORMAL);

    private final String mFamily;
    private final int mStyle;
    private final int mWeight;

    private Typeface(String family, int style) {
        this(family, style, style == BOLD || style == BOLD_ITALIC ? 700 : 400);
    }

    private Typeface(String family, int style, int weight) {
        mFamily = family;
        mStyle = style;
        mWeight = weight;
    }

    public static Typeface create(String familyName, int style) {
        return new Typeface(familyName != null ? familyName : "sans-serif", style);
    }

    public static Typeface create(Typeface family, int style) {
        return new Typeface(family != null ? family.mFamily : "sans-serif", style);
    }

    public static Typeface create(Typeface family, int weight, boolean italic) {
        int style = (weight >= 700 ? BOLD : NORMAL) | (italic ? ITALIC : 0);
        return new Typeface(family != null ? family.mFamily : "sans-serif", style, weight);
    }

    public static Typeface createFromFile(File file) {
        return new Typeface(file.getName(), NORMAL);
    }

    public static Typeface createFromFile(String path) {
        return createFromFile(new File(path));
    }

    public static Typeface defaultFromStyle(int style) {
        return create((String) null, style);
    }

    public int getStyle() { return mStyle; }
    public int getWeight() { return mWeight; }
    public boolean isBold() { return (mStyle & BOLD) != 0; }
    public boolean isItalic() { return (mStyle & ITALIC) != 0; }

    /** Auto-generated stub. */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    public @interface Style {}
}
