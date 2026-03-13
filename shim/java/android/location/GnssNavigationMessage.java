package android.location;

/**
 * Android-compatible GnssNavigationMessage shim. Stub — returns default message fields.
 */
public class GnssNavigationMessage {

    public static final int TYPE_GPS_L1CA   = 0x0101;
    public static final int TYPE_GPS_L2CNAV = 0x0102;
    public static final int TYPE_GPS_L5CNAV = 0x0103;

    private int    mType;
    private int    mSvid;
    private int    mMessageId;
    private int    mSubmessageId;
    private byte[] mData = new byte[0];
    private int    mStatus;

    public GnssNavigationMessage() {}

    public int    getType()         { return mType; }
    public void   setType(int v)    { mType = v; }

    public int    getSvid()         { return mSvid; }
    public void   setSvid(int v)    { mSvid = v; }

    public int    getMessageId()    { return mMessageId; }
    public void   setMessageId(int v) { mMessageId = v; }

    public int    getSubmessageId() { return mSubmessageId; }
    public void   setSubmessageId(int v) { mSubmessageId = v; }

    public byte[] getData()         { return mData; }
    public void   setData(byte[] v) { mData = v != null ? v : new byte[0]; }

    public int    getStatus()       { return mStatus; }
    public void   setStatus(int v)  { mStatus = v; }

    /** Object registered with LocationManager to receive navigation messages. */
    public static abstract class Object {
        public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {}
        public void onStatusChanged(int status) {}
    }
}
