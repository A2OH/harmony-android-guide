package android.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Android-compatible SettingInjectorService shim.
 * Abstract stub — subclasses implement onGetSummary() and onGetEnabled().
 */
public abstract class SettingInjectorService extends Service {

    /** Broadcast action sent when an injected setting value changes. */
    public static final String ACTION_INJECTED_SETTING_CHANGED =
            "android.location.SettingInjectorService.ACTION_INJECTED_SETTING_CHANGED";

    public SettingInjectorService() {}

    /**
     * Return a human-readable summary string for the injected setting.
     * Called on a worker thread; must not block the main thread.
     */
    protected abstract String onGetSummary();

    /**
     * Return whether the injected setting is currently enabled.
     * Called on a worker thread; must not block the main thread.
     */
    protected abstract boolean onGetEnabled();

    @Override
    public IBinder onBind(Intent intent) {
        return null; // not a bound service
    }
}
