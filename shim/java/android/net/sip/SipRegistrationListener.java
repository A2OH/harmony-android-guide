package android.net.sip;

public interface SipRegistrationListener {
    void onRegistering(String p0);
    void onRegistrationDone(String p0, long p1);
    void onRegistrationFailed(String p0, int p1, String p2);
}
