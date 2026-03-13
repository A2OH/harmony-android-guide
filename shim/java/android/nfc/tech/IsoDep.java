package android.nfc.tech;

import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible IsoDep stub.
 */
public class IsoDep implements TagTechnology {
    private final Tag mTag;
    private boolean mConnected;

    private IsoDep(Tag tag) { mTag = tag; }

    /** Returns an IsoDep instance for the given Tag, or null if unsupported. */
    public static IsoDep get(Tag tag) {
        if (tag == null) return null;
        return new IsoDep(tag);
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

    public byte[] getHistoricalBytes() { return new byte[0]; }

    public byte[] getHiLayerResponse() { return null; }

    public int getTimeout() { return 300; }

    public void setTimeout(int timeoutMs) {}

    public int getMaxTransceiveLength() { return 65279; }

    public boolean isExtendedLengthApduSupported() { return false; }

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
