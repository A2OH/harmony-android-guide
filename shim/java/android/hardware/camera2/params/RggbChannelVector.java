package android.hardware.camera2.params;

/**
 * Android-compatible RggbChannelVector shim. Immutable RGGB channel gain vector.
 */
public class RggbChannelVector {

    /** Number of channels (R, G_even, G_odd, B). */
    public static final int COUNT = 4;

    private final float mRed;
    private final float mGreenEven;
    private final float mGreenOdd;
    private final float mBlue;

    /**
     * Create a new RggbChannelVector with the given per-channel gains.
     *
     * @param red        gain for the red channel
     * @param greenEven  gain for the green channel on even rows
     * @param greenOdd   gain for the green channel on odd rows
     * @param blue       gain for the blue channel
     */
    public RggbChannelVector(float red, float greenEven, float greenOdd, float blue) {
        mRed       = red;
        mGreenEven = greenEven;
        mGreenOdd  = greenOdd;
        mBlue      = blue;
    }

    public float getRed()       { return mRed; }
    public float getGreenEven() { return mGreenEven; }
    public float getGreenOdd()  { return mGreenOdd; }
    public float getBlue()      { return mBlue; }

    @Override
    public String toString() {
        return "RggbChannelVector{R=" + mRed + ", G_even=" + mGreenEven
                + ", G_odd=" + mGreenOdd + ", B=" + mBlue + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RggbChannelVector)) return false;
        RggbChannelVector v = (RggbChannelVector) o;
        return Float.compare(v.mRed, mRed) == 0
                && Float.compare(v.mGreenEven, mGreenEven) == 0
                && Float.compare(v.mGreenOdd, mGreenOdd) == 0
                && Float.compare(v.mBlue, mBlue) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(mRed);
        result = 31 * result + Float.floatToIntBits(mGreenEven);
        result = 31 * result + Float.floatToIntBits(mGreenOdd);
        result = 31 * result + Float.floatToIntBits(mBlue);
        return result;
    }
}
