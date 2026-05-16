/*
 * PermissionMapper.java
 *
 * Bidirectional permission name mapping between Android and OpenHarmony.
 * Java-side counterpart of apk-installer/src/permission_mapper.h.
 *
 * Used by PackageInfoBuilder to convert OH permission names back to
 * Android format when responding to PackageManager queries.
 */
package adapter.packagemanager;

import java.util.HashMap;
import java.util.Map;

public class PermissionMapper {

    private static final Map<String, String> sAndroidToOH = new HashMap<>();
    private static final Map<String, String> sOHToAndroid = new HashMap<>();

    private static final String ANDROID_PREFIX = "android.permission.";
    private static final String OH_PREFIX = "ohos.permission.";
    private static final String OH_ANDROID_PREFIX = "ohos.permission.android.";

    static {
        // Network
        addMapping("android.permission.INTERNET",                 "ohos.permission.INTERNET");
        addMapping("android.permission.ACCESS_NETWORK_STATE",     "ohos.permission.GET_NETWORK_INFO");
        addMapping("android.permission.ACCESS_WIFI_STATE",        "ohos.permission.GET_WIFI_INFO");
        addMapping("android.permission.CHANGE_WIFI_STATE",        "ohos.permission.SET_WIFI_INFO");
        addMapping("android.permission.CHANGE_NETWORK_STATE",     "ohos.permission.SET_NETWORK_INFO");

        // Camera & Media
        addMapping("android.permission.CAMERA",                   "ohos.permission.CAMERA");
        addMapping("android.permission.RECORD_AUDIO",             "ohos.permission.MICROPHONE");

        // Storage
        addMapping("android.permission.READ_EXTERNAL_STORAGE",    "ohos.permission.READ_MEDIA");
        addMapping("android.permission.WRITE_EXTERNAL_STORAGE",   "ohos.permission.WRITE_MEDIA");
        addMapping("android.permission.MANAGE_EXTERNAL_STORAGE",  "ohos.permission.FILE_ACCESS_MANAGER");

        // Location
        addMapping("android.permission.ACCESS_FINE_LOCATION",     "ohos.permission.APPROXIMATELY_LOCATION");
        addMapping("android.permission.ACCESS_COARSE_LOCATION",   "ohos.permission.APPROXIMATELY_LOCATION");
        addMapping("android.permission.ACCESS_BACKGROUND_LOCATION", "ohos.permission.LOCATION_IN_BACKGROUND");

        // Contacts
        addMapping("android.permission.READ_CONTACTS",            "ohos.permission.READ_CONTACTS");
        addMapping("android.permission.WRITE_CONTACTS",           "ohos.permission.WRITE_CONTACTS");

        // Phone
        addMapping("android.permission.READ_PHONE_STATE",         "ohos.permission.GET_TELEPHONY_STATE");
        addMapping("android.permission.CALL_PHONE",               "ohos.permission.PLACE_CALL");
        addMapping("android.permission.READ_CALL_LOG",            "ohos.permission.READ_CALL_LOG");
        addMapping("android.permission.WRITE_CALL_LOG",           "ohos.permission.WRITE_CALL_LOG");
        addMapping("android.permission.READ_PHONE_NUMBERS",       "ohos.permission.GET_TELEPHONY_STATE");

        // SMS
        addMapping("android.permission.SEND_SMS",                 "ohos.permission.SEND_MESSAGES");
        addMapping("android.permission.READ_SMS",                 "ohos.permission.READ_MESSAGES");
        addMapping("android.permission.RECEIVE_SMS",              "ohos.permission.RECEIVE_SMS");

        // Calendar
        addMapping("android.permission.READ_CALENDAR",            "ohos.permission.READ_CALENDAR");
        addMapping("android.permission.WRITE_CALENDAR",           "ohos.permission.WRITE_CALENDAR");

        // Sensors
        addMapping("android.permission.BODY_SENSORS",             "ohos.permission.READ_HEALTH_DATA");

        // Bluetooth
        addMapping("android.permission.BLUETOOTH",                "ohos.permission.USE_BLUETOOTH");
        addMapping("android.permission.BLUETOOTH_ADMIN",          "ohos.permission.MANAGE_BLUETOOTH");
        addMapping("android.permission.BLUETOOTH_CONNECT",        "ohos.permission.USE_BLUETOOTH");
        addMapping("android.permission.BLUETOOTH_SCAN",           "ohos.permission.USE_BLUETOOTH");

        // System
        addMapping("android.permission.VIBRATE",                  "ohos.permission.VIBRATE");
        addMapping("android.permission.WAKE_LOCK",                "ohos.permission.RUNNING_LOCK");
        addMapping("android.permission.RECEIVE_BOOT_COMPLETED",   "ohos.permission.RUNNING_LOCK");
        addMapping("android.permission.SET_ALARM",                "ohos.permission.PUBLISH_AGENT_REMINDER");
        addMapping("android.permission.REQUEST_INSTALL_PACKAGES", "ohos.permission.INSTALL_BUNDLE");
        addMapping("android.permission.FOREGROUND_SERVICE",       "ohos.permission.KEEP_BACKGROUND_RUNNING");

        // Notifications
        addMapping("android.permission.POST_NOTIFICATIONS",       "ohos.permission.NOTIFICATION_CONTROLLER");

        // NFC
        addMapping("android.permission.NFC",                      "ohos.permission.NFC_TAG");
    }

    private static void addMapping(String android, String oh) {
        sAndroidToOH.put(android, oh);
        // Only add reverse if not already present (handles many-to-one)
        if (!sOHToAndroid.containsKey(oh)) {
            sOHToAndroid.put(oh, android);
        }
    }

    /**
     * Map Android permission name to OH permission name.
     */
    public static String mapToOH(String androidPermission) {
        if (androidPermission == null) return null;
        String mapped = sAndroidToOH.get(androidPermission);
        if (mapped != null) return mapped;
        // Unknown: strip android.permission. prefix and add ohos.permission.android.
        if (androidPermission.startsWith(ANDROID_PREFIX)) {
            return OH_ANDROID_PREFIX + androidPermission.substring(ANDROID_PREFIX.length());
        }
        return OH_ANDROID_PREFIX + androidPermission;
    }

    /**
     * Map OH permission name to Android permission name.
     */
    public static String mapToAndroid(String ohPermission) {
        if (ohPermission == null) return null;
        String mapped = sOHToAndroid.get(ohPermission);
        if (mapped != null) return mapped;
        // Reverse passthrough: strip ohos.permission.android. prefix
        if (ohPermission.startsWith(OH_ANDROID_PREFIX)) {
            return ANDROID_PREFIX + ohPermission.substring(OH_ANDROID_PREFIX.length());
        }
        return ohPermission;
    }
}
