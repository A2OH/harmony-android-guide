package android.view;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Parcel;
import android.os.Parcelable;

public class DragEvent implements Parcelable {
    public static final int ACTION_DRAG_ENDED = 0;
    public static final int ACTION_DRAG_ENTERED = 0;
    public static final int ACTION_DRAG_EXITED = 0;
    public static final int ACTION_DRAG_LOCATION = 0;
    public static final int ACTION_DRAG_STARTED = 0;
    public static final int ACTION_DROP = 0;

    public DragEvent() {}

    public int describeContents() { return 0; }
    public int getAction() { return 0; }
    public ClipData getClipData() { return null; }
    public ClipDescription getClipDescription() { return null; }
    public Object getLocalState() { return null; }
    public boolean getResult() { return false; }
    public float getX() { return 0f; }
    public float getY() { return 0f; }
    public void writeToParcel(Parcel p0, int p1) {}
}
