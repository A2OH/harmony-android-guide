package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Canvas;

import android.graphics.Canvas;

/**
 * Android-compatible RotateDrawable shim. Rotates a child drawable around a
 * configurable pivot po(int between fromDegrees and toDegrees based on level.
 */
public class RotateDrawable extends Drawable {

    private final Drawable mDrawable;
    private float mFromDegrees = 0f;
    private float mToDegrees = 360f;
    private float mPivotX = 0.5f;
    private float mPivotY = 0.5f;
    private boolean mPivotXRelative = true;
    private boolean mPivotYRelative = true;

    public RotateDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public RotateDrawable() {
        this(null);
    }

    /** Returns the wrapped drawable. */
    public Drawable getDrawable() { return mDrawable; }

    public float getFromDegrees() { return mFromDegrees; }

    public void setFromDegrees(float fromDegrees) { mFromDegrees = fromDegrees; }

    public float getToDegrees() { return mToDegrees; }

    public void setToDegrees(float toDegrees) { mToDegrees = toDegrees; }

    public float getPivotX() { return mPivotX; }

    public void setPivotX(float pivotX) {
        mPivotXRelative = false;
        mPivotX = pivotX;
    }

    public float getPivotY() { return mPivotY; }

    public void setPivotY(float pivotY) {
        mPivotYRelative = false;
        mPivotY = pivotY;
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        // Stub: real implementation rotates canvas by interpolated degrees.
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
