package android.content;

import com.ohos.shim.bridge.OHBridge;

import java.util.HashMap;
import java.util.Map;

/**
 * Shim: android.content.Context → OH common.Context / UIAbilityContext
 * Tier 2 — composite mapping.
 *
 * Context is the god-object of Android. In OH, its responsibilities are split across:
 * - UIAbilityContext (lifecycle, navigation)
 * - @ohos.data.preferences (SharedPreferences)
 * - @ohos.app.ability.common (system services)
 * - Direct module imports (no getSystemService pattern)
 *
 * This shim provides the most commonly used Context methods.
 */
public abstract class Context {
    public static final int MODE_PRIVATE = 0;
    public static final int MODE_WORLD_READABLE = 1;  // deprecated
    public static final int MODE_WORLD_WRITEABLE = 2;  // deprecated
    public static final int MODE_APPEND = 0x8000;

    // Service name constants (for getSystemService)
    public static final String ALARM_SERVICE = "alarm";
    public static final String NOTIFICATION_SERVICE = "notification";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String LOCATION_SERVICE = "location";
    public static final String SENSOR_SERVICE = "sensor";
    public static final String VIBRATOR_SERVICE = "vibrator";
    public static final String POWER_SERVICE = "power";
    public static final String TELEPHONY_SERVICE = "phone";
    public static final String WINDOW_SERVICE = "window";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final String AUDIO_SERVICE = "audio";
    public static final String CLIPBOARD_SERVICE = "clipboard";

    private String packageName = "com.example.app";  // set by runtime
    private final Map<String, SharedPreferences> prefsCache = new HashMap<>();

    // ── SharedPreferences ──

    public SharedPreferences getSharedPreferences(String name, int mode) {
        // mode is ignored — OH Preferences is always private
        SharedPreferences prefs = prefsCache.get(name);
        if (prefs == null) {
            prefs = new SharedPreferences(name);
            prefsCache.put(name, prefs);
        }
        return prefs;
    }

    // ── System Service ──
    // OH doesn't use getSystemService. Each shim service class is returned here,
    // and internally uses direct OH module imports via the bridge.

    private static final Map<String, Object> serviceCache = new HashMap<>();

    public Object getSystemService(String name) {
        Object service = serviceCache.get(name);
        if (service != null) return service;

        switch (name) {
            case NOTIFICATION_SERVICE:
                service = new android.app.NotificationManager();
                break;
            case ALARM_SERVICE:
                service = new android.app.AlarmManager();
                break;
            case CONNECTIVITY_SERVICE:
                service = new android.net.ConnectivityManager();
                break;
            case CLIPBOARD_SERVICE:
                service = new ClipboardManager();
                break;
            // TODO: add more service shims as they are built
            default:
                android.util.Log.w("Context", "getSystemService not shimmed: " + name);
                return null;
        }

        serviceCache.put(name, service);
        return service;
    }

    // ── Navigation ──

    public void startActivity(Intent intent) {
        String abilityName = intent.getTargetAbilityName();
        if (abilityName == null) {
            android.util.Log.e("Context", "startActivity: no target component in Intent");
            return;
        }
        OHBridge.startAbility(
            intent.getPackage() != null ? intent.getPackage() : packageName,
            abilityName,
            intent.getExtrasJson()
        );
    }

    public void startService(Intent intent) {
        // TODO: map to ServiceExtensionAbility start
        android.util.Log.w("Context", "startService: not yet implemented");
    }

    public void sendBroadcast(Intent intent) {
        // TODO: map to commonEventManager.publish
        android.util.Log.w("Context", "sendBroadcast: not yet implemented");
    }

    // ── Package info ──

    public String getPackageName() { return packageName; }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    // ── Resources (stub) ──

    public String getString(int resId) {
        // TODO: map to $r('app.string.xxx') via bridge resource lookup
        return "string:" + resId;
    }

    // ── Content Resolver ──

    private ContentResolver mContentResolver;

    public ContentResolver getContentResolver() {
        if (mContentResolver == null) {
            mContentResolver = new ContentResolver();
        }
        return mContentResolver;
    }

    // ── Application context chain ──

    public Context getApplicationContext() {
        return this;
    }
}
