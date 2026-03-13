package android.animation;

/**
 * Android-compatible AnimatorInflater shim.
 * Returns a no-op Animator stub; no XML inflation is performed on OpenHarmony.
 */
public class AnimatorInflater {

    private AnimatorInflater() {
    }

    /**
     * Load an Animator from a resource ID. Returns a stub ValueAnimator.
     *
     * @param context Android Context (accepted as Object to avoid hard dependency)
     * @param id      Resource ID (ignored in shim)
     * @return A stub ValueAnimator with default settings
     */
    public static Animator loadAnimator(Object context, int id) {
        return ValueAnimator.ofFloat(0f, 1f);
    }

    /**
     * Load an StateListAnimator from a resource ID. Returns null in the shim.
     */
    public static Object loadStateListAnimator(Object context, int id) {
        return null;
    }
}
