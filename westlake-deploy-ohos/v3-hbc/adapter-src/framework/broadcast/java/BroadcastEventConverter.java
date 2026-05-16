/*
 * BroadcastEventConverter.java
 *
 * Bidirectional converter between Android broadcast Actions and OH CommonEvent names.
 * Handles Action name mapping + Intent extras <-> Want params conversion.
 *
 * Mapping rules:
 *   - Known system broadcast Actions: hardcoded bidirectional table
 *   - Custom Actions: prefixed with "adapter.custom." for OH event names
 *   - OH-only events with no Android equivalent: passed through as-is
 */
package adapter.broadcast;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BroadcastEventConverter {

    private static final String TAG = "OH_BroadcastConverter";

    // Prefix for custom (non-system) Android broadcast Actions mapped to OH events
    private static final String CUSTOM_EVENT_PREFIX = "adapter.custom.";

    // Android Action -> OH Event Name
    private static final Map<String, String> ANDROID_TO_OH = new HashMap<>();
    // OH Event Name -> Android Action
    private static final Map<String, String> OH_TO_ANDROID = new HashMap<>();

    static {
        // Boot & shutdown
        mapAction("android.intent.action.BOOT_COMPLETED",         "usual.event.BOOT_COMPLETED");
        mapAction("android.intent.action.LOCKED_BOOT_COMPLETED",  "usual.event.LOCKED_BOOT_COMPLETED");
        mapAction("android.intent.action.ACTION_SHUTDOWN",        "usual.event.SHUTDOWN");

        // Battery & power
        mapAction("android.intent.action.BATTERY_CHANGED",        "usual.event.BATTERY_CHANGED");
        mapAction("android.intent.action.BATTERY_LOW",            "usual.event.BATTERY_LOW");
        mapAction("android.intent.action.BATTERY_OKAY",           "usual.event.BATTERY_OKAY");
        mapAction("android.intent.action.ACTION_POWER_CONNECTED", "usual.event.POWER_CONNECTED");
        mapAction("android.intent.action.ACTION_POWER_DISCONNECTED","usual.event.POWER_DISCONNECTED");

        // Screen
        mapAction("android.intent.action.SCREEN_ON",              "usual.event.SCREEN_ON");
        mapAction("android.intent.action.SCREEN_OFF",             "usual.event.SCREEN_OFF");
        mapAction("android.intent.action.USER_PRESENT",           "usual.event.USER_PRESENT");

        // Time
        mapAction("android.intent.action.TIME_TICK",              "usual.event.TIME_TICK");
        mapAction("android.intent.action.TIME_SET",               "usual.event.TIME_CHANGED");
        mapAction("android.intent.action.TIMEZONE_CHANGED",       "usual.event.TIMEZONE_CHANGED");
        mapAction("android.intent.action.DATE_CHANGED",           "usual.event.DATE_CHANGED");

        // Network
        mapAction("android.net.conn.CONNECTIVITY_CHANGE",         "usual.event.CONNECTIVITY_CHANGE");
        mapAction("android.net.wifi.STATE_CHANGE",                "usual.event.WIFI_CONN_STATE");
        mapAction("android.net.wifi.WIFI_STATE_CHANGED",          "usual.event.WIFI_POWER_STATE");

        // Bluetooth
        mapAction("android.bluetooth.adapter.action.STATE_CHANGED",
                  "usual.event.BLUETOOTH_HOST_STATE_UPDATE");

        // Airplane mode
        mapAction("android.intent.action.AIRPLANE_MODE",          "usual.event.AIRPLANE_MODE_CHANGED");

        // Package events
        mapAction("android.intent.action.PACKAGE_ADDED",          "usual.event.PACKAGE_ADDED");
        mapAction("android.intent.action.PACKAGE_REMOVED",        "usual.event.PACKAGE_REMOVED");
        mapAction("android.intent.action.PACKAGE_REPLACED",       "usual.event.PACKAGE_REPLACED");
        mapAction("android.intent.action.PACKAGE_CHANGED",        "usual.event.PACKAGE_CHANGED");
        mapAction("android.intent.action.PACKAGE_RESTARTED",      "usual.event.PACKAGE_RESTARTED");
        mapAction("android.intent.action.PACKAGE_DATA_CLEARED",   "usual.event.PACKAGE_DATA_CLEARED");
        mapAction("android.intent.action.MY_PACKAGE_REPLACED",    "usual.event.MY_PACKAGE_REPLACED");

        // Configuration & locale
        mapAction("android.intent.action.LOCALE_CHANGED",         "usual.event.LOCALE_CHANGED");
        mapAction("android.intent.action.CONFIGURATION_CHANGED",  "usual.event.CONFIGURATION_CHANGED");

        // UI events
        mapAction("android.intent.action.CLOSE_SYSTEM_DIALOGS",   "usual.event.CLOSE_SYSTEM_DIALOGS");

        // User events
        mapAction("android.intent.action.USER_SWITCHED",          "usual.event.USER_SWITCHED");
        mapAction("android.intent.action.USER_ADDED",             "usual.event.USER_ADDED");
        mapAction("android.intent.action.USER_REMOVED",           "usual.event.USER_REMOVED");

        // Storage
        mapAction("android.intent.action.DEVICE_STORAGE_LOW",     "usual.event.DEVICE_STORAGE_LOW");
        mapAction("android.intent.action.DEVICE_STORAGE_OK",      "usual.event.DEVICE_STORAGE_OK");

        // USB
        mapAction("android.hardware.usb.action.USB_STATE",        "usual.event.USB_STATE");
        mapAction("android.hardware.usb.action.USB_DEVICE_ATTACHED","usual.event.USB_DEVICE_ATTACHED");
        mapAction("android.hardware.usb.action.USB_DEVICE_DETACHED","usual.event.USB_DEVICE_DETACHED");

        // NFC
        mapAction("android.nfc.action.ADAPTER_STATE_CHANGED",
                  "usual.event.NFC_ACTION_ADAPTER_STATE_CHANGED");

        // Telephony
        mapAction("android.intent.action.PHONE_STATE",            "usual.event.CALL_STATE_CHANGED");
        mapAction("android.provider.Telephony.SMS_RECEIVED",      "usual.event.SMS_RECEIVE_COMPLETED");
    }

    private static void mapAction(String android, String oh) {
        ANDROID_TO_OH.put(android, oh);
        OH_TO_ANDROID.put(oh, android);
    }

    /**
     * Map an Android broadcast Action to an OH CommonEvent name.
     * Known system actions are mapped directly; custom actions get "adapter.custom." prefix.
     */
    public static String androidActionToOH(String androidAction) {
        if (androidAction == null) return null;
        String mapped = ANDROID_TO_OH.get(androidAction);
        if (mapped != null) return mapped;
        // Custom action: prefix it
        return CUSTOM_EVENT_PREFIX + androidAction;
    }

    /**
     * Map an OH CommonEvent name to an Android broadcast Action.
     * Known system events are mapped back; custom events have prefix stripped.
     */
    public static String ohEventToAndroid(String ohEvent) {
        if (ohEvent == null) return null;
        String mapped = OH_TO_ANDROID.get(ohEvent);
        if (mapped != null) return mapped;
        // Custom event: strip prefix
        if (ohEvent.startsWith(CUSTOM_EVENT_PREFIX)) {
            return ohEvent.substring(CUSTOM_EVENT_PREFIX.length());
        }
        // Unknown OH event: pass through as-is
        return ohEvent;
    }

    /**
     * Convert all Actions in an Android IntentFilter to OH event name array.
     */
    public static String[] filterActionsToOH(IntentFilter filter) {
        int count = filter.countActions();
        String[] ohEvents = new String[count];
        for (int i = 0; i < count; i++) {
            ohEvents[i] = androidActionToOH(filter.getAction(i));
        }
        return ohEvents;
    }

    /**
     * Serialize a Bundle to a JSON string for passing through JNI/IPC.
     */
    public static String bundleToJson(Bundle extras) {
        if (extras == null) return null;
        JSONObject json = new JSONObject();
        try {
            for (String key : extras.keySet()) {
                Object val = extras.get(key);
                if (val != null) {
                    json.put(key, val.toString());
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "bundleToJson error: " + e.getMessage());
        }
        return json.toString();
    }

    /**
     * Deserialize a JSON string back to a Bundle.
     */
    public static Bundle jsonToBundle(String jsonStr) {
        Bundle bundle = new Bundle();
        if (jsonStr == null || jsonStr.isEmpty()) return bundle;
        try {
            JSONObject json = new JSONObject(jsonStr);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                bundle.putString(key, json.getString(key));
            }
        } catch (JSONException e) {
            Log.w(TAG, "jsonToBundle error: " + e.getMessage());
        }
        return bundle;
    }

    /**
     * Build an Android Intent from OH event callback data.
     */
    public static Intent ohEventToIntent(String ohAction, String extrasJson,
            String uri, int code) {
        String androidAction = ohEventToAndroid(ohAction);
        Intent intent = new Intent(androidAction);
        if (extrasJson != null && !extrasJson.isEmpty()) {
            intent.putExtras(jsonToBundle(extrasJson));
        }
        if (uri != null && !uri.isEmpty()) {
            intent.setData(Uri.parse(uri));
        }
        return intent;
    }
}
