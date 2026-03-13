package android.graphics.drawable;
import android.graphics.Canvas;
import android.view.Gravity;
import android.graphics.Canvas;
import android.view.Gravity;

import android.graphics.Canvas;

/**
 * Android-compatible ClipDrawable shim. Clips a drawable to a fraction of
 * its bounds based on the current level (0–10000).
 */
public class ClipDrawable extends Drawable {

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    private final Drawable mDrawable;
    private final int mGravity;
    private final int mOrientation;

    /**
     * @param drawable    the drawable to clip
     * @param gravity     Gravity constant controlling which edge is clipped
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public ClipDrawable(Drawable drawable, int gravity, int orientation) {
        mDrawable = drawable;
        mGravity = gravity;
        mOrientation = orientation;
    }

    public Drawable getDrawable() { return mDrawable; }

    public int getGravity() { return mGravity; }

    public int getOrientation() { return mOrientation; }

    /**
     * Draw clipped to the fraction {@code level / 10000} of the bounds.
     * Stub: delegates to the wrapped drawable without actual clipping.
     */
    @Override
    public void draw(Canvas canvas) {
        // Real implementation would clip the canvas rect based on level/10000.
        // Stub simply delegates.
        if (mDrawable != null) mDrawable.draw(canvas);
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
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
