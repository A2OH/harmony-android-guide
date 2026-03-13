package android.view;

public interface WindowInsetsAnimationController {

    void finish(Object p0);
    float getCurrentAlpha();
    int getTypes();
    boolean isCancelled();
    boolean isFinished();
    boolean isReady();
    void setInsetsAndAlpha(Object p0, Object p1, Object p2, Object p3, Object p4);
}
