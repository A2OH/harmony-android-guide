package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Canvas;
import android.graphics.Picture;

import android.graphics.Canvas;
import android.graphics.Picture;

/**
 * Android-compatible PictureDrawable shim. Draws a Picture as a Drawable.
 */
public class PictureDrawable extends Drawable {
    private Picture mPicture;
    private int mAlpha = 255;

    public PictureDrawable(Picture picture) {
        mPicture = picture;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mPicture != null) {
            canvas.drawPicture(mPicture);
        }
    }

    @Override
    public int getAlpha() { return mAlpha; }

    @Override
    public void setAlpha(int alpha) { mAlpha = alpha; }

    public Picture getPicture() { return mPicture; }

    public void setPicture(Picture picture) { mPicture = picture; }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {}

    @Override
    public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }

    @Override
    public int getIntrinsicWidth() {
        return mPicture != null ? mPicture.getWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return mPicture != null ? mPicture.getHeight() : -1;
    }
}
