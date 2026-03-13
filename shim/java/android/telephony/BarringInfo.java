package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class BarringInfo implements Parcelable {
    public static final int BARRING_SERVICE_TYPE_CS_FALLBACK = 0;
    public static final int BARRING_SERVICE_TYPE_CS_SERVICE = 0;
    public static final int BARRING_SERVICE_TYPE_CS_VOICE = 0;
    public static final int BARRING_SERVICE_TYPE_EMERGENCY = 0;
    public static final int BARRING_SERVICE_TYPE_MMTEL_VIDEO = 0;
    public static final int BARRING_SERVICE_TYPE_MMTEL_VOICE = 0;
    public static final int BARRING_SERVICE_TYPE_MO_DATA = 0;
    public static final int BARRING_SERVICE_TYPE_MO_SIGNALLING = 0;
    public static final int BARRING_SERVICE_TYPE_PS_SERVICE = 0;
    public static final int BARRING_SERVICE_TYPE_SMS = 0;

    public BarringInfo() {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
