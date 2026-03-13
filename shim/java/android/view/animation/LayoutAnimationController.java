package android.view.animation;

public class LayoutAnimationController {
    public LayoutAnimationController() {}

    public static final int ORDER_NORMAL = 0;
    public static final int ORDER_RANDOM = 0;
    public static final int ORDER_REVERSE = 0;
    public int mAnimation = 0;
    public int mInterpolator = 0;
    public int mRandomizer = 0;
    public Object getAnimation() { return null; }
    public Object getAnimationForView(Object p0) { return null; }
    public float getDelay() { return 0f; }
    public long getDelayForView(Object p0) { return 0L; }
    public Object getInterpolator() { return null; }
    public int getOrder() { return 0; }
    public int getTransformedIndex(Object p0) { return 0; }
    public boolean isDone() { return false; }
    public void setAnimation(Object p0, Object p1) {}
    public void setAnimation(Object p0) {}
    public void setDelay(Object p0) {}
    public void setInterpolator(Object p0, Object p1) {}
    public void setInterpolator(Object p0) {}
    public void setOrder(Object p0) {}
    public void start() {}
    public boolean willOverlap() { return false; }
}
