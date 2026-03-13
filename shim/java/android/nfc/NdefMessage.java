package android.nfc;

import java.util.Arrays;

/**
 * Android-compatible NdefMessage shim. Stub for A2OH migration.
 */
public final class NdefMessage {

    private final NdefRecord[] mRecords;

    public NdefMessage(NdefRecord[] records) {
        if (records == null) throw new IllegalArgumentException("records is null");
        mRecords = Arrays.copyOf(records, records.length);
    }

    /**
     * Construct from a raw byte array (minimal parse — stores one
     * raw-payload record; full TLV parsing not required for shim).
     */
    public NdefMessage(byte[] data) {
        if (data == null) throw new IllegalArgumentException("data is null");
        // Wrap raw bytes in a single MIME record for compatibility
        mRecords = new NdefRecord[] {
            new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                    "application/octet-stream"
                            .getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                    new byte[0],
                    Arrays.copyOf(data, data.length))
        };
    }

    public NdefRecord[] getRecords() {
        return Arrays.copyOf(mRecords, mRecords.length);
    }

    public byte[] toByteArray() {
        // Concatenate serialised records
        byte[][] serialised = new byte[mRecords.length][];
        int total = 0;
        for (int i = 0; i < mRecords.length; i++) {
            serialised[i] = mRecords[i].toByteArray();
            total += serialised[i].length;
        }
        byte[] out = new byte[total];
        int pos = 0;
        for (byte[] r : serialised) {
            System.arraycopy(r, 0, out, pos, r.length);
            pos += r.length;
        }
        return out;
    }

    public int getByteArrayLength() {
        return toByteArray().length;
    }

    @Override
    public String toString() {
        return "NdefMessage[records=" + mRecords.length + "]";
    }
}
