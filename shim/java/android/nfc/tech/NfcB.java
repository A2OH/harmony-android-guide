package android.nfc.tech;

import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible NfcB stub.
 */
public class NfcB implements TagTechnology {
    private final Tag mTag;
    private boolean mConnected;

    private NfcB(Tag tag) { mTag = tag; }

    /** Returns an NfcB instance for the given Tag, or null if unsupported. */
    public static NfcB get(Tag tag) {
        if (tag == null) return null;
        return new NfcB(tag);
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

    /** Returns the Application Data bytes from the ATQB response. */
    public byte[] getApplicationData() { return new byte[4]; }

    /** Returns the Protocol Info bytes from the ATQB response. */
    public byte[] getProtocolInfo() { return new byte[3]; }

    public int getMaxTransceiveLength() { return 253; }

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
