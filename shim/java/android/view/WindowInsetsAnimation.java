package android.view;
import android.graphics.Insets;
import android.graphics.Interpolator;
import java.lang.annotation.*;
import java.util.List;

public final class WindowInsetsAnimation {
    public WindowInsetsAnimation(int typeMask, Interpolator interpolator, long durationMillis) {}

    public long getDurationMillis() { return 0L; }
    public float getInterpolatedFraction() { return 0f; }
    public int getTypeMask() { return 0; }
    public void setAlpha(float alpha) {}
    public void setFraction(float fraction) {}
    public float getFraction() { return 0f; }
    public float getAlpha() { return 0f; }

    public abstract static class Callback {
        public static final int DISPATCH_MODE_CONTINUE_ON_SUBTREE = 1;
        public static final int DISPATCH_MODE_STOP = 0;

        @Retention(RetentionPolicy.SOURCE)
        @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
        public @interface DispatchMode {}

        public Callback(int dispatchMode) {}

        public int getDispatchMode() { return DISPATCH_MODE_CONTINUE_ON_SUBTREE; }

        public void onPrepare(WindowInsetsAnimation animation) {}
        public abstract WindowInsets onProgress(WindowInsets insets, List<WindowInsetsAnimation> runningAnimations);
        public Bounds onStart(WindowInsetsAnimation animation, Bounds bounds) { return bounds; }
        public void onEnd(WindowInsetsAnimation animation) {}
    }

    public static class Bounds {
        public Bounds(Insets lowerBound, Insets upperBound) {}
        public Bounds() {}
        public Insets getLowerBound() { return Insets.NONE; }
        public Insets getUpperBound() { return Insets.NONE; }
        public Bounds inset(Insets insets) { return this; }
    }
}
