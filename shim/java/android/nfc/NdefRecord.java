package android.nfc;

import java.util.Arrays;

/**
 * Android-compatible NdefRecord shim. Stub for A2OH migration.
 */
public final class NdefRecord {

    /** Type Name Format: Empty */
    public static final short TNF_EMPTY         = 0x00;
    /** Type Name Format: Well-Known */
    public static final short TNF_WELL_KNOWN    = 0x01;
    /** Type Name Format: MIME Media */
    public static final short TNF_MIME_MEDIA    = 0x02;
    /** Type Name Format: Absolute URI */
    public static final short TNF_ABSOLUTE_URI  = 0x03;
    /** Type Name Format: External Type */
    public static final short TNF_EXTERNAL_TYPE = 0x04;

    /** RTD Text type */
    public static final byte[] RTD_TEXT = { 0x54 };
    /** RTD URI type */
    public static final byte[] RTD_URI  = { 0x55 };

    private final short  mTnf;
    private final byte[] mType;
    private final byte[] mId;
    private final byte[] mPayload;

    public NdefRecord(short tnf, byte[] type, byte[] id, byte[] payload) {
        mTnf     = tnf;
        mType    = type    != null ? Arrays.copyOf(type,    type.length)    : new byte[0];
        mId      = id      != null ? Arrays.copyOf(id,      id.length)      : new byte[0];
        mPayload = payload != null ? Arrays.copyOf(payload, payload.length) : new byte[0];
    }

    public short  getTnf()     { return mTnf; }
    public byte[] getType()    { return Arrays.copyOf(mType,    mType.length); }
    public byte[] getId()      { return Arrays.copyOf(mId,      mId.length); }
    public byte[] getPayload() { return Arrays.copyOf(mPayload, mPayload.length); }

    public byte[] toByteArray() {
        // Minimal NDEF record serialisation (short record, no chunk)
        int payloadLen = mPayload.length;
        boolean shortRecord = payloadLen < 256;
        int len = 1 /* header */ + 1 /* type length */ +
                  (shortRecord ? 1 : 4) /* payload length */ +
                  (mId.length > 0 ? 1 + mId.length : 0) +
                  mType.length + payloadLen;
        byte[] out = new byte[len];
        int pos = 0;
        byte header = (byte)(mTnf & 0x07);
        if (shortRecord)   header |= 0x10; // SR flag
        if (mId.length > 0) header |= 0x08; // IL flag
        out[pos++] = header;
        out[pos++] = (byte) mType.length;
        if (shortRecord) {
            out[pos++] = (byte) payloadLen;
        } else {
            out[pos++] = (byte)((payloadLen >> 24) & 0xFF);
            out[pos++] = (byte)((payloadLen >> 16) & 0xFF);
            out[pos++] = (byte)((payloadLen >>  8) & 0xFF);
            out[pos++] = (byte)( payloadLen        & 0xFF);
        }
        if (mId.length > 0) {
            out[pos++] = (byte) mId.length;
        }
        System.arraycopy(mType,    0, out, pos, mType.length);    pos += mType.length;
        if (mId.length > 0) {
            System.arraycopy(mId, 0, out, pos, mId.length);       pos += mId.length;
        }
        System.arraycopy(mPayload, 0, out, pos, mPayload.length);
        return out;
    }

    // -----------------------------------------------------------------------
    // Static factory helpers
    // -----------------------------------------------------------------------

    public static NdefRecord createUri(String uriString) {
        if (uriString == null) throw new IllegalArgumentException("uriString is null");
        byte[] uriBytes = uriString.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] payload  = new byte[1 + uriBytes.length];
        payload[0] = 0x00; // no URI abbreviation
        System.arraycopy(uriBytes, 0, payload, 1, uriBytes.length);
        return new NdefRecord(TNF_WELL_KNOWN, RTD_URI, new byte[0], payload);
    }

    public static NdefRecord createMime(String mimeType, byte[] mimeData) {
        if (mimeType == null) throw new IllegalArgumentException("mimeType is null");
        byte[] typeBytes = mimeType.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        return new NdefRecord(TNF_MIME_MEDIA, typeBytes, new byte[0],
                mimeData != null ? mimeData : new byte[0]);
    }

    public static NdefRecord createTextRecord(String languageCode, String text) {
        if (text == null) throw new IllegalArgumentException("text is null");
        String lang = languageCode != null ? languageCode : "en";
        byte[] langBytes = lang.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        byte[] textBytes = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] payload   = new byte[1 + langBytes.length + textBytes.length];
        payload[0] = (byte)(langBytes.length & 0x3F); // UTF-8, lang length
        System.arraycopy(langBytes, 0, payload, 1,                  langBytes.length);
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(TNF_WELL_KNOWN, RTD_TEXT, new byte[0], payload);
    }

    public static NdefRecord createApplicationRecord(String packageName) {
        if (packageName == null) throw new IllegalArgumentException("packageName is null");
        byte[] pkgBytes = packageName.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] type     = "android.com:pkg".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        return new NdefRecord(TNF_EXTERNAL_TYPE, type, new byte[0], pkgBytes);
    }

    @Override
    public String toString() {
        return "NdefRecord[tnf=" + mTnf + " typeLen=" + mType.length +
               " payloadLen=" + mPayload.length + "]";
    }
}
