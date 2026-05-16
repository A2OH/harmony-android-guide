/*
 * ContentProviderUriConverter.java
 *
 * Converts Android content:// URIs to OpenHarmony datashare:// URIs and vice versa.
 *
 * Android URI format:  content://authority/path
 * OH URI format:       datashare:///bundleName/path
 *
 * Maintains a bidirectional mapping table for known system providers
 * (contacts, media, calendar, settings, etc.).
 * For unknown authorities (e.g., app-defined providers), the authority
 * is passed through as bundleName directly.
 */
package adapter.contentprovider;

import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ContentProviderUriConverter {

    private static final String TAG = "OH_CPUriConverter";

    private static final String ANDROID_SCHEME = "content";
    private static final String OH_SCHEME = "datashare";

    // ========================================================================
    // Authority <-> BundleName mapping for system providers
    // ========================================================================

    private static final Map<String, String> AUTHORITY_TO_BUNDLE = new HashMap<>();
    private static final Map<String, String> BUNDLE_TO_AUTHORITY = new HashMap<>();

    static {
        // Contacts
        addMapping("com.android.contacts", "com.ohos.contacts");
        addMapping("contacts", "com.ohos.contacts");
        addMapping("call_log", "com.ohos.contacts");

        // Media
        addMapping("media", "com.ohos.medialibrary.medialibrarydata");

        // Calendar
        addMapping("com.android.calendar", "com.ohos.calendardata");

        // Settings
        addMapping("settings", "com.ohos.settingsdata");

        // Telephony / SMS
        addMapping("sms", "com.ohos.mms");
        addMapping("mms", "com.ohos.mms");
        addMapping("mms-sms", "com.ohos.mms");

        // Downloads
        addMapping("downloads", "com.ohos.download");

        // User Dictionary
        addMapping("user_dictionary", "com.ohos.inputmethod");
    }

    private static void addMapping(String authority, String bundleName) {
        AUTHORITY_TO_BUNDLE.put(authority, bundleName);
        // Only set reverse mapping if not already set (many-to-one is possible)
        if (!BUNDLE_TO_AUTHORITY.containsKey(bundleName)) {
            BUNDLE_TO_AUTHORITY.put(bundleName, authority);
        }
    }

    // ========================================================================
    // Public API
    // ========================================================================

    /**
     * Convert Android content:// URI to OH datashare:// URI.
     *
     * content://com.android.contacts/contacts/1
     *   -> datashare:///com.ohos.contacts/contacts/1
     *
     * For unknown authorities, passes through directly:
     * content://com.example.app/data
     *   -> datashare:///com.example.app/data
     */
    public static String toOhUri(Uri androidUri) {
        if (androidUri == null) return null;

        String authority = androidUri.getAuthority();
        if (authority == null) return androidUri.toString();

        String bundleName = AUTHORITY_TO_BUNDLE.getOrDefault(authority, authority);
        String path = androidUri.getPath();
        if (path == null) path = "";

        String ohUri = OH_SCHEME + ":///" + bundleName + path;

        // Preserve query parameters if present
        String query = androidUri.getQuery();
        if (query != null && !query.isEmpty()) {
            ohUri += "?" + query;
        }

        Log.d(TAG, "toOhUri: " + androidUri + " -> " + ohUri);
        return ohUri;
    }

    /**
     * Convert Android content:// URI string to OH datashare:// URI string.
     */
    public static String toOhUri(String androidUri) {
        if (androidUri == null) return null;
        return toOhUri(Uri.parse(androidUri));
    }

    /**
     * Convert OH datashare:// URI to Android content:// URI.
     *
     * datashare:///com.ohos.contacts/contacts/1
     *   -> content://com.android.contacts/contacts/1
     */
    public static Uri toAndroidUri(String ohUri) {
        if (ohUri == null) return null;

        // Parse datashare:///bundleName/path
        // Remove scheme: "datashare:///"
        String stripped = ohUri;
        if (stripped.startsWith(OH_SCHEME + ":///")) {
            stripped = stripped.substring(OH_SCHEME.length() + 4);
        } else if (stripped.startsWith(OH_SCHEME + "://")) {
            stripped = stripped.substring(OH_SCHEME.length() + 3);
        }

        // Split into bundleName and path
        int slashIdx = stripped.indexOf('/');
        String bundleName;
        String path;
        if (slashIdx >= 0) {
            bundleName = stripped.substring(0, slashIdx);
            path = stripped.substring(slashIdx);
        } else {
            bundleName = stripped;
            path = "";
        }

        String authority = BUNDLE_TO_AUTHORITY.getOrDefault(bundleName, bundleName);
        Uri result = Uri.parse(ANDROID_SCHEME + "://" + authority + path);

        Log.d(TAG, "toAndroidUri: " + ohUri + " -> " + result);
        return result;
    }

    /**
     * Look up the OH bundle name for a given Android authority.
     * Returns the authority itself if no mapping exists.
     */
    public static String authorityToBundleName(String authority) {
        return AUTHORITY_TO_BUNDLE.getOrDefault(authority, authority);
    }

    /**
     * Check if an authority maps to a known OH system data provider.
     */
    public static boolean isSystemProvider(String authority) {
        return AUTHORITY_TO_BUNDLE.containsKey(authority);
    }
}
