package android.nfc;

/**
 * Android-compatible NdefMessage stub.
 */
public class NdefMessage {
    private final NdefRecord[] mRecords;

    public NdefMessage(byte[] data) {
        mRecords = new NdefRecord[0];
    }

    public NdefMessage(NdefRecord[] records) {
        mRecords = (records != null) ? records : new NdefRecord[0];
    }

    public NdefRecord[] getRecords() { return mRecords; }

    public byte[] toByteArray() { return new byte[0]; }

    public int getByteArrayLength() { return 0; }
}
