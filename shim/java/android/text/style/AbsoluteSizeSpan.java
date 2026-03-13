package android.text.style;

/**
 * Android-compatible AbsoluteSizeSpan shim.
 * Sets the text size to an absolute pixel (or dip) value.
 */
public class AbsoluteSizeSpan {

    private final int     mSize;
    private final boolean mDip;

    /**
     * @param size Size in pixels.
     */
    public AbsoluteSizeSpan(int size) {
        mSize = size;
        mDip  = false;
    }

    /**
     * @param size Size in the unit specified by {@code dip}.
     * @param dip  If {@code true}, {@code size} is in density-independent pixels.
     */
    public AbsoluteSizeSpan(int size, boolean dip) {
        mSize = size;
        mDip  = dip;
    }

    /** Returns the absolute text size. */
    public int getSize() {
        return mSize;
    }

    /** Returns {@code true} if the size is specified in dip rather than pixels. */
    public boolean getDip() {
        return mDip;
    }
}
