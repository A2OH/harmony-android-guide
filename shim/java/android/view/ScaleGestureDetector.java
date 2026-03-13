package android.view;
import android.content.Context;
import android.os.Handler;

public class ScaleGestureDetector {
    public ScaleGestureDetector(Context p0, Object p1) {}
    public ScaleGestureDetector(Context p0, Object p1, Handler p2) {}

    public float getCurrentSpan() { return 0f; }
    public float getCurrentSpanX() { return 0f; }
    public float getCurrentSpanY() { return 0f; }
    public long getEventTime() { return 0L; }
    public float getFocusX() { return 0f; }
    public float getFocusY() { return 0f; }
    public float getPreviousSpan() { return 0f; }
    public float getPreviousSpanX() { return 0f; }
    public float getPreviousSpanY() { return 0f; }
    public float getScaleFactor() { return 0f; }
    public long getTimeDelta() { return 0L; }
    public boolean isInProgress() { return false; }
    public boolean isQuickScaleEnabled() { return false; }
    public boolean isStylusScaleEnabled() { return false; }
    public boolean onTouchEvent(MotionEvent p0) { return false; }
    public void setQuickScaleEnabled(boolean p0) {}
    public void setStylusScaleEnabled(boolean p0) {}
}
