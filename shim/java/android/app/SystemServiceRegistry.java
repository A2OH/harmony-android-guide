package android.app;

import java.util.HashMap;

/**
 * SystemServiceRegistry — maps service name strings to singleton service instances.
 *
 * In AOSP this is a complex class with lazy initialization and service fetchers.
 * In MiniServer, we pre-create singletons for the services we support.
 */
public class SystemServiceRegistry {
    private static final HashMap<String, Object> sServices = new HashMap<>();
    private static boolean sInitialized = false;

    /** Initialize all services. Called once by MiniServer. */
    public static void init() {
        if (sInitialized) return;
        sInitialized = true;

        // Services backed by OHBridge JNI (already wired)
        sServices.put("notification", new android.app.NotificationManager());
        sServices.put("alarm", new android.app.AlarmManager());
        sServices.put("activity", new android.app.ActivityManager());
        sServices.put("download", new android.app.DownloadManager());
        sServices.put("keyguard", new android.app.KeyguardManager());
        sServices.put("search", new android.app.SearchManager());
        sServices.put("power", new android.os.PowerManager());
        sServices.put("vibrator", new android.os.Vibrator());
        sServices.put("battery", new android.os.BatteryManager());

        // Media
        sServices.put("audio", new android.media.AudioManager());

        // Connectivity
        sServices.put("connectivity", new android.net.ConnectivityManager());
        sServices.put("wifi", new android.net.wifi.WifiManager());

        // Location
        sServices.put("location", new android.location.LocationManager());

        // Telephony
        sServices.put("phone", new android.telephony.TelephonyManager());

        // Sensors
        sServices.put("sensor", new android.hardware.SensorManager());

        // Layout inflater — special: returns a new instance per Context
        // (handled in getSystemService directly)
    }

    /** Get a system service by name. Returns null if unknown. */
    public static Object getService(String name) {
        if (!sInitialized) init();
        if (name == null) return null;
        return sServices.get(name);
    }

    /** Register a custom service (for testing or extension). */
    public static void registerService(String name, Object service) {
        if (!sInitialized) init();
        sServices.put(name, service);
    }
}
