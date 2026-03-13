package android.graphics;

/**
 * Android-compatible Outline shim. Defines a simple shape used for shadow casting
 * and clipping.
 */
public class Outline {
    private float mAlpha;
    private boolean mIsEmpty = true;
    private boolean mCanClip;
    private Rect mRect;
    private float mRadius;

    public Outline() {}

    public Outline(Outline src) {
        if (src != null) {
            mAlpha = src.mAlpha;
            mIsEmpty = src.mIsEmpty;
            mCanClip = src.mCanClip;
            mRadius = src.mRadius;
            if (src.mRect != null) {
                mRect = new Rect(src.mRect);
            }
        }
    }

    public void setRect(Rect rect) {
        setRect(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void setRect(int left, int top, int right, int bottom) {
        if (mRect == null) mRect = new Rect();
        mRect.set(left, top, right, bottom);
        mRadius = 0;
        mIsEmpty = false;
        mCanClip = true;
    }

    public void setRoundRect(Rect rect, float radius) {
        setRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius);
    }

    public void setRoundRect(int left, int top, int right, int bottom, float radius) {
        if (mRect == null) mRect = new Rect();
        mRect.set(left, top, right, bottom);
        mRadius = radius;
        mIsEmpty = false;
        mCanClip = true;
    }

    public void setOval(Rect rect) {
        setOval(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void setOval(int left, int top, int right, int bottom) {
        if (mRect == null) mRect = new Rect();
        mRect.set(left, top, right, bottom);
        mIsEmpty = false;
        mCanClip = (right - left) == (bottom - top);
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setEmpty() {
        mIsEmpty = true;
        mCanClip = false;
        mRect = null;
        mRadius = 0;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public boolean canClip() {
        return mCanClip;
    }

    public float getRadius() {
        return mRadius;
    }

    public boolean getRect(Rect outRect) {
        if (mRect != null && !mIsEmpty) {
            outRect.set(mRect);
            return true;
        }
        return false;
    }

    public void offset(int dx, int dy) {
        if (mRect != null) {
            mRect.offset(dx, dy);
        }
    }
}
