package android.view.animation;

/**
 * Android-compatible LayoutAnimationController shim. Controls how child views
 * in a layout are animated, with configurable order and delay between starts.
 */
public class LayoutAnimationController {

    public static final int ORDER_NORMAL  = 0;
    public static final int ORDER_REVERSE = 1;
    public static final int ORDER_RANDOM  = 2;

    private Animation mAnimation;
    private float mDelay;
    private int mOrder = ORDER_NORMAL;

    /**
     * Creates a controller with the given animation and no inter-child delay.
     */
    public LayoutAnimationController(Animation animation) {
        this(animation, 0f);
    }

    /**
     * @param animation the animation to apply to each child
     * @param delay     fractional delay between successive child starts
     *                  (relative to animation duration, e.g. 0.5 = half duration)
     */
    public LayoutAnimationController(Animation animation, float delay) {
        mAnimation = animation;
        mDelay = delay;
    }

    public Animation getAnimation() { return mAnimation; }

    public void setAnimation(Animation animation) { mAnimation = animation; }

    public float getDelay() { return mDelay; }

    public void setDelay(float delay) { mDelay = delay; }

    public int getOrder() { return mOrder; }

    public void setOrder(int order) { mOrder = order; }
}
