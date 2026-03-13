package android.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;

/**
 * Shim: android.graphics.drawable.DrawableWrapper
 * OH mapping: decorator pattern over PixelMap-backed drawables
 *
 * A Drawable that wraps another Drawable and delegates all drawing operations
 * to it.  Concrete subclasses may override individual methods to apply
 * transformations (e.g. clipping, scaling, tinting).
 */
public class DrawableWrapper extends Drawable implements Drawable.Callback {

    private Drawable mDrawable;
    private int      mAlpha = 0xFF;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a DrawableWrapper that wraps {@code drawable}.
     *
     * @param drawable the Drawable to wrap; may be {@code null}
     */
    public DrawableWrapper(Drawable drawable) {
        setDrawable(drawable);
    }

    // ── Wrapped drawable access ───────────────────────────────────────────────

    /** Returns the wrapped drawable, or {@code null} if none has been set. */
    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Sets the drawable being wrapped.  The previous drawable's callback is
     * cleared and the new one's callback is set to this wrapper.
     *
     * @param drawable the new Drawable to wrap; may be {@code null}
     */
    public void setDrawable(Drawable drawable) {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
        }
        mDrawable = drawable;
        if (mDrawable != null) {
            mDrawable.setCallback(this);
            mDrawable.setAlpha(mAlpha);
        }
        invalidateSelf();
    }

    // ── Drawable.Callback (propagate invalidation upward) ────────────────────

    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Callback cb = getCallback();
        if (cb != null) cb.scheduleDrawable(this, what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        Callback cb = getCallback();
        if (cb != null) cb.unscheduleDrawable(this, what);
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null) mDrawable.draw(canvas);
    }

    // ── Alpha ─────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() {
        return (mDrawable != null) ? mDrawable.getAlpha() : mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha & 0xFF;
        if (mDrawable != null) mDrawable.setAlpha(mAlpha);
    }

    // ── ColorFilter ───────────────────────────────────────────────────────────

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mDrawable != null) mDrawable.setColorFilter(colorFilter);
    }

    // ── Opacity ───────────────────────────────────────────────────────────────

    @Override
    public int getOpacity() {
        return (mDrawable != null) ? mDrawable.getOpacity() : -3;
    }

    // ── Intrinsic size ────────────────────────────────────────────────────────

    @Override
    public int getIntrinsicWidth() {
        return (mDrawable != null) ? mDrawable.getIntrinsicWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return (mDrawable != null) ? mDrawable.getIntrinsicHeight() : -1;
    }

    // ── Object overrides ──────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "DrawableWrapper(" + mDrawable + ")";
    }
}
