package android.view.animation;

import android.view.View;

public class LayoutAnimationController {
    public LayoutAnimationController() {}
    public LayoutAnimationController(Animation animation) {}
    public LayoutAnimationController(Animation animation, float delay) {}

    public static final int ORDER_NORMAL = 0;
    public static final int ORDER_RANDOM = 2;
    public static final int ORDER_REVERSE = 1;

    public Animation getAnimation() { return null; }
    public Animation getAnimationForView(View view) { return null; }
    public float getDelay() { return 0f; }
    public long getDelayForView(View view) { return 0L; }
    public Interpolator getInterpolator() { return null; }
    public int getOrder() { return 0; }
    public boolean isDone() { return false; }
    public void setAnimation(Animation animation) {}
    public void setDelay(float delay) {}
    public void setInterpolator(Interpolator interpolator) {}
    public void setOrder(int order) {}
    public void start() {}
    public boolean willOverlap() { return false; }

    public static class AnimationParameters {
        public int count;
        public int index;
        public AnimationParameters() {}
    }
}
