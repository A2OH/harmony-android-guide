package android.gesture;
import android.view.ViewGroup;
import android.view.ViewGroup;

import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible GestureOverlayView shim.
 * Extends ViewGroup; captures gesture input (stub — no actual touch handling).
 */
public class GestureOverlayView extends ViewGroup {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    public static final int GESTURE_STROKE_TYPE_SINGLE   = 0;
    public static final int GESTURE_STROKE_TYPE_MULTIPLE = 1;

    // -------------------------------------------------------------------------
    // Listener interfaces
    // -------------------------------------------------------------------------

    /** Object invoked when the user has performed a gesture. */
    public interface OnGesturePerformedListener {
        void onGesturePerformed(GestureOverlayView overlay, Gesture gesture);
    }

    /** Callbacks for the start, progress, and end of a gesture stroke. */
    public interface OnGesturingListener {
        void onGesturingStarted(GestureOverlayView overlay);
        void onGesturingEnded(GestureOverlayView overlay);
    }

    /** Callbacks for individual gesture stroke events. */
    public interface Object {
        void onGestureStarted(GestureOverlayView overlay, Object event);
        void onGesture(GestureOverlayView overlay, Object event);
        void onGestureEnded(GestureOverlayView overlay, Object event);
        void onGestureCancelled(GestureOverlayView overlay, Object event);
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private final List<OnGesturePerformedListener> mGestureListeners = new ArrayList<>();
    private int     mGestureStrokeType = GESTURE_STROKE_TYPE_SINGLE;
    private boolean mGestureVisible    = true;
    private Gesture mCurrentGesture    = null;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public GestureOverlayView() {
        super();
    }

    public GestureOverlayView(Object context) {
        super();
    }

    // -------------------------------------------------------------------------
    // Listener management
    // -------------------------------------------------------------------------

    public void addOnGesturePerformedListener(OnGesturePerformedListener listener) {
        if (listener != null && !mGestureListeners.contains(listener)) {
            mGestureListeners.add(listener);
        }
    }

    public void removeOnGesturePerformedListener(OnGesturePerformedListener listener) {
        mGestureListeners.remove(listener);
    }

    /** Notify all registered listeners that a gesture has been completed. */
    public void fireOnGesturePerformed(Gesture gesture) {
        for (OnGesturePerformedListener l : new ArrayList<>(mGestureListeners)) {
            l.onGesturePerformed(this, gesture);
        }
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    public void setGestureStrokeType(int strokeType) {
        mGestureStrokeType = strokeType;
    }

    public int getGestureStrokeType() {
        return mGestureStrokeType;
    }

    public boolean isGestureVisible() {
        return mGestureVisible;
    }

    public void setGestureVisible(boolean visible) {
        mGestureVisible = visible;
    }

    public Gesture getCurrentGesture() {
        return mCurrentGesture;
    }

    public void setCurrentGesture(Gesture gesture) {
        mCurrentGesture = gesture;
    }

    /** Clear the in-progress gesture. */
    public void clear(boolean animated) {
        mCurrentGesture = null;
    }

    /** Cancel the in-progress gesture. */
    public void cancelClearAnimation() {}

    @Override public String toString() {
        return "GestureOverlayView{strokeType=" + mGestureStrokeType
                + ", visible=" + mGestureVisible + "}";
    }
}
