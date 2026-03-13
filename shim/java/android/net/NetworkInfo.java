package android.net;

/**
 * Shim: android.net.NetworkInfo → @ohos.net.connection
 *
 * Carries the state of a single network type (Wi-Fi, Mobile, Ethernet, …).
 * Instances are created by ConnectivityManager; callers treat this as read-only.
 */
public class NetworkInfo {

    // ── State ──────────────────────────────────────────────────────────────────

    public enum State {
        CONNECTING,
        CONNECTED,
        SUSPENDED,
        DISCONNECTING,
        DISCONNECTED,
        UNKNOWN
    }

    // ── DetailedState ──────────────────────────────────────────────────────────

    public enum DetailedState {
        IDLE,
        SCANNING,
        CONNECTING,
        AUTHENTICATING,
        OBTAINING_IPADDR,
        CONNECTED,
        SUSPENDED,
        DISCONNECTING,
        DISCONNECTED,
        FAILED,
        BLOCKED,
        VERIFYING_POOR_LINK,
        CAPTIVE_PORTAL_CHECK
    }

    // ── Fields ─────────────────────────────────────────────────────────────────

    private final int type;
    private final String typeName;
    private final boolean connected;
    private final boolean available;
    private final State state;
    private final DetailedState detailedState;
    private final String extraInfo;   // e.g. SSID for Wi-Fi

    // ── Package-private constructor (used by ConnectivityManager) ──────────────

    NetworkInfo(int type, String typeName, boolean connected, boolean available,
                State state, DetailedState detailedState, String extraInfo) {
        this.type          = type;
        this.typeName      = typeName;
        this.connected     = connected;
        this.available     = available;
        this.state         = state;
        this.detailedState = detailedState;
        this.extraInfo     = extraInfo;
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Returns the network type constant (e.g. ConnectivityManager.TYPE_WIFI).
     */
    public int getType() {
        return type;
    }

    /**
     * Returns a human-readable network type name ("WIFI", "MOBILE", "ETHERNET", …).
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns true when this network is connected and usable.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Returns true when this network is available (not necessarily connected yet).
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Returns true when this network is in a connected-or-connecting transitional state.
     */
    public boolean isConnectedOrConnecting() {
        return state == State.CONNECTED || state == State.CONNECTING;
    }

    /**
     * Returns the coarse connection State.
     */
    public State getState() {
        return state;
    }

    /**
     * Returns the fine-grained DetailedState.
     */
    public DetailedState getDetailedState() {
        return detailedState;
    }

    /**
     * Returns ancillary information about the network (e.g. Wi-Fi SSID).
     * May be null.
     */
    public String getExtraInfo() {
        return extraInfo;
    }

    // ── Object ─────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "NetworkInfo{type=" + typeName
                + ", state=" + state
                + ", connected=" + connected
                + ", available=" + available
                + (extraInfo != null ? ", extra=" + extraInfo : "")
                + "}";
    }
}
