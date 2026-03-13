package android.net.nsd;
import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;
import java.util.Map;

public final class NsdServiceInfo implements Parcelable {
    public NsdServiceInfo() {}

    public int describeContents() { return 0; }
    public Map<?,?> getAttributes() { return null; }
    public InetAddress getHost() { return null; }
    public int getPort() { return 0; }
    public String getServiceName() { return null; }
    public String getServiceType() { return null; }
    public void removeAttribute(String p0) {}
    public void setAttribute(String p0, String p1) {}
    public void setHost(InetAddress p0) {}
    public void setPort(int p0) {}
    public void setServiceName(String p0) {}
    public void setServiceType(String p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
