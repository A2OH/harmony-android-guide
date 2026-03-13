package android.telephony;

public final class SmsManager {
    public SmsManager() {}

    public static final int EXTRA_MMS_DATA = 0;
    public static final int EXTRA_MMS_HTTP_STATUS = 0;
    public static final int MMS_CONFIG_ALIAS_ENABLED = 0;
    public static final int MMS_CONFIG_ALIAS_MAX_CHARS = 0;
    public static final int MMS_CONFIG_ALIAS_MIN_CHARS = 0;
    public static final int MMS_CONFIG_ALLOW_ATTACH_AUDIO = 0;
    public static final int MMS_CONFIG_APPEND_TRANSACTION_ID = 0;
    public static final int MMS_CONFIG_EMAIL_GATEWAY_NUMBER = 0;
    public static final int MMS_CONFIG_GROUP_MMS_ENABLED = 0;
    public static final int MMS_CONFIG_HTTP_PARAMS = 0;
    public static final int MMS_CONFIG_HTTP_SOCKET_TIMEOUT = 0;
    public static final int MMS_CONFIG_MAX_IMAGE_HEIGHT = 0;
    public static final int MMS_CONFIG_MAX_IMAGE_WIDTH = 0;
    public static final int MMS_CONFIG_MAX_MESSAGE_SIZE = 0;
    public static final int MMS_CONFIG_MESSAGE_TEXT_MAX_SIZE = 0;
    public static final int MMS_CONFIG_MMS_DELIVERY_REPORT_ENABLED = 0;
    public static final int MMS_CONFIG_MMS_ENABLED = 0;
    public static final int MMS_CONFIG_MMS_READ_REPORT_ENABLED = 0;
    public static final int MMS_CONFIG_MULTIPART_SMS_ENABLED = 0;
    public static final int MMS_CONFIG_NAI_SUFFIX = 0;
    public static final int MMS_CONFIG_NOTIFY_WAP_MMSC_ENABLED = 0;
    public static final int MMS_CONFIG_RECIPIENT_LIMIT = 0;
    public static final int MMS_CONFIG_SEND_MULTIPART_SMS_AS_SEPARATE_MESSAGES = 0;
    public static final int MMS_CONFIG_SHOW_CELL_BROADCAST_APP_LINKS = 0;
    public static final int MMS_CONFIG_SMS_DELIVERY_REPORT_ENABLED = 0;
    public static final int MMS_CONFIG_SMS_TO_MMS_TEXT_LENGTH_THRESHOLD = 0;
    public static final int MMS_CONFIG_SMS_TO_MMS_TEXT_THRESHOLD = 0;
    public static final int MMS_CONFIG_SUBJECT_MAX_LENGTH = 0;
    public static final int MMS_CONFIG_SUPPORT_HTTP_CHARSET_HEADER = 0;
    public static final int MMS_CONFIG_SUPPORT_MMS_CONTENT_DISPOSITION = 0;
    public static final int MMS_CONFIG_UA_PROF_TAG_NAME = 0;
    public static final int MMS_CONFIG_UA_PROF_URL = 0;
    public static final int MMS_CONFIG_USER_AGENT = 0;
    public static final int MMS_ERROR_CONFIGURATION_ERROR = 0;
    public static final int MMS_ERROR_HTTP_FAILURE = 0;
    public static final int MMS_ERROR_INVALID_APN = 0;
    public static final int MMS_ERROR_IO_ERROR = 0;
    public static final int MMS_ERROR_NO_DATA_NETWORK = 0;
    public static final int MMS_ERROR_RETRY = 0;
    public static final int MMS_ERROR_UNABLE_CONNECT_MMS = 0;
    public static final int MMS_ERROR_UNSPECIFIED = 0;
    public static final int RESULT_BLUETOOTH_DISCONNECTED = 0;
    public static final int RESULT_CANCELLED = 0;
    public static final int RESULT_ENCODING_ERROR = 0;
    public static final int RESULT_ERROR_FDN_CHECK_FAILURE = 0;
    public static final int RESULT_ERROR_GENERIC_FAILURE = 0;
    public static final int RESULT_ERROR_LIMIT_EXCEEDED = 0;
    public static final int RESULT_ERROR_NONE = 0;
    public static final int RESULT_ERROR_NO_SERVICE = 0;
    public static final int RESULT_ERROR_NULL_PDU = 0;
    public static final int RESULT_ERROR_RADIO_OFF = 0;
    public static final int RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED = 0;
    public static final int RESULT_ERROR_SHORT_CODE_NOT_ALLOWED = 0;
    public static final int RESULT_INTERNAL_ERROR = 0;
    public static final int RESULT_INVALID_ARGUMENTS = 0;
    public static final int RESULT_INVALID_BLUETOOTH_ADDRESS = 0;
    public static final int RESULT_INVALID_SMSC_ADDRESS = 0;
    public static final int RESULT_INVALID_SMS_FORMAT = 0;
    public static final int RESULT_INVALID_STATE = 0;
    public static final int RESULT_MODEM_ERROR = 0;
    public static final int RESULT_NETWORK_ERROR = 0;
    public static final int RESULT_NETWORK_REJECT = 0;
    public static final int RESULT_NO_BLUETOOTH_SERVICE = 0;
    public static final int RESULT_NO_DEFAULT_SMS_APP = 0;
    public static final int RESULT_NO_MEMORY = 0;
    public static final int RESULT_NO_RESOURCES = 0;
    public static final int RESULT_OPERATION_NOT_ALLOWED = 0;
    public static final int RESULT_RADIO_NOT_AVAILABLE = 0;
    public static final int RESULT_RECEIVE_DISPATCH_FAILURE = 0;
    public static final int RESULT_RECEIVE_INJECTED_NULL_PDU = 0;
    public static final int RESULT_RECEIVE_NULL_MESSAGE_FROM_RIL = 0;
    public static final int RESULT_RECEIVE_RUNTIME_EXCEPTION = 0;
    public static final int RESULT_RECEIVE_SQL_EXCEPTION = 0;
    public static final int RESULT_RECEIVE_URI_EXCEPTION = 0;
    public static final int RESULT_RECEIVE_WHILE_ENCRYPTED = 0;
    public static final int RESULT_REMOTE_EXCEPTION = 0;
    public static final int RESULT_REQUEST_NOT_SUPPORTED = 0;
    public static final int RESULT_RIL_CANCELLED = 0;
    public static final int RESULT_RIL_ENCODING_ERR = 0;
    public static final int RESULT_RIL_INTERNAL_ERR = 0;
    public static final int RESULT_RIL_INVALID_ARGUMENTS = 0;
    public static final int RESULT_RIL_INVALID_MODEM_STATE = 0;
    public static final int RESULT_RIL_INVALID_SMSC_ADDRESS = 0;
    public static final int RESULT_RIL_INVALID_SMS_FORMAT = 0;
    public static final int RESULT_RIL_INVALID_STATE = 0;
    public static final int RESULT_RIL_MODEM_ERR = 0;
    public static final int RESULT_RIL_NETWORK_ERR = 0;
    public static final int RESULT_RIL_NETWORK_NOT_READY = 0;
    public static final int RESULT_RIL_NETWORK_REJECT = 0;
    public static final int RESULT_RIL_NO_MEMORY = 0;
    public static final int RESULT_RIL_NO_RESOURCES = 0;
    public static final int RESULT_RIL_OPERATION_NOT_ALLOWED = 0;
    public static final int RESULT_RIL_RADIO_NOT_AVAILABLE = 0;
    public static final int RESULT_RIL_REQUEST_NOT_SUPPORTED = 0;
    public static final int RESULT_RIL_REQUEST_RATE_LIMITED = 0;
    public static final int RESULT_RIL_SIM_ABSENT = 0;
    public static final int RESULT_RIL_SMS_SEND_FAIL_RETRY = 0;
    public static final int RESULT_RIL_SYSTEM_ERR = 0;
    public static final int RESULT_SMS_BLOCKED_DURING_EMERGENCY = 0;
    public static final int RESULT_SMS_SEND_RETRY_FAILED = 0;
    public static final int RESULT_SYSTEM_ERROR = 0;
    public static final int RESULT_UNEXPECTED_EVENT_STOP_SENDING = 0;
    public static final int STATUS_ON_ICC_FREE = 0;
    public static final int STATUS_ON_ICC_READ = 0;
    public static final int STATUS_ON_ICC_SENT = 0;
    public static final int STATUS_ON_ICC_UNREAD = 0;
    public static final int STATUS_ON_ICC_UNSENT = 0;
    public Object createAppSpecificSmsToken(Object p0) { return null; }
    public Object divideMessage(Object p0) { return null; }
    public void downloadMultimediaMessage(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public static Object getDefault() { return null; }
    public static int getDefaultSmsSubscriptionId() { return 0; }
    public static Object getSmsManagerForSubscriptionId(Object p0) { return null; }
    public int getSubscriptionId() { return 0; }
    public void injectSmsPdu(Object p0, Object p1, Object p2) {}
    public void sendDataMessage(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void sendMultimediaMessage(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void sendMultipartTextMessage(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void sendMultipartTextMessage(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void sendMultipartTextMessage(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {}
    public void sendTextMessage(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public void sendTextMessage(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void onFinancialSmsMessages(Object p0) {}
}
