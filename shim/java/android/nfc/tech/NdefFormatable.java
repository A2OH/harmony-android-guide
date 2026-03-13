package android.nfc.tech;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.NdefMessage;
import android.nfc.Tag;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible NdefFormatable stub.
 */
public class NdefFormatable implements TagTechnology {
    private final Tag mTag;
    private boolean mConnected;

    private NdefFormatable(Tag tag) { mTag = tag; }

    /** Returns an NdefFormatable instance for the given Tag, or null if unsupported. */
    public static NdefFormatable get(Tag tag) {
        if (tag == null) return null;
        return new NdefFormatable(tag);
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

    public void format(NdefMessage firstMessage) throws IOException {
        checkConnected();
    }

    public void formatReadOnly(NdefMessage firstMessage) throws IOException {
        checkConnected();
    }

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
