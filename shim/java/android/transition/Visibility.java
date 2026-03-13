package android.transition;

/**
 * Android-compatible Visibility transition shim.
 * Abstract base class for transitions that respond to visibility changes.
 * Subclasses include Fade, Explode, and Slide.
 * Stub — no animation is performed on OpenHarmony.
 */
public abstract class Visibility extends Transition {

    /** Mode indicating the transition should animate views appearing. */
    public static final int MODE_IN = 0x1;

    /** Mode indicating the transition should animate views disappearing. */
    public static final int MODE_OUT = 0x2;

    private int mMode = MODE_IN | MODE_OUT;

    public Visibility() {}

    /**
     * Sets which visibility changes this transition responds to.
     *
     * @param mode a combination of {@link #MODE_IN} and {@link #MODE_OUT}
     */
    public void setMode(int mode) {
        mMode = mode;
    }

    /**
     * Returns the current transition mode.
     *
     * @return a combination of {@link #MODE_IN} and {@link #MODE_OUT}
     */
    public int getMode() {
        return mMode;
    }
}
