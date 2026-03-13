package android.webkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.webkit.GeolocationPermissions
 * OH mapping: @ohos.web.webview — geolocation permission APIs
 *
 * Manages geolocation permissions for WebView origins.  On OpenHarmony the
 * equivalent is the permission management provided by the ArkUI Web component's
 * {@code onGeolocationShow} and {@code onGeolocationHide} events together with
 * the webview controller's geolocation allow/deny methods.
 *
 * This shim tracks allowed/denied origins in-memory; the bridge runtime syncs
 * these decisions to the ArkUI layer at runtime.
 */
public class GeolocationPermissions {

    // ── Singleton ──

    private static final GeolocationPermissions INSTANCE = new GeolocationPermissions();

    /** Returns the singleton GeolocationPermissions instance. */
    public static GeolocationPermissions getInstance() {
        return INSTANCE;
    }

    // ── Inner interface ──

    /**
     * Callback interface used by the WebChromeClient to respond to a
     * geolocation permission request from the page.
     *
     * OH equivalent: callback in onGeolocationShow event handler.
     */
    public interface Callback {
        /**
         * Invoke to set the geolocation permission for the origin.
         *
         * @param origin  the origin requesting permission
         * @param allow   true to grant, false to deny
         * @param retain  true to remember the decision for future sessions
         */
        void invoke(String origin, boolean allow, boolean retain);
    }

    // ── Internal state ──

    /** Origins that have been explicitly allowed. */
    private final Set<String> allowedOrigins = new HashSet<>();
    /** Origins that have been retained (persisted) decisions. */
    private final Map<String, Boolean> retainedDecisions = new HashMap<>();

    // Package-private constructor — use getInstance().
    GeolocationPermissions() {}

    // ── Permission management ──

    /**
     * Allows geolocation access for the given origin.
     * OH equivalent: geolocationPermission.allow() in onGeolocationShow callback.
     */
    public void allow(String origin) {
        if (origin == null) return;
        allowedOrigins.add(origin);
    }

    /**
     * Denies and clears any stored geolocation permission for the given origin.
     * OH equivalent: geolocationPermission.deny() in onGeolocationShow callback.
     */
    public void clear(String origin) {
        if (origin == null) return;
        allowedOrigins.remove(origin);
        retainedDecisions.remove(origin);
    }

    /**
     * Clears all stored geolocation permissions.
     * OH equivalent: WebviewController.clearGeolocation()
     */
    public void clearAll() {
        allowedOrigins.clear();
        retainedDecisions.clear();
    }

    // ── Queries ──

    /**
     * Returns whether the given origin currently has a stored allow decision,
     * via an asynchronous callback.
     *
     * @param origin   the origin to query
     * @param callback receives a Boolean indicating allowed (true) or not (false/null)
     */
    public void getAllowed(String origin, ValueCallback<Boolean> callback) {
        if (callback == null) return;
        callback.onReceiveValue(origin != null && allowedOrigins.contains(origin));
    }

    /**
     * Returns the set of origins that have stored geolocation permissions,
     * via an asynchronous callback.
     */
    public void getOrigins(ValueCallback<Set<String>> callback) {
        if (callback == null) return;
        callback.onReceiveValue(new HashSet<>(allowedOrigins));
    }
}
