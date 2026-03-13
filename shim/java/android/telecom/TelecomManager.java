package android.telecom;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TelecomManager {
    public static final int ACTION_CHANGE_DEFAULT_DIALER = 0;
    public static final int ACTION_CHANGE_PHONE_ACCOUNTS = 0;
    public static final int ACTION_CONFIGURE_PHONE_ACCOUNT = 0;
    public static final int ACTION_DEFAULT_CALL_SCREENING_APP_CHANGED = 0;
    public static final int ACTION_DEFAULT_DIALER_CHANGED = 0;
    public static final int ACTION_PHONE_ACCOUNT_REGISTERED = 0;
    public static final int ACTION_PHONE_ACCOUNT_UNREGISTERED = 0;
    public static final int ACTION_POST_CALL = 0;
    public static final int ACTION_SHOW_CALL_ACCESSIBILITY_SETTINGS = 0;
    public static final int ACTION_SHOW_CALL_SETTINGS = 0;
    public static final int ACTION_SHOW_MISSED_CALLS_NOTIFICATION = 0;
    public static final int ACTION_SHOW_RESPOND_VIA_SMS_SETTINGS = 0;
    public static final int DTMF_CHARACTER_PAUSE = 0;
    public static final int DTMF_CHARACTER_WAIT = 0;
    public static final int DURATION_LONG = 0;
    public static final int DURATION_MEDIUM = 0;
    public static final int DURATION_SHORT = 0;
    public static final int DURATION_VERY_SHORT = 0;
    public static final int EXTRA_CALL_BACK_NUMBER = 0;
    public static final int EXTRA_CALL_DISCONNECT_CAUSE = 0;
    public static final int EXTRA_CALL_DISCONNECT_MESSAGE = 0;
    public static final int EXTRA_CALL_DURATION = 0;
    public static final int EXTRA_CALL_NETWORK_TYPE = 0;
    public static final int EXTRA_CALL_SUBJECT = 0;
    public static final int EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME = 0;
    public static final int EXTRA_DEFAULT_CALL_SCREENING_APP_COMPONENT_NAME = 0;
    public static final int EXTRA_DISCONNECT_CAUSE = 0;
    public static final int EXTRA_HANDLE = 0;
    public static final int EXTRA_INCOMING_CALL_ADDRESS = 0;
    public static final int EXTRA_INCOMING_CALL_EXTRAS = 0;
    public static final int EXTRA_INCOMING_VIDEO_STATE = 0;
    public static final int EXTRA_IS_DEFAULT_CALL_SCREENING_APP = 0;
    public static final int EXTRA_NOTIFICATION_COUNT = 0;
    public static final int EXTRA_NOTIFICATION_PHONE_NUMBER = 0;
    public static final int EXTRA_OUTGOING_CALL_EXTRAS = 0;
    public static final int EXTRA_PHONE_ACCOUNT_HANDLE = 0;
    public static final int EXTRA_START_CALL_WITH_RTT = 0;
    public static final int EXTRA_START_CALL_WITH_SPEAKERPHONE = 0;
    public static final int EXTRA_START_CALL_WITH_VIDEO_STATE = 0;
    public static final int EXTRA_USE_ASSISTED_DIALING = 0;
    public static final int GATEWAY_ORIGINAL_ADDRESS = 0;
    public static final int GATEWAY_PROVIDER_PACKAGE = 0;
    public static final int METADATA_INCLUDE_EXTERNAL_CALLS = 0;
    public static final int METADATA_INCLUDE_SELF_MANAGED_CALLS = 0;
    public static final int METADATA_IN_CALL_SERVICE_CAR_MODE_UI = 0;
    public static final int METADATA_IN_CALL_SERVICE_RINGING = 0;
    public static final int METADATA_IN_CALL_SERVICE_UI = 0;
    public static final int PRESENTATION_ALLOWED = 0;
    public static final int PRESENTATION_PAYPHONE = 0;
    public static final int PRESENTATION_RESTRICTED = 0;
    public static final int PRESENTATION_UNKNOWN = 0;

    public TelecomManager() {}

    public void acceptHandover(Uri p0, int p1, PhoneAccountHandle p2) {}
    public void addNewIncomingCall(PhoneAccountHandle p0, Bundle p1) {}
    public Intent createManageBlockedNumbersIntent() { return null; }
    public String getDefaultDialerPackage() { return null; }
    public PhoneAccount getPhoneAccount(PhoneAccountHandle p0) { return null; }
    public PhoneAccountHandle getSimCallManager() { return null; }
    public boolean isIncomingCallPermitted(PhoneAccountHandle p0) { return false; }
    public boolean isOutgoingCallPermitted(PhoneAccountHandle p0) { return false; }
    public void registerPhoneAccount(PhoneAccount p0) {}
    public void unregisterPhoneAccount(PhoneAccountHandle p0) {}
}
