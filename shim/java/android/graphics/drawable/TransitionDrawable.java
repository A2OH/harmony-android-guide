package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Canvas;

import android.graphics.Canvas;

/**
 * Android-compatible TransitionDrawable shim. A LayerDrawable with exactly
 * two layers that can animate a cross-fade transition between them.
 */
public class TransitionDrawable extends LayerDrawable {

    private boolean mCrossFadeEnabled = false;
    private int mDuration = 0;
    private boolean mReversed = false;
    private boolean mRunning = false;

    /**
     * Creates a new transition drawable with the two supplied drawables.
     * layers[0] is the starting drawable; layers[1] is the ending drawable.
     */
    public TransitionDrawable(Drawable[] layers) {
        super(layers);
    }

    /**
     * Begin the second layer transition. The duration is in milliseconds.
     */
    public void startTransition(int durationMillis) {
        mDuration = durationMillis;
        mReversed = false;
        mRunning = true;
        invalidateSelf();
    }

    /**
     * Show only the first layer with no animation.
     */
    public void resetTransition() {
        mRunning = false;
        invalidateSelf();
    }

    /**
     * Reverses the transition, running from the end back to the start.
     */
    public void reverseTransition(int durationMillis) {
        mDuration = durationMillis;
        mReversed = true;
        mRunning = true;
        invalidateSelf();
    }

    public boolean isCrossFadeEnabled() {
        return mCrossFadeEnabled;
    }

    public void setCrossFadeEnabled(boolean enabled) {
        mCrossFadeEnabled = enabled;
    }

    @Override
    public void draw(Canvas canvas) {
        // Stub: draw both layers; real implementation would interpolate alpha.
        super.draw(canvas);
    }
}
