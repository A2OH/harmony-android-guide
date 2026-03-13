package android.telephony;

/**
 * Android-compatible SmsMessage shim. Stub implementation for mock testing.
 * Represents a single SMS/MMS message received or read from the ICC.
 */
public class SmsMessage {

    // -------------------------------------------------------------------------
    // Status constants (ICC storage status)
    // -------------------------------------------------------------------------

    /** Status for a message that has been read from the ICC. */
    public static final int STATUS_ON_ICC_READ = 1;
    /** Status for a message that has not yet been read from the ICC. */
    public static final int STATUS_ON_ICC_UNREAD = 3;
    /** Status for a sent message stored on the ICC. */
    public static final int STATUS_ON_ICC_SENT = 5;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private String mOriginatingAddress;
    private String mMessageBody;
    private long mTimestampMillis;
    private int mStatus;
    private byte[] mPdu;

    private SmsMessage() {}

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    /**
     * Creates an SmsMessage from a raw PDU byte array.
     * In the shim this produces a stub object with empty fields; a non-null
     * pdu is required to return a non-null result.
     *
     * @param pdu    the raw PDU bytes
     * @param format PDU format string (ignored in shim)
     * @return an SmsMessage, or null if pdu is null
     */
    public static SmsMessage createFromPdu(byte[] pdu, String format) {
        if (pdu == null) return null;
        SmsMessage msg = new SmsMessage();
        msg.mPdu = pdu;
        msg.mTimestampMillis = System.currentTimeMillis();
        msg.mStatus = STATUS_ON_ICC_UNREAD;
        return msg;
    }

    /**
     * Creates an SmsMessage from a raw PDU byte array using the default format.
     *
     * @param pdu the raw PDU bytes
     * @return an SmsMessage, or null if pdu is null
     */
    public static SmsMessage createFromPdu(byte[] pdu) {
        return createFromPdu(pdu, "3gpp");
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the originating address (sender phone number) of this message,
     * or null if unknown.
     */
    public String getDisplayOriginatingAddress() {
        return mOriginatingAddress;
    }

    /**
     * Returns the originating address (same as display version in shim).
     */
    public String getOriginatingAddress() {
        return mOriginatingAddress;
    }

    /** Returns the body text of this message, or null if not a text message. */
    public String getMessageBody() {
        return mMessageBody;
    }

    /**
     * Returns the service centre timestamp of this message in milliseconds
     * since the Unix epoch.
     */
    public long getTimestampMillis() {
        return mTimestampMillis;
    }

    /** Returns the status of this message (one of the STATUS_ON_ICC_* constants). */
    public int getStatus() {
        return mStatus;
    }

    /** Returns the raw PDU bytes, or null if not constructed from PDU. */
    public byte[] getPdu() {
        return mPdu;
    }

    // -------------------------------------------------------------------------
    // Shim-internal setters (for test construction)
    // -------------------------------------------------------------------------

    /** Sets the originating address (for shim simulation). */
    public void setOriginatingAddress(String address) {
        mOriginatingAddress = address;
    }

    /** Sets the message body text (for shim simulation). */
    public void setMessageBody(String body) {
        mMessageBody = body;
    }

    /** Sets the timestamp in milliseconds (for shim simulation). */
    public void setTimestampMillis(long millis) {
        mTimestampMillis = millis;
    }

    /** Sets the ICC status (for shim simulation). */
    public void setStatus(int status) {
        mStatus = status;
    }

    @Override
    public String toString() {
        return "SmsMessage{from=" + mOriginatingAddress
                + ", body=" + (mMessageBody != null
                        ? mMessageBody.substring(0, Math.min(mMessageBody.length(), 30))
                        : "null")
                + ", ts=" + mTimestampMillis + "}";
    }
}
