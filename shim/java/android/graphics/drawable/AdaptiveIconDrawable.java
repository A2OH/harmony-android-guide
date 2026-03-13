package android.graphics.drawable;

import android.graphics.Canvas;

/**
 * Android-compatible AdaptiveIconDrawable shim. An adaptive launcher icon with
 * separate foreground and background layers.
 */
public class AdaptiveIconDrawable extends Drawable {
    private Drawable mForeground;
    private Drawable mBackground;
    private int mAlpha = 255;

    public AdaptiveIconDrawable(Drawable backgroundDr, Drawable foregroundDr) {
        mBackground = backgroundDr;
        mForeground = foregroundDr;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBackground != null) {
            mBackground.draw(canvas);
        }
        if (mForeground != null) {
            mForeground.draw(canvas);
        }
    }

    @Override
    public int getAlpha() { return mAlpha; }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        if (mBackground != null) mBackground.setAlpha(alpha);
        if (mForeground != null) mForeground.setAlpha(alpha);
    }

    public Drawable getForeground() { return mForeground; }
    public Drawable getBackground() { return mBackground; }

    @Override
    public int getIntrinsicWidth() { return 108; /* AdaptiveIconDrawable standard */ }
    @Override
    public int getIntrinsicHeight() { return 108; }
}
