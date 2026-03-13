package android.view;

import android.content.ClipData;

/**
 * Shim: android.view.DragEvent — pure Java stub.
 * Represents an event sent during a drag-and-drop operation.
 */
public class DragEvent {

    public static final int ACTION_DRAG_STARTED = 1;
    public static final int ACTION_DRAG_LOCATION = 2;
    public static final int ACTION_DROP = 3;
    public static final int ACTION_DRAG_ENDED = 4;
    public static final int ACTION_DRAG_ENTERED = 5;
    public static final int ACTION_DRAG_EXITED = 6;

    private int mAction;
    private float mX;
    private float mY;
    private ClipData mClipData;
    private Object mLocalState;
    private boolean mResult;

    /** Package-private constructor. */
    DragEvent() {}

    /** Return the action value of this event. */
    public int getAction() {
        return mAction;
    }

    /** Return the X coordinate of the drag point. */
    public float getX() {
        return mX;
    }

    /** Return the Y coordinate of the drag point. */
    public float getY() {
        return mY;
    }

    /** Return the ClipData associated with the drop. */
    public ClipData getClipData() {
        return mClipData;
    }

    /** Return the local-state object set when the drag started. */
    public Object getLocalState() {
        return mLocalState;
    }

    /** Return the result of the drag-and-drop operation. */
    public boolean getResult() {
        return mResult;
    }

    @Override
    public String toString() {
        return "DragEvent{action=" + mAction + ", x=" + mX + ", y=" + mY + "}";
    }
}
