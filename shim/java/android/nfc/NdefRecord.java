package android.nfc;

/**
 * Android-compatible NdefRecord stub.
 */
public class NdefRecord {
    public static final short TNF_EMPTY = 0;
    public static final short TNF_WELL_KNOWN = 1;
    public static final short TNF_MIME_MEDIA = 2;
    public static final short TNF_ABSOLUTE_URI = 3;
    public static final short TNF_EXTERNAL_TYPE = 4;
    public static final short TNF_UNKNOWN = 5;

    private final short mTnf;
    private final byte[] mType;
    private final byte[] mId;
    private final byte[] mPayload;

    public NdefRecord(short tnf, byte[] type, byte[] id, byte[] payload) {
        mTnf = tnf;
        mType = (type != null) ? type : new byte[0];
        mId = (id != null) ? id : new byte[0];
        mPayload = (payload != null) ? payload : new byte[0];
    }

    public static NdefRecord createUri(String uriString) {
        byte[] payload = (uriString != null) ? uriString.getBytes() : new byte[0];
        return new NdefRecord(TNF_ABSOLUTE_URI, new byte[0], new byte[0], payload);
    }

    public static NdefRecord createMime(String mimeType, byte[] mimeData) {
        byte[] type = (mimeType != null) ? mimeType.getBytes() : new byte[0];
        byte[] payload = (mimeData != null) ? mimeData : new byte[0];
        return new NdefRecord(TNF_MIME_MEDIA, type, new byte[0], payload);
    }

    public static NdefRecord createExternal(String domain, String type, byte[] data) {
        String extType = ((domain != null) ? domain : "") + ":" + ((type != null) ? type : "");
        byte[] payload = (data != null) ? data : new byte[0];
        return new NdefRecord(TNF_EXTERNAL_TYPE, extType.getBytes(), new byte[0], payload);
    }

    public short getTnf() { return mTnf; }
    public byte[] getType() { return mType; }
    public byte[] getPayload() { return mPayload; }
    public byte[] getId() { return mId; }
}
