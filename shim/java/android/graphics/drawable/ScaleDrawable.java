package android.graphics.drawable;

import android.graphics.Canvas;

/**
 * Android-compatible ScaleDrawable shim. Scales a drawable based on its
 * current level (0–10000) by a given width/height scale factor.
 */
public class ScaleDrawable extends Drawable {

    private final Drawable mDrawable;
    private final int mGravity;
    private final float mScaleWidth;
    private final float mScaleHeight;

    /**
     * @param drawable    the drawable to scale
     * @param gravity     Gravity constant for positioning the scaled drawable
     * @param scaleWidth  horizontal scale factor (0..1), or -1 to not scale width
     * @param scaleHeight vertical scale factor (0..1), or -1 to not scale height
     */
    public ScaleDrawable(Drawable drawable, int gravity,
                         float scaleWidth, float scaleHeight) {
        mDrawable = drawable;
        mGravity = gravity;
        mScaleWidth = scaleWidth;
        mScaleHeight = scaleHeight;
    }

    /** Returns the wrapped drawable. */
    public Drawable getDrawable() { return mDrawable; }

    public int getGravity() { return mGravity; }

    public float getScaleWidth() { return mScaleWidth; }

    public float getScaleHeight() { return mScaleHeight; }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        // Stub: real implementation would scale the canvas based on level/10000.
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
        return mDrawable != null ? mDrawable.getOpacity() : -3;
    }

    @Override
    public int getIntrinsicWidth() {
        return mDrawable != null ? mDrawable.getIntrinsicWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return mDrawable != null ? mDrawable.getIntrinsicHeight() : -1;
    }
}
