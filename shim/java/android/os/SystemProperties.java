package android.os;

/**
 * Android-compatible SystemProperties shim. This is a hidden/internal API
 * but widely used by apps via reflection. All getters return defaults;
 * set() is a no-op.
 */
public class SystemProperties {

    private SystemProperties() {}

    public static String get(String key) {
        return "";
    }

    public static String get(String key, String def) {
        return def;
    }

    public static int getInt(String key, int def) {
        return def;
    }

    public static long getLong(String key, long def) {
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        return def;
    }

    public static void set(String key, String val) {
        // no-op in shim layer
    }
}
