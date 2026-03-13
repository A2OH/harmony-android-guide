package android.service.controls;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.service.controls.actions.ControlAction;
import java.util.function.Consumer;

public class ControlsProviderService extends Service {
    public static final int SERVICE_CONTROLS = 0;

    public ControlsProviderService() {}

    public boolean onUnbind(Intent p0) { return false; }
    public void performControlAction(String p0, ControlAction p1, Object p2) {}
    public static void requestAddControl(Context p0, ComponentName p1, Control p2) {}
}
