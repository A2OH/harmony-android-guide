package android.transition;
import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;

public class ArcMotion extends PathMotion {
    public ArcMotion() {}
    public ArcMotion(Context p0, AttributeSet p1) {}

    public float getMaximumAngle() { return 0f; }
    public float getMinimumHorizontalAngle() { return 0f; }
    public float getMinimumVerticalAngle() { return 0f; }
    public Path getPath(float p0, float p1, float p2, float p3) { return null; }
    public void setMaximumAngle(float p0) {}
    public void setMinimumHorizontalAngle(float p0) {}
    public void setMinimumVerticalAngle(float p0) {}
}
