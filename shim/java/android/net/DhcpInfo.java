package android.net;

public class DhcpInfo {
    public DhcpInfo() {}

    public int dns1 = 0;
    public int dns2 = 0;
    public int gateway = 0;
    public int ipAddress = 0;
    public int leaseDuration = 0;
    public int netmask = 0;
    public int serverAddress = 0;
    public int describeContents() { return 0; }
    public void writeToParcel(Object p0, Object p1) {}
}
