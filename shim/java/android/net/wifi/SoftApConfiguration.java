package android.net.wifi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class SoftApConfiguration implements Parcelable {
    public static final int SECURITY_TYPE_OPEN = 0;
    public static final int SECURITY_TYPE_WPA2_PSK = 0;
    public static final int SECURITY_TYPE_WPA3_SAE = 0;
    public static final int SECURITY_TYPE_WPA3_SAE_TRANSITION = 0;

    public SoftApConfiguration() {}

    public int describeContents() { return 0; }
    public int getSecurityType() { return 0; }
    public boolean isHiddenSsid() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
