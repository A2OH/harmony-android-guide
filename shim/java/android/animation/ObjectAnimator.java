package android.animation;
import android.graphics.Path;
import android.util.Property;
import android.view.animation.Interpolator;

public final class ObjectAnimator extends ValueAnimator {
    public ObjectAnimator() {}

    public ObjectAnimator clone() { return null; }
    public static ObjectAnimator ofArgb(Object p0, String p1, int... p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofFloat(Object p0, String p1, float... p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofFloat(Object p0, Property p1, float... p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofFloat(Object p0, String p1, String p2, Path p3) { return new ObjectAnimator(); }
    public static ObjectAnimator ofInt(Object p0, String p1, int... p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofInt(Object p0, Property p1, int... p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofInt(Object p0, String p1, String p2, Path p3) { return new ObjectAnimator(); }
    public static ObjectAnimator ofMultiFloat(Object p0, String p1, float[][] p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofMultiInt(Object p0, String p1, int[][] p2) { return new ObjectAnimator(); }
    public static ObjectAnimator ofObject(Object p0, String p1, TypeEvaluator p2, Object... p3) { return new ObjectAnimator(); }
    public void setAutoCancel(boolean p0) {}
    public void setProperty(Property p0) {}
    public void setPropertyName(String p0) {}
    public void setInterpolator(TimeInterpolator interpolator) {}
}
