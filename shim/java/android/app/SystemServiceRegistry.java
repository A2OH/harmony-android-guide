package android.app;

/**
 * SystemServiceRegistry — maps service name strings to service singletons.
 *
 * Keep this bootstrap path free of HashMap usage. Early core-library map
 * initialization is still unstable on the pure Westlake ART path, and eager
 * service registration was crashing before app bootstrap could begin.
 */
public class SystemServiceRegistry {
    private static boolean sInitialized = false;

    private static Object sNotificationService;
    private static Object sAlarmService;
    private static Object sActivityService;
    private static Object sDownloadService;
    private static Object sKeyguardService;
    private static Object sSearchService;
    private static Object sPowerService;
    private static Object sVibratorService;
    private static Object sBatteryService;
    private static Object sAccessibilityService;
    private static Object sAudioService;
    private static Object sConnectivityService;
    private static Object sWifiService;
    private static Object sLocationService;
    private static Object sPhoneService;
    private static Object sSensorService;

    private static String[] sCustomNames = new String[4];
    private static Object[] sCustomServices = new Object[4];
    private static int sCustomCount = 0;

    /** Initialize service registry bookkeeping. */
    public static void init() {
        if (sInitialized) return;
        sInitialized = true;
    }

    /** Get a system service by name. Returns null if unknown. */
    public static Object getService(String name) {
        if (!sInitialized) init();
        if (name == null) return null;

        Object custom = getCustomService(name);
        if (custom != null) return custom;

        if ("notification".equals(name)) {
            if (sNotificationService == null) sNotificationService = new android.app.NotificationManager();
            return sNotificationService;
        }
        if ("alarm".equals(name)) {
            if (sAlarmService == null) sAlarmService = new android.app.AlarmManager();
            return sAlarmService;
        }
        if ("activity".equals(name)) {
            if (sActivityService == null) sActivityService = new android.app.ActivityManager();
            return sActivityService;
        }
        if ("download".equals(name)) {
            if (sDownloadService == null) sDownloadService = new android.app.DownloadManager();
            return sDownloadService;
        }
        if ("keyguard".equals(name)) {
            if (sKeyguardService == null) sKeyguardService = new android.app.KeyguardManager();
            return sKeyguardService;
        }
        if ("search".equals(name)) {
            if (sSearchService == null) sSearchService = new android.app.SearchManager();
            return sSearchService;
        }
        if ("power".equals(name)) {
            if (sPowerService == null) sPowerService = new android.os.PowerManager();
            return sPowerService;
        }
        if ("vibrator".equals(name)) {
            if (sVibratorService == null) sVibratorService = new android.os.Vibrator();
            return sVibratorService;
        }
        if ("battery".equals(name) || "batterymanager".equals(name)) {
            if (sBatteryService == null) sBatteryService = new android.os.BatteryManager();
            return sBatteryService;
        }
        if ("accessibility".equals(name)) {
            if (sAccessibilityService == null) {
                sAccessibilityService = new android.view.accessibility.AccessibilityManager();
            }
            return sAccessibilityService;
        }
        if ("audio".equals(name)) {
            if (sAudioService == null) sAudioService = new android.media.AudioManager();
            return sAudioService;
        }
        if ("connectivity".equals(name)) {
            if (sConnectivityService == null) sConnectivityService = new android.net.ConnectivityManager();
            return sConnectivityService;
        }
        if ("wifi".equals(name)) {
            if (sWifiService == null) sWifiService = new android.net.wifi.WifiManager();
            return sWifiService;
        }
        if ("location".equals(name)) {
            if (sLocationService == null) sLocationService = new android.location.LocationManager();
            return sLocationService;
        }
        if ("phone".equals(name)) {
            if (sPhoneService == null) sPhoneService = new android.telephony.TelephonyManager();
            return sPhoneService;
        }
        if ("sensor".equals(name)) {
            if (sSensorService == null) sSensorService = new android.hardware.SensorManager();
            return sSensorService;
        }

        return null;
    }

    /** Register a custom service (for testing or extension). */
    public static void registerService(String name, Object service) {
        if (!sInitialized) init();
        if (name == null) return;

        for (int i = 0; i < sCustomCount; i++) {
            if (name.equals(sCustomNames[i])) {
                sCustomServices[i] = service;
                return;
            }
        }

        if (sCustomCount == sCustomNames.length) {
            int newSize = sCustomCount * 2;
            String[] newNames = new String[newSize];
            Object[] newServices = new Object[newSize];
            for (int i = 0; i < sCustomCount; i++) {
                newNames[i] = sCustomNames[i];
                newServices[i] = sCustomServices[i];
            }
            sCustomNames = newNames;
            sCustomServices = newServices;
        }

        sCustomNames[sCustomCount] = name;
        sCustomServices[sCustomCount] = service;
        sCustomCount++;
    }

    private static Object getCustomService(String name) {
        for (int i = 0; i < sCustomCount; i++) {
            if (name.equals(sCustomNames[i])) {
                return sCustomServices[i];
            }
        }
        return null;
    }
}
