package android.view.textclassifier;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class TextClassifierEvent implements Parcelable {
    public static final int CATEGORY_CONVERSATION_ACTIONS = 0;
    public static final int CATEGORY_LANGUAGE_DETECTION = 0;
    public static final int CATEGORY_LINKIFY = 0;
    public static final int CATEGORY_SELECTION = 0;
    public static final int TYPE_ACTIONS_GENERATED = 0;
    public static final int TYPE_ACTIONS_SHOWN = 0;
    public static final int TYPE_AUTO_SELECTION = 0;
    public static final int TYPE_COPY_ACTION = 0;
    public static final int TYPE_CUT_ACTION = 0;
    public static final int TYPE_LINKS_GENERATED = 0;
    public static final int TYPE_LINK_CLICKED = 0;
    public static final int TYPE_MANUAL_REPLY = 0;
    public static final int TYPE_OTHER_ACTION = 0;
    public static final int TYPE_OVERTYPE = 0;
    public static final int TYPE_PASTE_ACTION = 0;
    public static final int TYPE_SELECTION_DESTROYED = 0;
    public static final int TYPE_SELECTION_DRAG = 0;
    public static final int TYPE_SELECTION_MODIFIED = 0;
    public static final int TYPE_SELECTION_RESET = 0;
    public static final int TYPE_SELECTION_STARTED = 0;
    public static final int TYPE_SELECT_ALL = 0;
    public static final int TYPE_SHARE_ACTION = 0;
    public static final int TYPE_SMART_ACTION = 0;
    public static final int TYPE_SMART_SELECTION_MULTI = 0;
    public static final int TYPE_SMART_SELECTION_SINGLE = 0;

    public TextClassifierEvent() {}

    public int describeContents() { return 0; }
    public int getEventCategory() { return 0; }
    public int getEventIndex() { return 0; }
    public int getEventType() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
