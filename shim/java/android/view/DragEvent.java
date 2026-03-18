package android.view;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Parcel;
import android.os.Parcelable;

public class DragEvent implements Parcelable {
    public static final int ACTION_DRAG_STARTED  = 1;
    public static final int ACTION_DRAG_LOCATION = 2;
    public static final int ACTION_DROP          = 3;
    public static final int ACTION_DRAG_ENDED    = 4;
    public static final int ACTION_DRAG_ENTERED  = 5;
    public static final int ACTION_DRAG_EXITED   = 6;

    // Package-private fields used by ViewGroup AOSP code directly
    int mAction;
    float mX;
    float mY;
    ClipData mClipData;
    ClipDescription mClipDescription;
    Object mLocalState;
    boolean mDragResult;
    boolean mEventHandlerWasCalled;

    DragEvent() {}

    public static DragEvent obtain(DragEvent source) {
        DragEvent ev = new DragEvent();
        ev.mAction = source.mAction;
        ev.mX = source.mX;
        ev.mY = source.mY;
        ev.mClipData = source.mClipData;
        ev.mClipDescription = source.mClipDescription;
        ev.mLocalState = source.mLocalState;
        ev.mDragResult = source.mDragResult;
        return ev;
    }

    public static DragEvent obtain(int action, float x, float y, Object localState,
                                   ClipDescription description, ClipData data,
                                   DragSurface surface, boolean result) {
        DragEvent ev = new DragEvent();
        ev.mAction = action;
        ev.mX = x;
        ev.mY = y;
        ev.mLocalState = localState;
        ev.mClipDescription = description;
        ev.mClipData = data;
        ev.mDragResult = result;
        return ev;
    }

    public void recycle() {}

    public int describeContents() { return 0; }
    public int getAction() { return mAction; }
    public ClipData getClipData() { return mClipData; }
    public ClipDescription getClipDescription() { return mClipDescription; }
    public Object getLocalState() { return mLocalState; }
    public boolean getResult() { return mDragResult; }
    public float getX() { return mX; }
    public float getY() { return mY; }
    public void writeToParcel(Parcel p0, int p1) {}
}
