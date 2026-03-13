package android.telephony;
import android.app.PendingIntent;
import android.net.Uri;
import android.telecom.PhoneAccountHandle;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class TelephonyManager {
    public static final int ACTION_CARRIER_MESSAGING_CLIENT_SERVICE = 0;
    public static final int ACTION_CONFIGURE_VOICEMAIL = 0;
    public static final int ACTION_MULTI_SIM_CONFIG_CHANGED = 0;
    public static final int ACTION_NETWORK_COUNTRY_CHANGED = 0;
    public static final int ACTION_RESPOND_VIA_MESSAGE = 0;
    public static final int ACTION_SECRET_CODE = 0;
    public static final int ACTION_SHOW_VOICEMAIL_NOTIFICATION = 0;
    public static final int ACTION_SUBSCRIPTION_CARRIER_IDENTITY_CHANGED = 0;
    public static final int ACTION_SUBSCRIPTION_SPECIFIC_CARRIER_IDENTITY_CHANGED = 0;
    public static final int APPTYPE_CSIM = 0;
    public static final int APPTYPE_ISIM = 0;
    public static final int APPTYPE_RUIM = 0;
    public static final int APPTYPE_SIM = 0;
    public static final int APPTYPE_USIM = 0;
    public static final int AUTHTYPE_EAP_AKA = 0;
    public static final int AUTHTYPE_EAP_SIM = 0;
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_OFFHOOK = 0;
    public static final int CALL_STATE_RINGING = 0;
    public static final int CDMA_ROAMING_MODE_AFFILIATED = 0;
    public static final int CDMA_ROAMING_MODE_ANY = 0;
    public static final int CDMA_ROAMING_MODE_HOME = 0;
    public static final int CDMA_ROAMING_MODE_RADIO_DEFAULT = 0;
    public static final int DATA_ACTIVITY_DORMANT = 0;
    public static final int DATA_ACTIVITY_IN = 0;
    public static final int DATA_ACTIVITY_INOUT = 0;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_OUT = 0;
    public static final int DATA_CONNECTED = 0;
    public static final int DATA_CONNECTING = 0;
    public static final int DATA_DISCONNECTED = 0;
    public static final int DATA_DISCONNECTING = 0;
    public static final int DATA_SUSPENDED = 0;
    public static final int DATA_UNKNOWN = 0;
    public static final int EXTRA_ACTIVE_SIM_SUPPORTED_COUNT = 0;
    public static final int EXTRA_CALL_VOICEMAIL_INTENT = 0;
    public static final int EXTRA_CARRIER_ID = 0;
    public static final int EXTRA_CARRIER_NAME = 0;
    public static final int EXTRA_HIDE_PUBLIC_SETTINGS = 0;
    public static final int EXTRA_IS_REFRESH = 0;
    public static final int EXTRA_LAUNCH_VOICEMAIL_SETTINGS_INTENT = 0;
    public static final int EXTRA_NETWORK_COUNTRY = 0;
    public static final int EXTRA_NOTIFICATION_COUNT = 0;
    public static final int EXTRA_PHONE_ACCOUNT_HANDLE = 0;
    public static final int EXTRA_SPECIFIC_CARRIER_ID = 0;
    public static final int EXTRA_SPECIFIC_CARRIER_NAME = 0;
    public static final int EXTRA_STATE = 0;
    public static final int EXTRA_STATE_IDLE = 0;
    public static final int EXTRA_STATE_OFFHOOK = 0;
    public static final int EXTRA_STATE_RINGING = 0;
    public static final int EXTRA_SUBSCRIPTION_ID = 0;
    public static final int EXTRA_VOICEMAIL_NUMBER = 0;
    public static final int METADATA_HIDE_VOICEMAIL_SETTINGS_MENU = 0;
    public static final int MULTISIM_ALLOWED = 0;
    public static final int MULTISIM_NOT_SUPPORTED_BY_CARRIER = 0;
    public static final int MULTISIM_NOT_SUPPORTED_BY_HARDWARE = 0;
    public static final int NETWORK_SELECTION_MODE_AUTO = 0;
    public static final int NETWORK_SELECTION_MODE_MANUAL = 0;
    public static final int NETWORK_SELECTION_MODE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_1xRTT = 0;
    public static final int NETWORK_TYPE_CDMA = 0;
    public static final int NETWORK_TYPE_EDGE = 0;
    public static final int NETWORK_TYPE_EHRPD = 0;
    public static final int NETWORK_TYPE_EVDO_0 = 0;
    public static final int NETWORK_TYPE_EVDO_A = 0;
    public static final int NETWORK_TYPE_EVDO_B = 0;
    public static final int NETWORK_TYPE_GPRS = 0;
    public static final int NETWORK_TYPE_GSM = 0;
    public static final int NETWORK_TYPE_HSDPA = 0;
    public static final int NETWORK_TYPE_HSPA = 0;
    public static final int NETWORK_TYPE_HSPAP = 0;
    public static final int NETWORK_TYPE_HSUPA = 0;
    public static final int NETWORK_TYPE_IDEN = 0;
    public static final int NETWORK_TYPE_IWLAN = 0;
    public static final int NETWORK_TYPE_LTE = 0;
    public static final int NETWORK_TYPE_NR = 0;
    public static final int NETWORK_TYPE_TD_SCDMA = 0;
    public static final int NETWORK_TYPE_UMTS = 0;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int PHONE_TYPE_CDMA = 0;
    public static final int PHONE_TYPE_GSM = 0;
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_SIP = 0;
    public static final int SET_OPPORTUNISTIC_SUB_INACTIVE_SUBSCRIPTION = 0;
    public static final int SET_OPPORTUNISTIC_SUB_NO_OPPORTUNISTIC_SUB_AVAILABLE = 0;
    public static final int SET_OPPORTUNISTIC_SUB_REMOTE_SERVICE_EXCEPTION = 0;
    public static final int SET_OPPORTUNISTIC_SUB_SUCCESS = 0;
    public static final int SET_OPPORTUNISTIC_SUB_VALIDATION_FAILED = 0;
    public static final int SIM_STATE_ABSENT = 0;
    public static final int SIM_STATE_CARD_IO_ERROR = 0;
    public static final int SIM_STATE_CARD_RESTRICTED = 0;
    public static final int SIM_STATE_NETWORK_LOCKED = 0;
    public static final int SIM_STATE_NOT_READY = 0;
    public static final int SIM_STATE_PERM_DISABLED = 0;
    public static final int SIM_STATE_PIN_REQUIRED = 0;
    public static final int SIM_STATE_PUK_REQUIRED = 0;
    public static final int SIM_STATE_READY = 0;
    public static final int SIM_STATE_UNKNOWN = 0;
    public static final int UNINITIALIZED_CARD_ID = 0;
    public static final int UNKNOWN_CARRIER_ID = 0;
    public static final int UNSUPPORTED_CARD_ID = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_ABORTED = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_DISABLE_MODEM_FAIL = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_ENABLE_MODEM_FAIL = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_INVALID_ARGUMENTS = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_MULTIPLE_NETWORKS_NOT_SUPPORTED = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_NO_CARRIER_PRIVILEGE = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_NO_OPPORTUNISTIC_SUB_AVAILABLE = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_REMOTE_SERVICE_EXCEPTION = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_SERVICE_IS_DISABLED = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_SUCCESS = 0;
    public static final int UPDATE_AVAILABLE_NETWORKS_UNKNOWN_FAILURE = 0;
    public static final int USSD_ERROR_SERVICE_UNAVAIL = 0;
    public static final int USSD_RETURN_FAILURE = 0;
    public static final int VVM_TYPE_CVVM = 0;
    public static final int VVM_TYPE_OMTP = 0;
    public static final int ERROR_MODEM_ERROR = 0;
    public static final int ERROR_TIMEOUT = 0;


    public boolean canChangeDtmfToneLength() { return false; }
    public TelephonyManager createForSubscriptionId(int p0) { return null; }
    public int getActiveModemCount() { return 0; }
    public int getCallState() { return 0; }
    public int getCardIdForDefaultEuicc() { return 0; }
    public int getCarrierIdFromSimMccMnc() { return 0; }
    public int getDataActivity() { return 0; }
    public int getDataState() { return 0; }
    public String getIccAuthentication(int p0, int p1, String p2) { return null; }
    public String getMmsUAProfUrl() { return null; }
    public String getMmsUserAgent() { return null; }
    public String getNetworkCountryIso() { return null; }
    public String getNetworkOperator() { return null; }
    public String getNetworkOperatorName() { return null; }
    public String getNetworkSpecifier() { return null; }
    public int getPhoneType() { return 0; }
    public int getSimCarrierId() { return 0; }
    public String getSimCountryIso() { return null; }
    public String getSimOperator() { return null; }
    public String getSimOperatorName() { return null; }
    public int getSimSpecificCarrierId() { return 0; }
    public int getSimState() { return 0; }
    public int getSimState(int p0) { return 0; }
    public int getSubscriptionId() { return 0; }
    public int getSupportedModemCount() { return 0; }
    public Uri getVoicemailRingtoneUri(PhoneAccountHandle p0) { return null; }
    public boolean hasCarrierPrivileges() { return false; }
    public boolean hasIccCard() { return false; }
    public boolean isConcurrentVoiceAndDataSupported() { return false; }
    public boolean isEmergencyNumber(String p0) { return false; }
    public boolean isHearingAidCompatibilitySupported() { return false; }
    public boolean isNetworkRoaming() { return false; }
    public boolean isRttSupported() { return false; }
    public boolean isSmsCapable() { return false; }
    public boolean isVoiceCapable() { return false; }
    public boolean isVoicemailVibrationEnabled(PhoneAccountHandle p0) { return false; }
    public boolean isWorldPhone() { return false; }
    public void listen(PhoneStateListener p0, int p1) {}
    public void sendDialerSpecialCode(String p0) {}
    public void sendVisualVoicemailSms(String p0, int p1, String p2, PendingIntent p3) {}
    public boolean setLine1NumberForDisplay(String p0, String p1) { return false; }
    public boolean setOperatorBrandOverride(String p0) { return false; }
    public boolean setPreferredNetworkTypeToGlobal() { return false; }
    public void setPreferredOpportunisticDataSubscription(int p0, boolean p1, Executor p2, Object p3) {}
    public void setVisualVoicemailSmsFilterSettings(VisualVoicemailSmsFilterSettings p0) {}
    public boolean setVoiceMailNumber(String p0, String p1) { return false; }
    public void updateAvailableNetworks(java.util.List<Object> p0, Executor p1, Object p2) {}
    public void onCellInfo(java.util.List<Object> p0) {}
    public void onError(int p0, Throwable p1) {}
    public void onReceiveUssdResponse(TelephonyManager p0, String p1, CharSequence p2) {}
    public void onReceiveUssdResponseFailed(TelephonyManager p0, String p1, int p2) {}
}
