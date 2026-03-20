package android.animation;

public class RectEvaluator implements TypeEvaluator<android.graphics.Rect> {
    private android.graphics.Rect mRect;

    public RectEvaluator() {}

    public RectEvaluator(android.graphics.Rect reuseRect) {
        mRect = reuseRect;
    }

    public android.graphics.Rect evaluate(float fraction, android.graphics.Rect startValue,
            android.graphics.Rect endValue) {
        int left = startValue.left + (int) ((endValue.left - startValue.left) * fraction);
        int top = startValue.top + (int) ((endValue.top - startValue.top) * fraction);
        int right = startValue.right + (int) ((endValue.right - startValue.right) * fraction);
        int bottom = startValue.bottom + (int) ((endValue.bottom - startValue.bottom) * fraction);
        if (mRect == null) {
            return new android.graphics.Rect(left, top, right, bottom);
        } else {
            mRect.set(left, top, right, bottom);
            return mRect;
        }
    }
}
