package android.net.wifi.aware;
import android.net.TransportInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.TransportInfo;
import android.os.Parcel;
import android.os.Parcelable;

public final class WifiAwareNetworkInfo implements Parcelable, TransportInfo {
    public WifiAwareNetworkInfo() {}

    public int describeContents() { return 0; }
    public int getPort() { return 0; }
    public int getTransportProtocol() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
