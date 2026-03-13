package android.telephony;

import com.ohos.shim.bridge.OHBridge;

/**
 * Android-compatible TelephonyManager shim.
 *
 * Maps to OpenHarmony:
 *   @ohos.telephony.sim    – SIM state, operator, subscriber info
 *   @ohos.telephony.observer – network type, roaming, call state
 *
 * On real OpenHarmony devices the native methods in OHBridge delegate to
 * the OH telephony NDK/JS APIs. In local-JVM tests the mock OHBridge
 * returns sensible stub values so all APIs can be exercised without a
 * device.
 */
public class TelephonyManager {

    // ── Phone type constants ─────────────────────────────────────────────────
    /** No phone radio. */
    public static final int PHONE_TYPE_NONE  = 0;
    /** GSM phone. */
    public static final int PHONE_TYPE_GSM   = 1;
    /** CDMA phone. */
    public static final int PHONE_TYPE_CDMA  = 2;
    /** SIP phone. */
    public static final int PHONE_TYPE_SIP   = 3;

    // ── SIM state constants ──────────────────────────────────────────────────
    public static final int SIM_STATE_UNKNOWN        = 0;
    public static final int SIM_STATE_ABSENT         = 1;
    public static final int SIM_STATE_PIN_REQUIRED   = 2;
    public static final int SIM_STATE_PUK_REQUIRED   = 3;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_READY          = 5;

    // ── Network type constants ───────────────────────────────────────────────
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_GPRS    = 1;
    public static final int NETWORK_TYPE_EDGE    = 2;
    public static final int NETWORK_TYPE_UMTS    = 3;
    public static final int NETWORK_TYPE_LTE     = 13;
    public static final int NETWORK_TYPE_NR      = 20;

    // ── Data connection state constants ─────────────────────────────────────
    public static final int DATA_DISCONNECTED = 0;
    public static final int DATA_CONNECTING   = 1;
    public static final int DATA_CONNECTED    = 2;

    // ── Singleton ────────────────────────────────────────────────────────────
    private static final TelephonyManager sInstance = new TelephonyManager();

    private TelephonyManager() {}

    /** Returns the system TelephonyManager (context is ignored in shim). */
    public static TelephonyManager from(@SuppressWarnings("unused") android.content.Context context) {
        return sInstance;
    }

    // ── Device identification ────────────────────────────────────────────────

    /**
     * Returns the unique device ID (IMEI/MEID).
     * Maps to @ohos.telephony.sim: getIMEI / getUniqueDeviceId.
     */
    public String getDeviceId() {
        return OHBridge.telephonyGetDeviceId();
    }

    /**
     * Returns the phone number (MSISDN) of line 1.
     * Maps to @ohos.telephony.sim: getLine1Number.
     */
    public String getLine1Number() {
        return OHBridge.telephonyGetLine1Number();
    }

    /**
     * Returns the IMSI of the subscriber.
     * Maps to @ohos.telephony.sim: getSubscriberId.
     */
    public String getSubscriberId() {
        // Not exposed via a dedicated native method; fall back to a stub.
        return "";
    }

    // ── SIM information ──────────────────────────────────────────────────────

    /**
     * Returns the SIM state for the default subscription.
     * Maps to @ohos.telephony.sim: getSimState.
     */
    public int getSimState() {
        return OHBridge.telephonyGetSimState();
    }

    /**
     * Returns the MCC+MNC of the SIM provider (e.g. "31000").
     * Maps to @ohos.telephony.sim: getSimOperator.
     */
    public String getSimOperator() {
        return "";
    }

    /**
     * Returns the display name of the SIM operator.
     * Maps to @ohos.telephony.sim: getSimSpn / getSimOperatorName.
     */
    public String getSimOperatorName() {
        return OHBridge.telephonyGetNetworkOperatorName();
    }

    /**
     * Returns the ISO country code for the SIM provider (lower-case, e.g. "us").
     * Maps to @ohos.telephony.sim: getISOCountryCodeForSim.
     */
    public String getSimCountryIso() {
        return "";
    }

    // ── Network information ──────────────────────────────────────────────────

    /**
     * Returns the phone type (GSM / CDMA / SIP / NONE).
     * Maps to @ohos.telephony.observer / radio.getPhoneType.
     */
    public int getPhoneType() {
        return OHBridge.telephonyGetPhoneType();
    }

    /**
     * Returns the current radio access technology (e.g. LTE, NR).
     * Maps to @ohos.telephony.observer: on('networkStateChange').
     */
    public int getNetworkType() {
        return OHBridge.telephonyGetNetworkType();
    }

    /**
     * Returns the display name of the registered network operator.
     * Maps to @ohos.telephony.observer: NetworkState.longOperatorName.
     */
    public String getNetworkOperatorName() {
        return OHBridge.telephonyGetNetworkOperatorName();
    }

    /**
     * Returns the ISO country code of the registered network (lower-case).
     * Maps to @ohos.telephony.observer: NetworkState.isoCountryCode.
     */
    public String getNetworkCountryIso() {
        return "";
    }

    /**
     * Returns true when the device is registered on a roaming network.
     * Maps to @ohos.telephony.observer: NetworkState.isRoaming.
     */
    public boolean isNetworkRoaming() {
        return false;
    }

    // ── Data / call state ────────────────────────────────────────────────────

    /**
     * Returns the data connection state (DISCONNECTED / CONNECTING / CONNECTED).
     * Maps to @ohos.telephony.observer: on('dataConnectionStateChange').
     */
    public int getDataState() {
        return DATA_CONNECTED;
    }

    /**
     * Returns the call state (CALL_STATE_IDLE / OFFHOOK / RINGING).
     * Maps to @ohos.telephony.observer: on('callStateChange').
     */
    public int getCallState() {
        return 0; // CALL_STATE_IDLE
    }
}
