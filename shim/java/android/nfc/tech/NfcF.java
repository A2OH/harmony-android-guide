package android.nfc.tech;

import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible NfcF (JIS 6319-4 / FeliCa) stub.
 */
public class NfcF implements TagTechnology {
    private final Tag mTag;
    private boolean mConnected;

    private NfcF(Tag tag) { mTag = tag; }

    /** Returns an NfcF instance for the given Tag, or null if unsupported. */
    public static NfcF get(Tag tag) {
        if (tag == null) return null;
        return new NfcF(tag);
    }

    @Override
    public void connect() throws IOException {
        mConnected = true;
    }

    @Override
    public void close() throws IOException {
        mConnected = false;
    }

    @Override
    public boolean isConnected() { return mConnected; }

    @Override
    public Tag getTag() { return mTag; }

    public byte[] transceive(byte[] data) throws IOException {
        checkConnected();
        return new byte[0];
    }

    /** Returns the 2-byte System Code from the tag. */
    public byte[] getSystemCode() { return new byte[2]; }

    /** Returns the 2-byte Manufacturer code from the tag. */
    public byte[] getManufacturer() { return new byte[2]; }

    public int getTimeout() { return 300; }

    public void setTimeout(int timeoutMs) {}

    public int getMaxTransceiveLength() { return 253; }

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
