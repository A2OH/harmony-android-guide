package android.net.sip;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class SipManager {
    public static final int EXTRA_CALL_ID = 0;
    public static final int EXTRA_OFFER_SD = 0;
    public static final int INCOMING_CALL_RESULT_CODE = 0;

    public SipManager() {}

    public void close(String p0) {}
    public SipSession createSipSession(SipProfile p0, Object p1) { return null; }
    public static String getCallId(Intent p0) { return null; }
    public static String getOfferSessionDescription(Intent p0) { return null; }
    public SipSession getSessionFor(Intent p0) { return null; }
    public static boolean isApiSupported(Context p0) { return false; }
    public static boolean isIncomingCallIntent(Intent p0) { return false; }
    public boolean isOpened(String p0) { return false; }
    public boolean isRegistered(String p0) { return false; }
    public static boolean isSipWifiOnly(Context p0) { return false; }
    public static boolean isVoipSupported(Context p0) { return false; }
    public SipAudioCall makeAudioCall(SipProfile p0, SipProfile p1, Object p2, int p3) { return null; }
    public static SipManager newInstance(Context p0) { return null; }
    public void open(SipProfile p0) {}
    public void open(SipProfile p0, PendingIntent p1, SipRegistrationListener p2) {}
    public void register(SipProfile p0, int p1, SipRegistrationListener p2) {}
    public void setRegistrationListener(String p0, SipRegistrationListener p1) {}
    public SipAudioCall takeAudioCall(Intent p0, Object p1) { return null; }
    public void unregister(SipProfile p0, SipRegistrationListener p1) {}
}
