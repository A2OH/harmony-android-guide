package android.widget;
import android.telecom.Call;
import android.telecom.Call;

/**
 * Android-compatible Scroller shim for smooth scrolling animation.
 * Stub implementation: interpolates linearly; no real animation loop.
 */
public class Scroller {

    private int mStartX;
    private int mStartY;
    private int mFinalX;
    private int mFinalY;
    private int mCurrX;
    private int mCurrY;
    private int mDuration;
    private long mStartTime;
    private boolean mFinished = true;

    public Scroller(Object context) {}

    public Scroller(Object context, Object interpolator) {}

    public Scroller(Object context, Object interpolator, boolean flywheel) {}

    /** Start a scroll from (startX, startY) by (dx, dy) over duration ms. */
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        mStartX = startX;
        mStartY = startY;
        mFinalX = startX + dx;
        mFinalY = startY + dy;
        mCurrX = startX;
        mCurrY = startY;
        mDuration = duration > 0 ? duration : 250;
        mStartTime = System.currentTimeMillis();
        mFinished = false;
    }

    /** Start a scroll from (startX, startY) by (dx, dy) over 250 ms. */
    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy);
    }

    /** Start a fling from (startX, startY) with given velocities, clamped to min/max. */
    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY) {
        mStartX = startX;
        mStartY = startY;
        mCurrX = startX;
        mCurrY = startY;
        // Simplified: project 300 ms forward at a deceleration factor
        int dx = (int) (velocityX * 0.3f);
        int dy = (int) (velocityY * 0.3f);
        mFinalX = Math.max(minX, Math.min(maxX, startX + dx));
        mFinalY = Math.max(minY, Math.min(maxY, startY + dy));
        mDuration = 300;
        mStartTime = System.currentTimeMillis();
        mFinished = false;
    }

    /**
     * Call this in each animation frame. Returns true if the scroll is still in progress
     * and updates getCurrX/getCurrY accordingly.
     */
    public boolean computeScrollOffset() {
        if (mFinished) return false;
        long elapsed = System.currentTimeMillis() - mStartTime;
        if (elapsed >= mDuration) {
            mCurrX = mFinalX;
            mCurrY = mFinalY;
            mFinished = true;
            return false;
        }
        float progress = (float) elapsed / mDuration;
        mCurrX = mStartX + (int) ((mFinalX - mStartX) * progress);
        mCurrY = mStartY + (int) ((mFinalY - mStartY) * progress);
        return true;
    }

    public int getCurrX() { return mCurrX; }
    public int getCurrY() { return mCurrY; }
    public int getFinalX() { return mFinalX; }
    public int getFinalY() { return mFinalY; }
    public int getStartX() { return mStartX; }
    public int getStartY() { return mStartY; }
    public boolean isFinished() { return mFinished; }
    public void forceFinished(boolean finished) { mFinished = finished; }
    public int getDuration() { return mDuration; }

    /** Returns elapsed milliseconds since the scroll started. */
    public int timePassed() {
        return (int) (System.currentTimeMillis() - mStartTime);
    }

    public void abortAnimation() {
        mCurrX = mFinalX;
        mCurrY = mFinalY;
        mFinished = true;
    }

    public float getCurrVelocity() { return 0f; }
}
