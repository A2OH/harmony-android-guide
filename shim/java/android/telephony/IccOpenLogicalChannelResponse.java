package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class IccOpenLogicalChannelResponse implements Parcelable {
    public static final int INVALID_CHANNEL = 0;
    public static final int STATUS_MISSING_RESOURCE = 0;
    public static final int STATUS_NO_ERROR = 0;
    public static final int STATUS_NO_SUCH_ELEMENT = 0;
    public static final int STATUS_UNKNOWN_ERROR = 0;

    public IccOpenLogicalChannelResponse() {}

    public int describeContents() { return 0; }
    public int getChannel() { return 0; }
    public byte[] getSelectResponse() { return new byte[0]; }
    public int getStatus() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
