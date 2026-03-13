package android.view.animation;
import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;

public class PathInterpolator extends BaseInterpolator {
    public PathInterpolator(Path p0) {}
    public PathInterpolator(float p0, float p1) {}
    public PathInterpolator(float p0, float p1, float p2, float p3) {}
    public PathInterpolator(Context p0, AttributeSet p1) {}

    public float getInterpolation(float p0) { return 0f; }
}
