package android.telecom;

public final class PhoneAccount {
    public PhoneAccount() {}

    public static final int CAPABILITY_ADHOC_CONFERENCE_CALLING = 0;
    public static final int CAPABILITY_CALL_PROVIDER = 0;
    public static final int CAPABILITY_CALL_SUBJECT = 0;
    public static final int CAPABILITY_CONNECTION_MANAGER = 0;
    public static final int CAPABILITY_PLACE_EMERGENCY_CALLS = 0;
    public static final int CAPABILITY_RTT = 0;
    public static final int CAPABILITY_SELF_MANAGED = 0;
    public static final int CAPABILITY_SIM_SUBSCRIPTION = 0;
    public static final int CAPABILITY_SUPPORTS_VIDEO_CALLING = 0;
    public static final int CAPABILITY_VIDEO_CALLING = 0;
    public static final int CAPABILITY_VIDEO_CALLING_RELIES_ON_PRESENCE = 0;
    public static final int EXTRA_CALL_SUBJECT_CHARACTER_ENCODING = 0;
    public static final int EXTRA_CALL_SUBJECT_MAX_LENGTH = 0;
    public static final int EXTRA_LOG_SELF_MANAGED_CALLS = 0;
    public static final int EXTRA_SUPPORTS_HANDOVER_FROM = 0;
    public static final int EXTRA_SUPPORTS_HANDOVER_TO = 0;
    public static final int NO_HIGHLIGHT_COLOR = 0;
    public static final int NO_RESOURCE_ID = 0;
    public static final int SCHEME_SIP = 0;
    public static final int SCHEME_TEL = 0;
    public static final int SCHEME_VOICEMAIL = 0;
    public static Object builder(Object p0, Object p1) { return null; }
    public int describeContents() { return 0; }
    public Object getAccountHandle() { return null; }
    public Object getAddress() { return null; }
    public int getCapabilities() { return 0; }
    public Object getExtras() { return null; }
    public int getHighlightColor() { return 0; }
    public Object getIcon() { return null; }
    public Object getLabel() { return null; }
    public Object getShortDescription() { return null; }
    public Object getSubscriptionAddress() { return null; }
    public Object getSupportedUriSchemes() { return null; }
    public boolean hasCapabilities(Object p0) { return false; }
    public boolean isEnabled() { return false; }
    public boolean supportsUriScheme(Object p0) { return false; }
    public Object toBuilder() { return null; }
    public void writeToParcel(Object p0, Object p1) {}
}
