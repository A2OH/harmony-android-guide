package android.net.wifi.aware;
import android.os.Handler;

public class WifiAwareManager {
    public static final int ACTION_WIFI_AWARE_STATE_CHANGED = 0;
    public static final int WIFI_AWARE_DATA_PATH_ROLE_INITIATOR = 0;
    public static final int WIFI_AWARE_DATA_PATH_ROLE_RESPONDER = 0;

    public WifiAwareManager() {}

    public void attach(AttachCallback p0, Handler p1) {}
    public void attach(AttachCallback p0, IdentityChangedListener p1, Handler p2) {}
    public Characteristics getCharacteristics() { return null; }
    public boolean isAvailable() { return false; }
}
