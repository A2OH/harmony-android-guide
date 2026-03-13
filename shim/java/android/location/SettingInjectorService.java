package android.location;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class SettingInjectorService extends Service {
    public static final int ACTION_INJECTED_SETTING_CHANGED = 0;
    public static final int ACTION_SERVICE_INTENT = 0;
    public static final int ATTRIBUTES_NAME = 0;
    public static final int META_DATA_NAME = 0;

    public SettingInjectorService(String p0) {}

    public IBinder onBind(Intent p0) { return null; }
    public boolean onGetEnabled() { return false; }
    public String onGetSummary() { return null; }
    public void onStart(Intent p0, int p1) {}
    public int onStartCommand(Intent p0, int p1, int p2) { return 0; }
    public static void refreshSettings(Context p0) {}
}
