package android.graphics.drawable;

public interface Animatable2 {

    void clearAnimationCallbacks();
    void registerAnimationCallback(Object p0);
    boolean unregisterAnimationCallback(Object p0);
    void onAnimationEnd(Object p0);
    void onAnimationStart(Object p0);
}
