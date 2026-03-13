package android.net.wifi.p2p;
import android.os.Parcel;
import android.os.Parcelable;

public final class WifiP2pWfdInfo implements Parcelable {
    public static final int DEVICE_TYPE_PRIMARY_SINK = 0;
    public static final int DEVICE_TYPE_SECONDARY_SINK = 0;
    public static final int DEVICE_TYPE_SOURCE_OR_PRIMARY_SINK = 0;
    public static final int DEVICE_TYPE_WFD_SOURCE = 0;

    public WifiP2pWfdInfo() {}
    public WifiP2pWfdInfo(WifiP2pWfdInfo p0) {}

    public int describeContents() { return 0; }
    public int getControlPort() { return 0; }
    public int getDeviceType() { return 0; }
    public int getMaxThroughput() { return 0; }
    public boolean isContentProtectionSupported() { return false; }
    public boolean isEnabled() { return false; }
    public boolean isSessionAvailable() { return false; }
    public void setContentProtectionSupported(boolean p0) {}
    public void setControlPort(int p0) {}
    public boolean setDeviceType(int p0) { return false; }
    public void setEnabled(boolean p0) {}
    public void setMaxThroughput(int p0) {}
    public void setSessionAvailable(boolean p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
