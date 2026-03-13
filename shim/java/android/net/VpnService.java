package android.net;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class VpnService extends Service {
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA_SUPPORTS_ALWAYS_ON = 0;

    public VpnService() {}

    public boolean isAlwaysOn() { return false; }
    public boolean isLockdownEnabled() { return false; }
    public IBinder onBind(Intent p0) { return null; }
    public void onRevoke() {}
    public static Intent prepare(Context p0) { return null; }
    public boolean protect(int p0) { return false; }
    public boolean setUnderlyingNetworks(Network[] p0) { return false; }
}
