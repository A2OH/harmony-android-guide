package android.net.wifi.p2p.nsd;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiP2pServiceInfo implements Parcelable {
    public static final int SERVICE_TYPE_ALL = 0;
    public static final int SERVICE_TYPE_BONJOUR = 0;
    public static final int SERVICE_TYPE_UPNP = 0;
    public static final int SERVICE_TYPE_VENDOR_SPECIFIC = 0;

    public WifiP2pServiceInfo() {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
