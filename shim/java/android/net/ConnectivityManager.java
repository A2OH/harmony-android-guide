package android.net;

import com.ohos.shim.bridge.OHBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.net.ConnectivityManager → @ohos.net.connection
 *
 * On OpenHarmony the @ohos.net.connection module provides equivalent
 * functionality.  This shim surfaces it through the familiar Android API
 * surface so that existing Android code compiles and runs with minimal changes.
 *
 * OH mapping summary:
 *   getActiveNetworkInfo()          → connection.getDefaultNet() + getNetCapabilities()
 *   isDefaultNetworkActive()        → OHBridge.isNetworkAvailable()
 *   registerDefaultNetworkCallback  → connection.on('netAvailable' / 'netLost')
 *
 * Native queries are delegated to OHBridge.isNetworkAvailable() and
 * OHBridge.getNetworkType(), which already exist in both the real Rust bridge
 * and the mock OHBridge used during local JVM testing.
 */
public class ConnectivityManager {

    // ── Network-type constants (mirror android.net.ConnectivityManager) ──────

    /** Mobile data (cellular). OH: BEARER_CELLULAR */
    public static final int TYPE_MOBILE   = 0;
    /** Wi-Fi.  OH: BEARER_WIFI */
    public static final int TYPE_WIFI     = 1;
    /** Ethernet.  OH: BEARER_ETHERNET */
    public static final int TYPE_ETHERNET = 9;

    // ── OH getNetworkType() return values ────────────────────────────────────
    // Must stay in sync with the Rust bridge implementation.
    private static final int OH_NET_TYPE_WIFI     = 1;
    private static final int OH_NET_TYPE_CELLULAR = 2;
    private static final int OH_NET_TYPE_ETHERNET = 3;

    // ── NetworkCallback ───────────────────────────────────────────────────────

    /**
     * Base class for network-state callbacks.
     * Mirrors android.net.ConnectivityManager.NetworkCallback.
     */
    public static class NetworkCallback {
        /** Called when a satisfying network becomes available. */
        public void onAvailable(Network network) {}
        /** Called when the network is lost. */
        public void onLost(Network network) {}
        /** Called when the network is about to be lost (grace-period). */
        public void onLosing(Network network, int maxMsToLive) {}
        /** Called when none of the requested capabilities can be satisfied. */
        public void onUnavailable() {}
    }

    // ── Instance state ────────────────────────────────────────────────────────

    private final List<NetworkCallback> mCallbacks = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public ConnectivityManager() {}

    // ── Core API ──────────────────────────────────────────────────────────────

    /**
     * Returns a NetworkInfo describing the currently-active default network,
     * or null if there is no active network.
     *
     * Internally calls OHBridge.isNetworkAvailable() and
     * OHBridge.getNetworkType(), which map to @ohos.net.connection queries.
     */
    public NetworkInfo getActiveNetworkInfo() {
        if (!OHBridge.isNetworkAvailable()) {
            return null;
        }
        int ohType = OHBridge.getNetworkType();
        return buildNetworkInfo(ohTypeToAndroidType(ohType), true);
    }

    /**
     * Returns a Network handle for the currently-active default network,
     * or null if there is no active network.
     *
     * This is a stub — OH network handles are managed asynchronously; here we
     * return a synthetic handle so that code which passes Network objects
     * around continues to work.
     */
    public Network getActiveNetwork() {
        if (!OHBridge.isNetworkAvailable()) {
            return null;
        }
        return new Network(OHBridge.getNetworkType());
    }

    /**
     * Returns true if the default network is currently active.
     */
    public boolean isDefaultNetworkActive() {
        return OHBridge.isNetworkAvailable();
    }

    /**
     * Returns a NetworkInfo for the given network type, or null if that type
     * is not currently active.
     *
     * @param networkType one of TYPE_MOBILE, TYPE_WIFI, TYPE_ETHERNET, …
     */
    public NetworkInfo getNetworkInfo(int networkType) {
        if (!OHBridge.isNetworkAvailable()) {
            return buildNetworkInfo(networkType, false);
        }
        int activeAndroidType = ohTypeToAndroidType(OHBridge.getNetworkType());
        boolean active = (activeAndroidType == networkType);
        return buildNetworkInfo(networkType, active);
    }

    /**
     * Returns one NetworkInfo per known network type.
     * In this shim, TYPE_WIFI, TYPE_MOBILE, and TYPE_ETHERNET are enumerated.
     */
    public NetworkInfo[] getAllNetworkInfo() {
        return new NetworkInfo[]{
                getNetworkInfo(TYPE_WIFI),
                getNetworkInfo(TYPE_MOBILE),
                getNetworkInfo(TYPE_ETHERNET)
        };
    }

    // ── Callback registration ─────────────────────────────────────────────────

    /**
     * Registers a callback that will be invoked whenever the default network
     * changes.
     *
     * Note: In this shim, callbacks are stored but not actively dispatched on a
     * background thread; on a real OpenHarmony device the Rust bridge would
     * call back into Java when the @ohos.net.connection events fire.
     *
     * @param callback the callback to register
     */
    public void registerDefaultNetworkCallback(NetworkCallback callback) {
        if (callback != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    /**
     * Unregisters a previously-registered callback.
     *
     * @param callback the callback to remove
     */
    public void unregisterNetworkCallback(NetworkCallback callback) {
        mCallbacks.remove(callback);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Maps the OH network-type int (returned by OHBridge.getNetworkType()) to
     * the corresponding Android TYPE_* constant.
     */
    private static int ohTypeToAndroidType(int ohType) {
        switch (ohType) {
            case OH_NET_TYPE_WIFI:     return TYPE_WIFI;
            case OH_NET_TYPE_CELLULAR: return TYPE_MOBILE;
            case OH_NET_TYPE_ETHERNET: return TYPE_ETHERNET;
            default:                   return TYPE_WIFI;  // best-effort fallback
        }
    }

    /**
     * Returns the human-readable name string for a given Android TYPE_* constant.
     */
    private static String androidTypeToName(int type) {
        switch (type) {
            case TYPE_MOBILE:   return "MOBILE";
            case TYPE_WIFI:     return "WIFI";
            case TYPE_ETHERNET: return "ETHERNET";
            default:            return "UNKNOWN";
        }
    }

    /**
     * Constructs a NetworkInfo for the given Android type.
     *
     * @param androidType  TYPE_WIFI / TYPE_MOBILE / TYPE_ETHERNET / …
     * @param connected    true when this type is the currently-active network
     */
    private static NetworkInfo buildNetworkInfo(int androidType, boolean connected) {
        String name = androidTypeToName(androidType);
        NetworkInfo.State state = connected
                ? NetworkInfo.State.CONNECTED
                : NetworkInfo.State.DISCONNECTED;
        NetworkInfo.DetailedState ds = connected
                ? NetworkInfo.DetailedState.CONNECTED
                : NetworkInfo.DetailedState.DISCONNECTED;
        return new NetworkInfo(androidType, name, connected, connected, state, ds, null);
    }
}
