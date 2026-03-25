package android.net;

import java.lang.reflect.Method;

/**
 * Bridges ConnectivityManager to the phone's real implementation.
 * On phone: delegates to framework's ConnectivityManager via reflection.
 * On OHOS/headless: returns stubs indicating connectivity available.
 */
public class NetworkBridge {

    private static boolean phoneDetected;

    static {
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object instance = host.getField("instance").get(null);
            phoneDetected = (instance != null);
        } catch (Exception e) {
            phoneDetected = false;
        }
    }

    public static boolean isOnPhone() { return phoneDetected; }

    /**
     * Get the real ConnectivityManager from the phone's Context.
     */
    public static Object getRealConnectivityManager() {
        if (!phoneDetected) return null;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object instance = host.getField("instance").get(null);
            // Call getSystemService("connectivity")
            Method gsm = instance.getClass().getMethod("getSystemService", String.class);
            return gsm.invoke(instance, "connectivity");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if network is available via the real ConnectivityManager.
     */
    public static boolean isNetworkAvailable() {
        Object cm = getRealConnectivityManager();
        if (cm == null) return true; // assume available in headless mode
        try {
            Method m = cm.getClass().getMethod("getActiveNetworkInfo");
            Object ni = m.invoke(cm);
            if (ni == null) return false;
            Method isConnected = ni.getClass().getMethod("isConnected");
            return (Boolean) isConnected.invoke(ni);
        } catch (Exception e) {
            return true; // assume available if reflection fails
        }
    }
}
