package android.hardware.camera2.params;
import android.graphics.Rect;
import android.graphics.Rect;

import android.graphics.Rect;

/**
 * Android-compatible MeteringRectangle shim. Immutable metering area with weight.
 */
public class MeteringRectangle {

    /** Maximum metering weight. */
    public static final int METERING_WEIGHT_MAX = 1000;
    /** Minimum metering weight (excludes region from metering). */
    public static final int METERING_WEIGHT_MIN = 0;

    private final int mX;
    private final int mY;
    private final int mWidth;
    private final int mHeight;
    private final int mMeteringWeight;

    /**
     * Create a new MeteringRectangle.
     *
     * @param x             left coordinate
     * @param y             top coordinate
     * @param width         width of the rectangle
     * @param height        height of the rectangle
     * @param meteringWeight weight in [METERING_WEIGHT_MIN, METERING_WEIGHT_MAX]
     */
    public MeteringRectangle(int x, int y, int width, int height, int meteringWeight) {
        mX             = x;
        mY             = y;
        mWidth         = width;
        mHeight        = height;
        mMeteringWeight = meteringWeight;
    }

    public int getX()             { return mX; }
    public int getY()             { return mY; }
    public int getWidth()         { return mWidth; }
    public int getHeight()        { return mHeight; }
    public int getMeteringWeight() { return mMeteringWeight; }

    /** Return the rectangle portion of this metering region. */
    public Rect getRect() {
        return new Rect(mX, mY, mX + mWidth, mY + mHeight);
    }

    @Override
    public String toString() {
        return "MeteringRectangle{x=" + mX + ", y=" + mY + ", w=" + mWidth
                + ", h=" + mHeight + ", weight=" + mMeteringWeight + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeteringRectangle)) return false;
        MeteringRectangle r = (MeteringRectangle) o;
        return mX == r.mX && mY == r.mY && mWidth == r.mWidth
                && mHeight == r.mHeight && mMeteringWeight == r.mMeteringWeight;
    }

    @Override
    public int hashCode() {
        int result = mX;
        result = 31 * result + mY;
        result = 31 * result + mWidth;
        result = 31 * result + mHeight;
        result = 31 * result + mMeteringWeight;
        return result;
    }
}
