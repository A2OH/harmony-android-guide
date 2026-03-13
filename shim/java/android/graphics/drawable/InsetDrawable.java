package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Canvas;
import android.graphics.PixelFormat;

import android.graphics.Canvas;

/**
 * Android-compatible InsetDrawable shim. Wraps a drawable and insets it by
 * specified pixel amounts on each side.
 */
public class InsetDrawable extends Drawable {

    private final Drawable mDrawable;
    private final int mInsetLeft;
    private final int mInsetTop;
    private final int mInsetRight;
    private final int mInsetBottom;

    /**
     * Creates an InsetDrawable with the same inset on all four sides.
     */
    public InsetDrawable(Drawable drawable, int inset) {
        this(drawable, inset, inset, inset, inset);
    }

    /**
     * Creates an InsetDrawable with independent insets on each side.
     */
    public InsetDrawable(Drawable drawable, int insetLeft, int insetTop,
                         int insetRight, int insetBottom) {
        mDrawable = drawable;
        mInsetLeft = insetLeft;
        mInsetTop = insetTop;
        mInsetRight = insetRight;
        mInsetBottom = insetBottom;
    }

    /** Returns the wrapped drawable. */
    public Drawable getDrawable() {
        return mDrawable;
    }

    public int getInsetLeft()   { return mInsetLeft; }
    public int getInsetTop()    { return mInsetTop; }
    public int getInsetRight()  { return mInsetRight; }
    public int getInsetBottom() { return mInsetBottom; }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null) mDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mDrawable != null) mDrawable.setAlpha(alpha);
    }

    @Override
    public int getAlpha() { return mDrawable != null ? mDrawable.getAlpha() : 255; }

    @Override
    public int getOpacity() {
        return mDrawable != null ? mDrawable.getOpacity() : -2; /* PixelFormat.TRANSPARENT */
    }

    @Override
    public int getIntrinsicWidth() {
        if (mDrawable == null) return -1;
        int w = mDrawable.getIntrinsicWidth();
        return w >= 0 ? w + mInsetLeft + mInsetRight : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        if (mDrawable == null) return -1;
        int h = mDrawable.getIntrinsicHeight();
        return h >= 0 ? h + mInsetTop + mInsetBottom : -1;
    }
}
