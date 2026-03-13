package android.view.textclassifier;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class ConversationAction implements Parcelable {
    public static final int TYPE_CALL_PHONE = 0;
    public static final int TYPE_CREATE_REMINDER = 0;
    public static final int TYPE_OPEN_URL = 0;
    public static final int TYPE_SEND_EMAIL = 0;
    public static final int TYPE_SEND_SMS = 0;
    public static final int TYPE_SHARE_LOCATION = 0;
    public static final int TYPE_TEXT_REPLY = 0;
    public static final int TYPE_TRACK_FLIGHT = 0;
    public static final int TYPE_VIEW_CALENDAR = 0;
    public static final int TYPE_VIEW_MAP = 0;

    public ConversationAction() {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
