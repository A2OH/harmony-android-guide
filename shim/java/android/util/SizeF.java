package android.util;

/**
 * Android-compatible SizeF shim. Pure Java — immutable float width×height pair.
 */
public class SizeF {
    private final float mWidth;
    private final float mHeight;

    public SizeF(float width, float height) {
        if (Float.isNaN(width) || Float.isNaN(height)) {
            throw new IllegalArgumentException("width and height must not be NaN");
        }
        mWidth = width;
        mHeight = height;
    }

    public float getWidth() { return mWidth; }
    public float getHeight() { return mHeight; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof SizeF)) return false;
        SizeF other = (SizeF) obj;
        return mWidth == other.mWidth && mHeight == other.mHeight;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(mWidth) ^ Float.floatToIntBits(mHeight);
    }

    @Override
    public String toString() {
        return mWidth + "x" + mHeight;
    }
}
