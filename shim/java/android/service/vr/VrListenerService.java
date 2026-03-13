package android.service.vr;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class VrListenerService extends Service {
    public static final int SERVICE_INTERFACE = 0;

    public VrListenerService() {}

    public static boolean isVrModePackageEnabled(Context p0, ComponentName p1) { return false; }
    public IBinder onBind(Intent p0) { return null; }
    public void onCurrentVrActivityChanged(ComponentName p0) {}
}
