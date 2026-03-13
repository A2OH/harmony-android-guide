package android.text.style;

/**
 * Android-compatible DrawableMarginSpan stub for the A2OH shim layer.
 *
 * Stores a drawable object and padding value; provides leading-margin
 * geometry but delegates no actual drawing (no-op on OpenHarmony).
 */
public class DrawableMarginSpan implements LeadingMarginSpan {

    private final Object mDrawable;
    private final int mPad;

    public DrawableMarginSpan(Object drawable) {
        this(drawable, 0);
    }

    public DrawableMarginSpan(Object drawable, int pad) {
        mDrawable = drawable;
        mPad = pad;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mPad;
    }

    @Override
    public void drawLeadingMargin(Object canvas, Object paint, int x, int dir,
            int top, int baseline, int bottom, CharSequence text,
            int start, int end, boolean first, Object layout) {
        // No-op: drawing not supported in A2OH shim layer
    }
}
