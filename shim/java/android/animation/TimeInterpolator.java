package android.animation;

/**
 * Android-compatible TimeInterpolator shim.
 */
public interface TimeInterpolator {
    float getInterpolation(float input);
}
