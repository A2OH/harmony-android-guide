package android.gesture;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class GestureOverlayView extends ViewGroup {

    public static final int GESTURE_STROKE_TYPE_SINGLE   = 0;
    public static final int GESTURE_STROKE_TYPE_MULTIPLE = 1;

    public interface OnGesturePerformedListener {
        void onGesturePerformed(GestureOverlayView overlay, Gesture gesture);
    }
    public interface OnGesturingListener {
        void onGesturingStarted(GestureOverlayView overlay);
        void onGesturingEnded(GestureOverlayView overlay);
    }
    public interface OnGestureListener {
        void onGestureStarted(GestureOverlayView overlay, android.view.MotionEvent event);
        void onGesture(GestureOverlayView overlay, android.view.MotionEvent event);
        void onGestureEnded(GestureOverlayView overlay, android.view.MotionEvent event);
        void onGestureCancelled(GestureOverlayView overlay, android.view.MotionEvent event);
    }

    private final List<OnGesturePerformedListener> mGestureListeners = new ArrayList<>();
    private int mGestureStrokeType = GESTURE_STROKE_TYPE_SINGLE;
    private boolean mGestureVisible = true;
    private Gesture mCurrentGesture = null;

    public GestureOverlayView(Context context) { super(context); }
    public GestureOverlayView(Context context, AttributeSet attrs) { super(context, attrs); }
    public GestureOverlayView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public void addOnGesturePerformedListener(OnGesturePerformedListener listener) {
        if (listener != null && !mGestureListeners.contains(listener)) mGestureListeners.add(listener);
    }
    public void removeOnGesturePerformedListener(OnGesturePerformedListener listener) { mGestureListeners.remove(listener); }
    public void fireOnGesturePerformed(Gesture gesture) {
        for (OnGesturePerformedListener l : new ArrayList<>(mGestureListeners)) l.onGesturePerformed(this, gesture);
    }
    public void setGestureStrokeType(int strokeType) { mGestureStrokeType = strokeType; }
    public int getGestureStrokeType() { return mGestureStrokeType; }
    public boolean isGestureVisible() { return mGestureVisible; }
    public void setGestureVisible(boolean visible) { mGestureVisible = visible; }
    public Gesture getCurrentGesture() { return mCurrentGesture; }
    public void setCurrentGesture(Gesture gesture) { mCurrentGesture = gesture; }
    public void clear(boolean animated) { mCurrentGesture = null; }
    public void cancelClearAnimation() {}
}
