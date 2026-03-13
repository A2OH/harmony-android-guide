package android.telephony;

/**
 * Android-compatible DataFailCause shim. Stub implementation for mock testing.
 */
public final class DataFailCause {

    private DataFailCause() {}

    public static final int NONE = 0;
    public static final int OPERATOR_BARRED = 8;
    public static final int NAS_SIGNALLING = 14;
    public static final int LLC_SNDCP = 25;
    public static final int INSUFFICIENT_RESOURCES = 26;
    public static final int MISSING_UNKNOWN_APN = 27;
    public static final int UNKNOWN_PDP_ADDRESS_TYPE = 28;
    public static final int USER_AUTHENTICATION = 29;
    public static final int ACTIVATION_REJECT_GGSN = 30;
    public static final int ACTIVATION_REJECT_UNSPECIFIED = 31;
    public static final int SERVICE_OPTION_NOT_SUPPORTED = 32;
    public static final int SERVICE_OPTION_NOT_SUBSCRIBED = 33;
    public static final int SERVICE_OPTION_OUT_OF_ORDER = 34;
    public static final int NSAPI_IN_USE = 35;
    public static final int REGULAR_DEACTIVATION = 36;
    public static final int QOS_NOT_ACCEPTED = 37;
    public static final int NETWORK_FAILURE = 38;
    public static final int UMTS_REACTIVATION_REQ = 39;
    public static final int FEATURE_NOT_SUPP = 40;
    public static final int TFT_SEMANTIC_ERROR = 41;
    public static final int TFT_SYTAX_ERROR = 42;
    public static final int UNKNOWN_PDP_CONTEXT = 43;
    public static final int FILTER_SEMANTIC_ERROR = 44;
    public static final int FILTER_SYTAX_ERROR = 45;
    public static final int PDP_WITHOUT_ACTIVE_TFT = 46;
    public static final int ONLY_IPV4_ALLOWED = 50;
    public static final int ONLY_IPV6_ALLOWED = 51;
    public static final int ONLY_SINGLE_BEARER_ALLOWED = 52;
    public static final int ESM_INFO_NOT_RECEIVED = 53;
    public static final int PDN_CONN_DOES_NOT_EXIST = 54;
    public static final int MULTI_CONN_TO_SAME_PDN_ADDRESS_NOT_ALLOWED = 55;
    public static final int MAX_ACTIVE_PDP_CONTEXT_REACHED = 65;
    public static final int UNSUPPORTED_APN_IN_CURRENT_PLMN = 66;
    public static final int INVALID_TRANSACTION_ID = 81;
    public static final int MESSAGE_INCORRECT_SEMANTIC = 95;
    public static final int INVALID_MANDATORY_INFO = 96;
    public static final int MESSAGE_TYPE_UNSUPPORTED = 97;
    public static final int MSG_TYPE_NONCOMPATIBLE_STATE = 98;
    public static final int UNKNOWN_INFO_ELEMENT = 99;
    public static final int CONDITIONAL_IE_ERROR = 100;
    public static final int MSG_AND_PROTOCOL_STATE_UNCOMPATIBLE = 101;
    public static final int PROTOCOL_ERRORS = 111;
    public static final int APN_TYPE_CONFLICT = 112;
    public static final int INVALID_PCSCF_ADDR = 113;
    public static final int INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN = 114;
    public static final int EMM_ACCESS_BARRED = 115;
    public static final int EMERGENCY_IFACE_ONLY = 116;
    public static final int IFACE_MISMATCH = 117;
    public static final int COMPANION_IFACE_IN_USE = 118;
    public static final int IP_ADDRESS_MISMATCH = 119;
    public static final int IFACE_AND_POL_FAMILY_MISMATCH = 120;
    public static final int EMM_ACCESS_BARRED_INFINITE_RETRY = 121;
    public static final int AUTH_FAILURE_ON_EMERGENCY_CALL = 122;
    public static final int OEM_DCFAILCAUSE_1 = 0x1001;
    public static final int OEM_DCFAILCAUSE_2 = 0x1002;
    public static final int OEM_DCFAILCAUSE_3 = 0x1003;
    public static final int UNACCEPTABLE_NETWORK_PARAMETER = 0x10001;
    public static final int ERROR_UNSPECIFIED = 0xFFFF;
    public static final int UNKNOWN = Integer.MAX_VALUE;
}
