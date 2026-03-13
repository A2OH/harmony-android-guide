package android.text.style;

/**
 * Android-compatible ImageSpan shim.
 * Replaces a run of text with an image (Drawable).
 *
 * Drawable is typed as Object because android.graphics.drawable.Drawable
 * does not yet exist in the shim tree.
 */
public class ImageSpan extends ReplacementSpan {

    /** Align the bottom of the image with the bottom of the text. */
    public static final int ALIGN_BOTTOM = 0;
    /** Align the baseline of the image with the baseline of the text. */
    public static final int ALIGN_BASELINE = 1;
    /** Align the center of the image with the center of the text. */
    public static final int ALIGN_CENTER = 2;

    private final Object mDrawable;
    private final String mSource;
    private final int mVerticalAlignment;

    /**
     * Constructs an ImageSpan from a Drawable (typed as Object).
     * @param drawable the Drawable to render
     */
    public ImageSpan(Object drawable) {
        this(drawable, null, ALIGN_BOTTOM);
    }

    /**
     * Constructs an ImageSpan from a Drawable with vertical alignment.
     * @param drawable          the Drawable to render
     * @param verticalAlignment one of ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER
     */
    public ImageSpan(Object drawable, int verticalAlignment) {
        this(drawable, null, verticalAlignment);
    }

    /**
     * Constructs an ImageSpan from a Drawable with an associated source URI.
     * @param drawable the Drawable to render
     * @param source   URI string describing the image source
     */
    public ImageSpan(Object drawable, String source) {
        this(drawable, source, ALIGN_BOTTOM);
    }

    /**
     * Constructs an ImageSpan from a Drawable with a source URI and vertical alignment.
     * @param drawable          the Drawable to render
     * @param source            URI string describing the image source
     * @param verticalAlignment one of ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER
     */
    public ImageSpan(Object drawable, String source, int verticalAlignment) {
        mDrawable = drawable;
        mSource = source;
        mVerticalAlignment = verticalAlignment;
    }

    /** Returns the Drawable used by this span. */
    public Object getDrawable() {
        return mDrawable;
    }

    /** Returns the source URI for the image, or null if none was provided. */
    public String getSource() {
        return mSource;
    }

    /** Returns the vertical alignment of this span. */
    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }

    // ---- ReplacementSpan contract ----

    @Override
    public int getSize(Object paint, CharSequence text, int start, int end, Object fm) {
        return 0;
    }

    @Override
    public void draw(Object canvas, CharSequence text, int start, int end,
            float x, int top, int y, int bottom, Object paint) {
        // no-op shim
    }
}
