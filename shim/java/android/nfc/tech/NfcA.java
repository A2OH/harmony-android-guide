package android.nfc.tech;

import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible NfcA stub.
 */
public class NfcA implements TagTechnology {
    private final Tag mTag;
    private boolean mConnected;

    private NfcA(Tag tag) { mTag = tag; }

    /** Returns an NfcA instance for the given Tag, or null if unsupported. */
    public static NfcA get(Tag tag) {
        if (tag == null) return null;
        return new NfcA(tag);
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

    /** Returns the ATQA (Answer To Request, Type A) bytes. */
    public byte[] getAtqa() { return new byte[2]; }

    /** Returns the SAK (Select Acknowledge) byte. */
    public short getSak() { return 0; }

    public int getTimeout() { return 300; }

    public void setTimeout(int timeoutMs) {}

    public int getMaxTransceiveLength() { return 253; }

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
