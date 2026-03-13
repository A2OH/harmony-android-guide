package android.nfc.cardemulation;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class HostApduService extends Service {
    public static final int DEACTIVATION_DESELECTED = 0;
    public static final int DEACTIVATION_LINK_LOSS = 0;
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public HostApduService() {}

    public void notifyUnhandled() {}
    public IBinder onBind(Intent p0) { return null; }
    public void onDeactivated(int p0) {}
    public byte[] processCommandApdu(byte[] p0, Bundle p1) { return new byte[0]; }
    public void sendResponseApdu(byte[] p0) {}
}
