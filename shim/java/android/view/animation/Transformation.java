package android.view.animation;

import android.graphics.Matrix;

public class Transformation {
    public Transformation() {}

    public static final int TYPE_ALPHA = 1;
    public static final int TYPE_BOTH = 3;
    public static final int TYPE_IDENTITY = 0;
    public static final int TYPE_MATRIX = 2;

    protected float mAlpha = 1.0f;
    protected Matrix mMatrix = new Matrix();
    protected int mTransformationType = TYPE_IDENTITY;

    public void clear() {
        mAlpha = 1.0f;
        mMatrix.reset();
        mTransformationType = TYPE_IDENTITY;
    }
    public void compose(Transformation t) {}
    public float getAlpha() { return mAlpha; }
    public Matrix getMatrix() { return mMatrix; }
    public int getTransformationType() { return mTransformationType; }
    public void set(Transformation t) {}
    public void setAlpha(float alpha) { mAlpha = alpha; }
    public void setTransformationType(int type) { mTransformationType = type; }
    public String toShortString() { return "Transformation{}"; }
}
