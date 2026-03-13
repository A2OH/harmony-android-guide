package android.view.animation;

/**
 * Android shim: android.view.animation.CycleInterpolator
 *
 * Repeats the animation for a specified number of cycles. The rate of change
 * follows a sinusoidal pattern:  sin(2 * π * cycles * input)
 */
public class CycleInterpolator implements Interpolator {

    private final float mCycles;

    /**
     * Creates a new CycleInterpolator.
     *
     * @param cycles the number of cycles to repeat
     */
    public CycleInterpolator(float cycles) {
        mCycles = cycles;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) Math.sin(2 * mCycles * Math.PI * input);
    }
}
