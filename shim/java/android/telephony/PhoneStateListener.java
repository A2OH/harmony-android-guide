package android.telephony;
import java.util.List;

/**
 * Shim for android.telephony.PhoneStateListener.
 *
 * @deprecated Use {@code TelephonyCallback} and its derivatives on Android 12+.
 *             Retained here for source-level compatibility with older Android APIs.
 */
@Deprecated
public class PhoneStateListener {

    // -------------------------------------------------------------------------
    // LISTEN_* flag constants
    // -------------------------------------------------------------------------

    /** Stop listening; used to cancel a previously registered listener. */
    public static final int LISTEN_NONE                           = 0;

    /** Listen for changes to the device call state. */
    public static final int LISTEN_CALL_STATE                     = 0x00000001;

    /** Listen for changes to the device's cell location. */
    public static final int LISTEN_CELL_LOCATION                  = 0x00000002;

    /** Listen for changes to the network signal strengths. */
    public static final int LISTEN_SIGNAL_STRENGTHS               = 0x00000004;

    /** Listen for changes to the message-waiting indicator. */
    public static final int LISTEN_MESSAGE_WAITING_INDICATOR      = 0x00000008;

    /** Listen for changes to the call-forwarding indicator. */
    public static final int LISTEN_CALL_FORWARDING_INDICATOR      = 0x00000010;

    /** Listen for changes to the device's data activity state. */
    public static final int LISTEN_DATA_ACTIVITY                  = 0x00000020;

    /** Listen for changes to the data connection state. */
    public static final int LISTEN_DATA_CONNECTION_STATE          = 0x00000040;

    /** Listen for changes to the device's service state. */
    public static final int LISTEN_SERVICE_STATE                  = 0x00000080;

    /** Listen for changes to the device's signal strength (deprecated form). */
    public static final int LISTEN_SIGNAL_STRENGTH                = 0x00000100;

    /** Listen for changes to observed cell info. */
    public static final int LISTEN_CELL_INFO                      = 0x00000200;

    /** Listen for changes to the precise call state. */
    public static final int LISTEN_PRECISE_CALL_STATE             = 0x00000400;

    /** Listen for changes to the precise data connection state. */
    public static final int LISTEN_PRECISE_DATA_CONNECTION_STATE  = 0x00001000;

    /** Listen for changes to the OEM hook raw event. */
    public static final int LISTEN_OEM_HOOK_RAW_EVENT             = 0x00008000;

    /** Listen for changes to the SRVCC state. */
    public static final int LISTEN_SRVCC_STATE_CHANGED            = 0x00040000;

    /** Listen for carrier network changes. */
    public static final int LISTEN_CARRIER_NETWORK_CHANGE         = 0x00200000;

    /** Listen for voice activation state changes. */
    public static final int LISTEN_VOICE_ACTIVATION_STATE         = 0x00400000;

    /** Listen for radio power state changes. */
    public static final int LISTEN_RADIO_POWER_STATE_CHANGED      = 0x02000000;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public PhoneStateListener() {}

    // -------------------------------------------------------------------------
    // Object methods — override in subclasses
    // -------------------------------------------------------------------------

    /**
     * Object invoked when the device call state changes.
     *
     * @param state       one of {@link TelephonyManager#CALL_STATE_IDLE},
     *                    {@link TelephonyManager#CALL_STATE_RINGING}, or
     *                    {@link TelephonyManager#CALL_STATE_OFFHOOK}
     * @param phoneNumber the incoming phone number for ringing state;
     *                    empty string otherwise
     */
    public void onCallStateChanged(int state, String phoneNumber) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the network signal strengths change.
     *
     * @param signalStrength a {@code SignalStrength} object (typed as Object for shim compatibility)
     */
    public void onSignalStrengthsChanged(Object signalStrength) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the device's cell location changes.
     *
     * @param location a {@code CellLocation} object (typed as Object for shim compatibility)
     */
    public void onCellLocationChanged(Object location) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the data connection state (and its network type) changes.
     *
     * @param state       the data connection state; one of
     *                    {@link TelephonyManager#DATA_DISCONNECTED},
     *                    {@link TelephonyManager#DATA_CONNECTING},
     *                    {@link TelephonyManager#DATA_CONNECTED}, or
     *                    {@link TelephonyManager#DATA_SUSPENDED}
     * @param networkType the network type of the data connection
     */
    public void onDataConnectionStateChanged(int state, int networkType) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the service state changes.
     *
     * @param serviceState a {@code ServiceState} object (typed as Object for shim compatibility)
     */
    public void onServiceStateChanged(Object serviceState) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the message-waiting indicator changes.
     *
     * @param mwi true if a message is waiting, false otherwise
     */
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the call-forwarding indicator changes.
     *
     * @param cfi true if call forwarding is enabled, false otherwise
     */
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when the over-the-air activation completes.
     *
     * @param state the OTA provisioning state
     */
    public void onOtaspStateChanged(int state) {
        // no-op; override in subclass
    }

    /**
     * Object invoked when a observed cell info has changed or new cells have
     * been added or removed.
     *
     * @param cellInfo list of CellInfo (typed as Object for shim compatibility)
     */
    public void onCellInfoChanged(java.util.List<Object> cellInfo) {
        // no-op; override in subclass
    }
}
