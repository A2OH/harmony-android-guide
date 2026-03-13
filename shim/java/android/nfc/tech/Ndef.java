package android.nfc.tech;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.NdefMessage;
import android.nfc.Tag;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible Ndef stub.
 */
public class Ndef implements TagTechnology {
    public static final String NFC_FORUM_TYPE_1 = "org.nfcforum.ndef.type1";
    public static final String NFC_FORUM_TYPE_2 = "org.nfcforum.ndef.type2";
    public static final String NFC_FORUM_TYPE_3 = "org.nfcforum.ndef.type3";
    public static final String NFC_FORUM_TYPE_4 = "org.nfcforum.ndef.type4";
    public static final String MIFARE_CLASSIC    = "com.nxp.ndef.mifareclassic";

    private final Tag mTag;
    private boolean mConnected;
    private NdefMessage mCachedMessage;

    private Ndef(Tag tag) { mTag = tag; }

    /** Returns an Ndef instance for the given Tag, or null if unsupported. */
    public static Ndef get(Tag tag) {
        if (tag == null) return null;
        return new Ndef(tag);
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

    public NdefMessage getNdefMessage() throws IOException {
        checkConnected();
        return mCachedMessage;
    }

    public void writeNdefMessage(NdefMessage msg) throws IOException {
        checkConnected();
        mCachedMessage = msg;
    }

    public boolean makeReadOnly() throws IOException {
        checkConnected();
        return false;
    }

    public boolean canMakeReadOnly() { return false; }

    public boolean isWritable() { return true; }

    public int getMaxSize() { return 0; }

    public String getType() { return NFC_FORUM_TYPE_2; }

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
