package android.telecom;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;

public class CallRedirectionService extends Service {
    public static final int SERVICE_INTERFACE = 0;

    public CallRedirectionService() {}

    public void cancelCall() {}
    public void onPlaceCall(Uri p0, PhoneAccountHandle p1, boolean p2) {}
    public boolean onUnbind(Intent p0) { return false; }
    public void placeCallUnmodified() {}
    public void redirectCall(Uri p0, PhoneAccountHandle p1, boolean p2) {}
}
