package android.view.textclassifier;
import android.os.Parcel;
import android.os.Parcelable;

public final class SelectionEvent implements Parcelable {
    public static final int ACTION_ABANDON = 0;
    public static final int ACTION_COPY = 0;
    public static final int ACTION_CUT = 0;
    public static final int ACTION_DRAG = 0;
    public static final int ACTION_OTHER = 0;
    public static final int ACTION_OVERTYPE = 0;
    public static final int ACTION_PASTE = 0;
    public static final int ACTION_RESET = 0;
    public static final int ACTION_SELECT_ALL = 0;
    public static final int ACTION_SHARE = 0;
    public static final int ACTION_SMART_SHARE = 0;
    public static final int EVENT_AUTO_SELECTION = 0;
    public static final int EVENT_SELECTION_MODIFIED = 0;
    public static final int EVENT_SELECTION_STARTED = 0;
    public static final int EVENT_SMART_SELECTION_MULTI = 0;
    public static final int EVENT_SMART_SELECTION_SINGLE = 0;
    public static final int INVOCATION_LINK = 0;
    public static final int INVOCATION_MANUAL = 0;
    public static final int INVOCATION_UNKNOWN = 0;

    public SelectionEvent() {}

    public int describeContents() { return 0; }
    public long getDurationSincePreviousEvent() { return 0L; }
    public long getDurationSinceSessionStart() { return 0L; }
    public int getEnd() { return 0; }
    public int getEventIndex() { return 0; }
    public long getEventTime() { return 0L; }
    public int getEventType() { return 0; }
    public int getInvocationMethod() { return 0; }
    public int getSmartEnd() { return 0; }
    public int getSmartStart() { return 0; }
    public int getStart() { return 0; }
    public static boolean isTerminal(int p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
