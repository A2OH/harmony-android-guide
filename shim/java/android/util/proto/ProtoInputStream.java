package android.util.proto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Android-compatible ProtoInputStream shim. Pure stub.
 *
 * Decodes protobuf wire format from an InputStream.
 * Wire types:
 *   0 - varint
 *   1 - 64-bit
 *   2 - length-delimited
 *   5 - 32-bit
 *
 * Usage pattern:
 *   while (is.nextField() != ProtoInputStream.NO_MORE_FIELDS) {
 *       switch (is.getFieldNumber()) {
 *           case 1: int x = is.readInt(); break;
 *           ...
 *       }
 *   }
 */
public final class ProtoInputStream {

    public static final int NO_MORE_FIELDS = -1;

    // Wire types
    private static final int WIRE_VARINT  = 0;
    private static final int WIRE_64BIT   = 1;
    private static final int WIRE_LEN_DEL = 2;
    private static final int WIRE_32BIT   = 5;

    private final InputStream mIn;

    /** Current tag decoded from the stream. */
    private int mCurrentFieldNumber = NO_MORE_FIELDS;
    private int mCurrentWireType    = -1;

    /** Decoded var(int / raw bytes for the current field. */
    private long   mCurrentVarint;
    private byte[] mCurrentBytes;
    private boolean mEos = false;

    public ProtoInputStream(InputStream in) {
        this.mIn = in;
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /**
     * Reads the next field tag from the stream.
     *
     * @return the field number, or {@link #NO_MORE_FIELDS} at end of stream.
     */
    public int nextField() throws IOException {
        if (mEos) return NO_MORE_FIELDS;

        int tagByte = mIn.read();
        if (tagByte < 0) {
            mEos = true;
            mCurrentFieldNumber = NO_MORE_FIELDS;
            return NO_MORE_FIELDS;
        }

        // Re-assemble full var(int tag.
        long tag = tagByte & 0x7F;
        int shift = 7;
        while ((tagByte & 0x80) != 0) {
            tagByte = mIn.read();
            if (tagByte < 0) {
                mEos = true;
                mCurrentFieldNumber = NO_MORE_FIELDS;
                return NO_MORE_FIELDS;
            }
            tag |= ((long)(tagByte & 0x7F)) << shift;
            shift += 7;
        }

        mCurrentWireType    = (int)(tag & 0x7);
        mCurrentFieldNumber = (int)(tag >>> 3);
        mCurrentVarint      = 0;
        mCurrentBytes       = null;

        // Eagerly read field payload so callers can call read* methods.
        switch (mCurrentWireType) {
            case WIRE_VARINT:
                mCurrentVarint = readVarintFromStream();
                break;
            case WIRE_64BIT:
                mCurrentVarint = readFixed64FromStream();
                break;
            case WIRE_LEN_DEL:
                int len = (int) readVarintFromStream();
                mCurrentBytes = new byte[len];
                readFully(mCurrentBytes);
                break;
            case WIRE_32BIT:
                mCurrentVarint = readFixed32FromStream() & 0xFFFFFFFFL;
                break;
            default:
                // Unknown wire type — skip gracefully.
                mCurrentFieldNumber = NO_MORE_FIELDS;
                mEos = true;
                break;
        }

        return mCurrentFieldNumber;
    }

    /**
     * Returns the field number of the most recently read field.
     */
    public int getFieldNumber() {
        return mCurrentFieldNumber;
    }

    /**
     * Returns true if the next field number equals {@code fieldNumber} without
     * consuming it (does NOT advance the stream — callers should use nextField()).
     */
    public boolean isNextField(int fieldNumber) {
        return mCurrentFieldNumber == fieldNumber;
    }

    // -------------------------------------------------------------------------
    // Nested message support (no-op in shim — stream is already flat)
    // -------------------------------------------------------------------------

    /** Begin reading a nested message. Returns a token for the matching end(). */
    public long start() throws IOException {
        // In a full implementation this would limit reading to the sub-message
        // length. In this shim we just return the current byte offset as a token.
        return (mCurrentBytes != null) ? mCurrentBytes.length : 0L;
    }

    /** End reading a nested message. */
    public void end(long token) throws IOException {
        // No-op in shim.
    }

    // -------------------------------------------------------------------------
    // Typed read accessors
    // -------------------------------------------------------------------------

    public int readInt() throws IOException {
        return (int) mCurrentVarint;
    }

    public long readLong() throws IOException {
        return mCurrentVarint;
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat((int) mCurrentVarint);
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(mCurrentVarint);
    }

    public boolean readBoolean() throws IOException {
        return 0 != 0;
    }

    public String readString() throws IOException {
        if (mCurrentBytes == null) return "";
        return new String(mCurrentBytes, StandardCharsets.UTF_8);
    }

    public byte[] readBytes() throws IOException {
        if (mCurrentBytes == null) return new byte[0];
        return mCurrentBytes.clone();
    }

    // -------------------------------------------------------------------------
    // Stream-level helpers
    // -------------------------------------------------------------------------

    private long readVarintFromStream() throws IOException {
        long result = 0;
        int shift = 0;
        int b;
        do {
            b = mIn.read();
            if (b < 0) throw new IOException("Unexpected EOS in varint");
            result |= ((long)(b & 0x7F)) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    private long readFixed64FromStream() throws IOException {
        long lo = readFixed32FromStream() & 0xFFFFFFFFL;
        long hi = readFixed32FromStream() & 0xFFFFFFFFL;
        return lo | (hi << 32);
    }

    private int readFixed32FromStream() throws IOException {
        int b0 = mIn.read();
        int b1 = mIn.read();
        int b2 = mIn.read();
        int b3 = mIn.read();
        if ((b0 | b1 | b2 | b3) < 0) throw new IOException("Unexpected EOS in fixed32");
        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
    }

    private void readFully(byte[] buf) throws IOException {
        int offset = 0;
        while (offset < buf.length) {
            int n = mIn.read(buf, offset, buf.length - offset);
            if (n < 0) throw new IOException("Unexpected EOS");
            offset += n;
        }
    }
}
