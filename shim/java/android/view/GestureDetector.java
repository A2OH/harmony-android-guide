package android.view;
import android.content.Context;
import android.os.Handler;

public class GestureDetector {
    public GestureDetector(Context p0, Object p1) {}
    public GestureDetector(Context p0, Object p1, Handler p2) {}
    public GestureDetector(Context p0, Object p1, Handler p2, boolean p3) {}

    public boolean isLongpressEnabled() { return false; }
    public boolean onGenericMotionEvent(MotionEvent p0) { return false; }
    public boolean onTouchEvent(MotionEvent p0) { return false; }
    public void setContextClickListener(Object p0) {}
    public void setIsLongpressEnabled(boolean p0) {}
    public void setOnDoubleTapListener(Object p0) {}
}
