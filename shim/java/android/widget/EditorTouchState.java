package android.widget;

import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Stub for android.widget.EditorTouchState.
 * Tracks touch state for the Editor (cursor/selection handles).
 */
public class EditorTouchState {

    private float mLastDownX;
    private float mLastDownY;
    private float mLastUpX;
    private float mLastUpY;
    private boolean mIsOnHandle;

    public EditorTouchState() {}

    public void update(MotionEvent event, ViewConfiguration config) {}

    public float getLastDownX() { return mLastDownX; }
    public float getLastDownY() { return mLastDownY; }
    public float getLastUpX() { return mLastUpX; }
    public float getLastUpY() { return mLastUpY; }
    public boolean isOnHandle() { return mIsOnHandle; }
    public void setIsOnHandle(boolean onHandle) { mIsOnHandle = onHandle; }
    public boolean isMultiTapInSameArea() { return false; }
    public boolean isDoubleTap() { return false; }
    public boolean isTripleClick() { return false; }
    public boolean isMultiTap() { return false; }
    public boolean isMovedEnoughForDrag() { return false; }
    public float getInitialDragDirectionXYRatio() { return 0f; }

    public static float getXYRatio(int degrees) {
        if (degrees <= 0 || degrees >= 90) return 0f;
        return (float) Math.tan(Math.toRadians(degrees));
    }

    public static boolean isDistanceWithin(float x1, float y1, float x2, float y2, int distance) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (dx * dx + dy * dy) <= (distance * distance);
    }
}
