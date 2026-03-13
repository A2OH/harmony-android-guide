package android.nfc.tech;

import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible MifareUltralight stub.
 */
public class MifareUltralight implements TagTechnology {
    public static final int TYPE_ULTRALIGHT   = 1;
    public static final int TYPE_ULTRALIGHT_C = 2;
    public static final int TYPE_UNKNOWN      = -1;

    private final Tag mTag;
    private boolean mConnected;

    private MifareUltralight(Tag tag) { mTag = tag; }

    /** Returns a MifareUltralight instance for the given Tag, or null if unsupported. */
    public static MifareUltralight get(Tag tag) {
        if (tag == null) return null;
        return new MifareUltralight(tag);
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

    /**
     * Reads 4 pages (16 bytes) starting at pageOffset.
     */
    public byte[] readPages(int pageOffset) throws IOException {
        checkConnected();
        return new byte[16];
    }

    public void writePage(int pageOffset, byte[] data) throws IOException {
        checkConnected();
    }

    public int getType() { return TYPE_ULTRALIGHT; }

    public int getTimeout() { return 300; }

    public void setTimeout(int timeoutMs) {}

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
