package android.net.sip;

public final class SipSession {
    public SipSession() {}

    public void answerCall(String p0, int p1) {}
    public void changeCall(String p0, int p1) {}
    public void endCall() {}
    public String getCallId() { return null; }
    public String getLocalIp() { return null; }
    public SipProfile getLocalProfile() { return null; }
    public SipProfile getPeerProfile() { return null; }
    public int getState() { return 0; }
    public boolean isInCall() { return false; }
    public void makeCall(SipProfile p0, String p1, int p2) {}
    public void register(int p0) {}
    public void setListener(Object p0) {}
    public void unregister() {}
}
