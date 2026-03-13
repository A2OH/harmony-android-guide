package android.telecom;

public final class ConnectionRequest {
    public ConnectionRequest() {}

    public int describeContents() { return 0; }
    public Object getAccountHandle() { return null; }
    public Object getAddress() { return null; }
    public Object getExtras() { return null; }
    public Object getRttTextStream() { return null; }
    public int getVideoState() { return 0; }
    public boolean isAdhocConferenceCall() { return false; }
    public boolean isRequestingRtt() { return false; }
    public void writeToParcel(Object p0, Object p1) {}
}
