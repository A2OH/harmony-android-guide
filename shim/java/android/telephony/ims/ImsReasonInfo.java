package android.telephony.ims;

/**
 * Android-compatible ImsReasonInfo shim. Stub implementation for mock testing.
 */
public class ImsReasonInfo {

    // --- General codes ---
    public static final int CODE_UNSPECIFIED = 0;
    public static final int CODE_LOCAL_ILLEGAL_ARGUMENT = 101;
    public static final int CODE_LOCAL_ILLEGAL_STATE = 102;
    public static final int CODE_LOCAL_INTERNAL_ERROR = 103;
    public static final int CODE_LOCAL_IMS_SERVICE_DOWN = 106;
    public static final int CODE_LOCAL_NO_PENDING_CALL = 107;

    // --- Local call codes ---
    public static final int CODE_LOCAL_CALL_DECLINE = 1;
    public static final int CODE_LOCAL_CALL_VCC_ON_PROGRESSING = 2;
    public static final int CODE_LOCAL_NETWORK_ROAMING = 3;
    public static final int CODE_LOCAL_NETWORK_IP_CHANGED = 4;
    public static final int CODE_LOCAL_SERVICE_UNAVAILABLE = 5;
    public static final int CODE_LOCAL_NOT_REGISTERED = 6;
    public static final int CODE_LOCAL_CALL_EXCEEDED = 7;
    public static final int CODE_LOCAL_CALL_BUSY = 8;
    public static final int CODE_LOCAL_CALL_NOT_CONNECTED = 9;
    public static final int CODE_LOCAL_POWER_OFF = 10;
    public static final int CODE_LOCAL_LOW_BATTERY = 11;
    public static final int CODE_LOCAL_NETWORK_NO_LTE_COVERAGE = 12;
    public static final int CODE_LOCAL_NETWORK_NO_SERVICE = 13;
    public static final int CODE_LOCAL_CALL_CS_RETRY_REQUIRED = 14;
    public static final int CODE_LOCAL_CALL_VOLTE_RETRY_REQUIRED = 15;
    public static final int CODE_LOCAL_CALL_TERMINATED = 16;
    public static final int CODE_LOCAL_HO_NOT_FEASIBLE = 17;

    // --- SIP codes ---
    public static final int CODE_SIP_REDIRECTED = 321;
    public static final int CODE_SIP_BAD_REQUEST = 400;
    public static final int CODE_SIP_FORBIDDEN = 403;
    public static final int CODE_SIP_NOT_FOUND = 404;
    public static final int CODE_SIP_NOT_SUPPORTED = 415;
    public static final int CODE_SIP_REQUEST_TIMEOUT = 408;
    public static final int CODE_SIP_TEMPRARILY_UNAVAILABLE = 480;
    public static final int CODE_SIP_BAD_ADDRESS = 484;
    public static final int CODE_SIP_BUSY = 486;
    public static final int CODE_SIP_CALL_OR_TRANS_DOES_NOT_EXIST = 481;
    public static final int CODE_SIP_SERVER_INTERNAL_ERROR = 500;
    public static final int CODE_SIP_SERVICE_UNAVAILABLE = 503;
    public static final int CODE_SIP_SERVER_TIMEOUT = 504;
    public static final int CODE_SIP_USER_REJECTED = 603;
    public static final int CODE_SIP_GLOBAL_ERROR = 699;

    // --- Media codes ---
    public static final int CODE_MEDIA_INIT_FAILED = 401;
    public static final int CODE_MEDIA_NO_DATA = 402;
    public static final int CODE_MEDIA_NOT_ACCEPTABLE = 403;
    public static final int CODE_MEDIA_UNSPECIFIED = 404;

    private final int mCode;
    private final int mExtraCode;
    private String mExtraMessage;

    public ImsReasonInfo(int code, int extraCode) {
        mCode = code;
        mExtraCode = extraCode;
        mExtraMessage = null;
    }

    public ImsReasonInfo(int code, int extraCode, String extraMessage) {
        mCode = code;
        mExtraCode = extraCode;
        mExtraMessage = extraMessage;
    }

    public int getCode() {
        return mCode;
    }

    public int getExtraCode() {
        return mExtraCode;
    }

    public String getExtraMessage() {
        return mExtraMessage;
    }
}
