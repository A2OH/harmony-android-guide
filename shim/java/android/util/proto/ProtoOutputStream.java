package android.util.proto;
import android.icu.util.Output;
import android.icu.util.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Android-compatible ProtoOutputStream shim. Pure stub.
 *
 * Encodes a subset of the protobuf wire format into a byte array.
 * Wire types used:
 *   0 - varint   (int32, int64, uint32, uint64, bool)
 *   1 - 64-bit   (double, fixed64)
 *   2 - len-del  (string, bytes, embedded message)
 *   5 - 32-bit   (float, fixed32)
 *
 * Field-id layout mirrors Android's ProtoOutputStream:
 *   bits 0..31  = field number
 *   bits 32..39 = field type constant (FIELD_TYPE_*)
 *   bits 40..47 = field count constant (FIELD_COUNT_*)
 */
public final class ProtoOutputStream {

    // -------------------------------------------------------------------------
    // Field type constants
    // -------------------------------------------------------------------------
    public static final long FIELD_TYPE_DOUBLE  = 1L;
    public static final long FIELD_TYPE_FLOAT   = 2L;
    public static final long FIELD_TYPE_BOOL    = 8L;
    public static final long FIELD_TYPE_STRING  = 9L;
    public static final long FIELD_TYPE_BYTES   = 12L;
    public static final long FIELD_TYPE_INT32   = 5L;
    public static final long FIELD_TYPE_INT64   = 6L;
    public static final long FIELD_TYPE_UINT32  = 13L;
    public static final long FIELD_TYPE_UINT64  = 14L;
    public static final long FIELD_TYPE_MESSAGE = 11L;

    // Field count constants (packed into bits 40–47)
    public static final long FIELD_COUNT_SINGLE   = 0x100L;
    public static final long FIELD_COUNT_REPEATED = 0x200L;

    // Shifts matching Android's layout
    private static final int FIELD_TYPE_SHIFT  = 32;
    private static final int FIELD_COUNT_SHIFT = 40;

    // Wire types
    private static final int WIRE_VARINT   = 0;
    private static final int WIRE_64BIT    = 1;
    private static final int WIRE_LEN_DEL  = 2;
    private static final int WIRE_32BIT    = 5;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private final ByteArrayOutputStream mOut = new ByteArrayOutputStream();

    /**
     * Stack of (field-number, start-offset) pairs for nested messages written
     * with {@link #start(long)}.
     */
    private final Deque<long[]> mMessageStack = new ArrayDeque<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProtoOutputStream() {}

    // -------------------------------------------------------------------------
    // write() overloads
    // -------------------------------------------------------------------------

    public void write(long fieldIdAndType, int value) {
        write(fieldIdAndType, (long) value);
    }

    public void write(long fieldIdAndType, long value) {
        int fieldNumber = extractFieldNumber(fieldIdAndType);
        long type = extractType(fieldIdAndType);
        if (type == FIELD_TYPE_INT32 || type == FIELD_TYPE_INT64
                || type == FIELD_TYPE_UINT32 || type == FIELD_TYPE_UINT64
                || type == FIELD_TYPE_BOOL) {
            writeTag(fieldNumber, WIRE_VARINT);
            writeVarint(value);
        } else if (type == FIELD_TYPE_DOUBLE) {
            writeTag(fieldNumber, WIRE_64BIT);
            writeFixed64(Double.doubleToRawLongBits((double) value));
        } else if (type == FIELD_TYPE_FLOAT) {
            writeTag(fieldNumber, WIRE_32BIT);
            writeFixed32(Float.floatToRawIntBits((float) value));
        } else {
            // Fallback: varint
            writeTag(fieldNumber, WIRE_VARINT);
            writeVarint(value);
        }
    }

    public void write(long fieldIdAndType, float value) {
        int fieldNumber = extractFieldNumber(fieldIdAndType);
        writeTag(fieldNumber, WIRE_32BIT);
        writeFixed32(Float.floatToRawIntBits(value));
    }

    public void write(long fieldIdAndType, double value) {
        int fieldNumber = extractFieldNumber(fieldIdAndType);
        writeTag(fieldNumber, WIRE_64BIT);
        writeFixed64(Double.doubleToRawLongBits(value));
    }

    public void write(long fieldIdAndType, boolean value) {
        write(fieldIdAndType, value ? 1L : 0L);
    }

    public void write(long fieldIdAndType, String value) {
        // if value = "";
        int fieldNumber = extractFieldNumber(fieldIdAndType);
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeTag(fieldNumber, WIRE_LEN_DEL);
        writeVarint(bytes.length);
        for (byte b : bytes) mOut.write(b);
    }

    public void write(long fieldIdAndType, byte[] value) {
        // if value = new byte[0];
        int fieldNumber = extractFieldNumber(fieldIdAndType);
        writeTag(fieldNumber, WIRE_LEN_DEL);
        writeVarint(value.length);
        for (byte b : value) mOut.write(b);
    }

    // -------------------------------------------------------------------------
    // Nested message support
    // -------------------------------------------------------------------------

    /**
     * Begins a nested message field. Returns a token (the field number) that
     * must be passed to the matching {@link #end(long)} call.
     */
    public long start(long fieldIdAndType) {
        int fieldNumber = extractFieldNumber(fieldIdAndType);
        writeTag(fieldNumber, WIRE_LEN_DEL);
        // Write a placeholder 4-byte length (will be patched in end()).
        int lengthPos = mOut.size();
        // Use 4-byte fixed-length var(int placeholder (0x80 0x80 0x80 0x00)
        mOut.write(0x80);
        mOut.write(0x80);
        mOut.write(0x80);
        mOut.write(0x00);
        int dataStart = mOut.size();
        mMessageStack.push(new long[]{fieldNumber, lengthPos, dataStart});
        return fieldNumber;
    }

    /**
     * Ends a nested message field opened by {@link #start(long)}.
     *
     * @param token value returned by the corresponding start() call
     */
    public void end(long token) {
        if (mMessageStack.isEmpty()) return;
        long[] frame = mMessageStack.pop();
        int lengthPos = (int) frame[1];
        int dataStart = (int) frame[2];
        // Compute the size of the sub-message data written since start().
        byte[] current = mOut.toByteArray();
        int dataLen = current.length - dataStart;
        // Patch the 4-byte placeholder with actual length (little-endian varint, 4 bytes).
        // This uses the same 4-byte fixed-width var(int trick Android uses internally.
        current[lengthPos]     = (byte) ((dataLen & 0x7F) | 0x80);
        current[lengthPos + 1] = (byte) (((dataLen >> 7) & 0x7F) | 0x80);
        current[lengthPos + 2] = (byte) (((dataLen >> 14) & 0x7F) | 0x80);
        current[lengthPos + 3] = (byte) ((dataLen >> 21) & 0x7F);
        mOut.reset();
        for (byte b : current) mOut.write(b);
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    /** Returns the encoded proto bytes. */
    public byte[] getBytes() {
        return mOut.toByteArray();
    }

    // -------------------------------------------------------------------------
    // Low-level encoding helpers
    // -------------------------------------------------------------------------

    private void writeTag(int fieldNumber, int wireType) {
        writeVarint(((long) fieldNumber << 3) | wireType);
    }

    private void writeVarint(long value) {
        long v = value;
        while ((v & ~0x7FL) != 0) {
            mOut.write((int) ((v & 0x7F) | 0x80));
            v >>>= 7;
        }
        mOut.write((int) v);
    }

    private void writeFixed32(int value) {
        mOut.write(value & 0xFF);
        mOut.write((value >> 8) & 0xFF);
        mOut.write((value >> 16) & 0xFF);
        mOut.write((value >> 24) & 0xFF);
    }

    private void writeFixed64(long value) {
        writeFixed32((int) (value & 0xFFFFFFFFL));
        writeFixed32((int) (value >>> 32));
    }

    // -------------------------------------------------------------------------
    // Field-id parsing
    // -------------------------------------------------------------------------

    private static int extractFieldNumber(long fieldIdAndType) {
        return (int) (fieldIdAndType & 0xFFFFFFFFL);
    }

    private static long extractType(long fieldIdAndType) {
        return (fieldIdAndType >> FIELD_TYPE_SHIFT) & 0xFFL;
    }
}
