package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class NetworkRegistrationInfo implements Parcelable {
    public static final int DOMAIN_CS = 0;
    public static final int DOMAIN_CS_PS = 0;
    public static final int DOMAIN_PS = 0;
    public static final int DOMAIN_UNKNOWN = 0;
    public static final int NR_STATE_CONNECTED = 0;
    public static final int NR_STATE_NONE = 0;
    public static final int NR_STATE_NOT_RESTRICTED = 0;
    public static final int NR_STATE_RESTRICTED = 0;
    public static final int SERVICE_TYPE_DATA = 0;
    public static final int SERVICE_TYPE_EMERGENCY = 0;
    public static final int SERVICE_TYPE_SMS = 0;
    public static final int SERVICE_TYPE_UNKNOWN = 0;
    public static final int SERVICE_TYPE_VIDEO = 0;
    public static final int SERVICE_TYPE_VOICE = 0;

    public NetworkRegistrationInfo() {}

    public int describeContents() { return 0; }
    public int getAccessNetworkTechnology() { return 0; }
    public int getDomain() { return 0; }
    public int getTransportType() { return 0; }
    public boolean isRegistered() { return false; }
    public boolean isRoaming() { return false; }
    public boolean isSearching() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
