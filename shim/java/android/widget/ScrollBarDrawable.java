package android.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

/** Auto-generated stub for AOSP compilation. */
public class ScrollBarDrawable extends Drawable {
    private Drawable mVerticalThumb;
    private Drawable mVerticalTrack;
    private Drawable mHorizontalThumb;
    private Drawable mHorizontalTrack;
    private int mAlpha = 255;

    public ScrollBarDrawable() {}

    public void draw(Canvas canvas) {}
    public void setOpacity(int opacity) {}
    public int getOpacity() { return 0; }
    public void setAlpha(int alpha) { mAlpha = alpha; }
    public int getAlpha() { return mAlpha; }
    public void setColorFilter(android.graphics.ColorFilter cf) {}
    public int getSize(boolean vertical) { return 0; }
    public ScrollBarDrawable mutate() { return this; }

    public void setVerticalThumbDrawable(Drawable thumb) { mVerticalThumb = thumb; }
    public void setVerticalTrackDrawable(Drawable track) { mVerticalTrack = track; }
    public void setHorizontalThumbDrawable(Drawable thumb) { mHorizontalThumb = thumb; }
    public void setHorizontalTrackDrawable(Drawable track) { mHorizontalTrack = track; }
    public Drawable getVerticalThumbDrawable() { return mVerticalThumb; }
    public Drawable getVerticalTrackDrawable() { return mVerticalTrack; }
    public Drawable getHorizontalThumbDrawable() { return mHorizontalThumb; }
    public Drawable getHorizontalTrackDrawable() { return mHorizontalTrack; }

    public void setAlwaysDrawHorizontalTrack(boolean always) {}
    public void setAlwaysDrawVerticalTrack(boolean always) {}
    public void setParameters(int range, int offset, int extent, boolean vertical) {}
    public boolean setState(int[] stateSet) { return true; }
    public void setCallback(View callback) {}

    public static int getThumbLength(int size, int thickness, int extent, int range) { return 0; }
    public static int getThumbOffset(int size, int thumbLength, int extent, int range, int offset) { return 0; }
}
