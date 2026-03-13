package android.text.style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable;

/**
 * Android-compatible DynamicDrawableSpan shim.
 * Abstract base class for spans that replace text with a drawable object.
 * Subclasses must implement {@link #getDrawable()} to provide the drawable
 * to be rendered in place of the spanned text.
 *
 * Drawable is typed as Object because android.graphics.drawable.Drawable
 * does not yet exist in the shim tree.
 */
public class DynamicDrawableSpan extends ReplacementSpan {

    /** Align the bottom of the drawable with the bottom of the text. */
    public static final int ALIGN_BOTTOM = 0;

    /** Align the bottom of the drawable with the baseline of the text. */
    public static final int ALIGN_BASELINE = 1;

    /** Align the center of the drawable with the center of the text. */
    public static final int ALIGN_CENTER = 2;

    /** The vertical alignment of this span. */
    protected final int mVerticalAlignment;

    /**
     * Constructs a DynamicDrawableSpan with default ALIGN_BOTTOM alignment.
     */
    public DynamicDrawableSpan() {
        mVerticalAlignment = ALIGN_BOTTOM;
    }

    /**
     * Constructs a DynamicDrawableSpan with the specified vertical alignment.
     *
     * @param verticalAlignment one of {@link #ALIGN_BOTTOM},
     *        {@link #ALIGN_BASELINE}, or {@link #ALIGN_CENTER}
     */
    public DynamicDrawableSpan(int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    /**
     * Returns the vertical alignment of this span.
     *
     * @return one of {@link #ALIGN_BOTTOM}, {@link #ALIGN_BASELINE},
     *         or {@link #ALIGN_CENTER}
     */
    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }

    /**
     * Returns the drawable to be rendered. Subclasses must implement this.
     * Typed as Object because the Drawable shim is not yet available.
     *
     * @return the drawable object
     */
    public Object getDrawable() { return null; }

    // ---- ReplacementSpan contract (stub implementations) ----

    @Override
    public int getSize(Object paint, CharSequence text, int start, int end, Object fm) {
        return 0; // stub
    }

    @Override
    public void draw(Object canvas, CharSequence text, int start, int end,
            float x, int top, int y, int bottom, Object paint) {
        // no-op stub
    }
}
