package android.text.style;
import android.graphics.Bitmap;
import android.graphics.Bitmap;

/**
 * Android shim: IconMarginSpan
 * A span that draws an icon (bitmap) at the beginning of a paragraph and
 * indents the remaining text by the icon width plus an optional padding.
 * The bitmap is typed as Object to avoid pulling in android.graphics.Bitmap.
 */
public class IconMarginSpan implements LeadingMarginSpan {

    private final Object mBitmap;
    private final int mPad;

    /**
     * Creates an IconMarginSpan from the given bitmap with the specified padding.
     *
     * @param bitmap the icon bitmap (typed as Object for shim compatibility)
     * @param pad    additional padding in pixels between the icon and the text
     */
    public IconMarginSpan(Object bitmap, int pad) {
        mBitmap = bitmap;
        mPad = pad;
    }

    /**
     * Creates an IconMarginSpan from the given bitmap with zero padding.
     *
     * @param bitmap the icon bitmap (typed as Object for shim compatibility)
     */
    public IconMarginSpan(Object bitmap) {
        this(bitmap, 0);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mPad;
    }

    @Override
    public void drawLeadingMargin(Object canvas, Object paint, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Object layout) {
        // No-op in shim layer.
    }
}
