package android.view;
import android.content.Context;
import android.graphics.Region;
import android.util.AttributeSet;

public class SurfaceView extends View {
    public SurfaceView(Context p0) {}
    public SurfaceView(Context p0, AttributeSet p1) {}
    public SurfaceView(Context p0, AttributeSet p1, int p2) {}
    public SurfaceView(Context p0, AttributeSet p1, int p2, int p3) {}

    public boolean gatherTransparentRegion(Region p0) { return false; }
    public SurfaceHolder getHolder() { return null; }
    public SurfaceControl getSurfaceControl() { return null; }
    public void setChildSurfacePackage(Object p0) {}
    public void setSecure(boolean p0) {}
    public void setZOrderMediaOverlay(boolean p0) {}
    public void setZOrderOnTop(boolean p0) {}
}
