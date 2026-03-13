package android.telephony;

public class SmsMessage {
    public SmsMessage() {}

    public static final int ENCODING_16BIT = 0;
    public static final int ENCODING_7BIT = 0;
    public static final int ENCODING_8BIT = 0;
    public static final int ENCODING_UNKNOWN = 0;
    public static final int FORMAT_3GPP = 0;
    public static final int FORMAT_3GPP2 = 0;
    public static final int MAX_USER_DATA_BYTES = 0;
    public static final int MAX_USER_DATA_BYTES_WITH_HEADER = 0;
    public static final int MAX_USER_DATA_SEPTETS = 0;
    public static final int MAX_USER_DATA_SEPTETS_WITH_HEADER = 0;
    public static int calculateLength(Object p0, Object p1) { return 0; }
    public static Object createFromPdu(Object p0, Object p1) { return null; }
    public Object getDisplayMessageBody() { return null; }
    public Object getDisplayOriginatingAddress() { return null; }
    public Object getEmailBody() { return null; }
    public Object getEmailFrom() { return null; }
    public int getIndexOnIcc() { return 0; }
    public Object getMessageBody() { return null; }
    public Object getMessageClass() { return null; }
    public byte getPdu() { return 0; }
    public int getProtocolIdentifier() { return 0; }
    public Object getPseudoSubject() { return null; }
    public Object getServiceCenterAddress() { return null; }
    public int getStatus() { return 0; }
    public int getStatusOnIcc() { return 0; }
    public static Object getSubmitPdu(Object p0, Object p1, Object p2, Object p3) { return null; }
    public static Object getSubmitPdu(Object p0, Object p1, Object p2, Object p3, Object p4) { return null; }
    public static int getTPLayerLengthForPDU(Object p0) { return 0; }
    public long getTimestampMillis() { return 0L; }
    public byte getUserData() { return 0; }
    public boolean isCphsMwiMessage() { return false; }
    public boolean isEmail() { return false; }
    public boolean isMWIClearMessage() { return false; }
    public boolean isMWISetMessage() { return false; }
    public boolean isMwiDontStore() { return false; }
    public boolean isReplace() { return false; }
    public boolean isReplyPathPresent() { return false; }
    public boolean isStatusReportMessage() { return false; }
}
