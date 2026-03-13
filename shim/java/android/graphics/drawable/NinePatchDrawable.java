package android.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Rect;

/**
 * Android-compatible NinePatchDrawable shim. A Drawable that wraps a NinePatch
 * for resizable drawing.
 */
public class NinePatchDrawable extends Drawable {
    private NinePatch mNinePatch;
    private int mAlpha = 255;

    public NinePatchDrawable(NinePatch patch) {
        mNinePatch = patch;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mNinePatch != null) {
            mNinePatch.draw(canvas, getBounds());
        }
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
        return -3; // PixelFormat.TRANSLUCENT
    }

    @Override
    public int getIntrinsicWidth() {
        return mNinePatch != null ? mNinePatch.getWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return mNinePatch != null ? mNinePatch.getHeight() : -1;
    }

    public NinePatch getNinePatch() {
        return mNinePatch;
    }
}
