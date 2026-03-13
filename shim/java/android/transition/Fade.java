package android.transition;

/**
 * Android-compatible Fade transition shim.
 * Stub — no animation is performed on OpenHarmony.
 */
public class Fade extends Transition {

    public static final int IN  = 0x1;
    public static final int OUT = 0x2;

    private int mFadingMode;

    public Fade() {
        this(IN | OUT);
    }

    public Fade(int fadingMode) {
        mFadingMode = fadingMode;
    }

    public int getFadingMode() {
        return mFadingMode;
    }
}
