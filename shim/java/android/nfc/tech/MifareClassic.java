package android.nfc.tech;
import android.nfc.Tag;
import android.nfc.Tag;

import android.nfc.Tag;
import java.io.IOException;

/**
 * Android-compatible MifareClassic stub.
 */
public class MifareClassic implements TagTechnology {
    public static final int SIZE_1K   = 1024;
    public static final int SIZE_2K   = 2048;
    public static final int SIZE_4K   = 4096;
    public static final int SIZE_MINI = 320;

    public static final int TYPE_CLASSIC = 0;
    public static final int TYPE_PLUS    = 1;
    public static final int TYPE_PRO     = 2;
    public static final int TYPE_UNKNOWN = -1;

    public static final byte[] KEY_DEFAULT = {
        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF
    };
    public static final byte[] KEY_MIFARE_APPLICATION_DIRECTORY = {
        (byte)0xA0, (byte)0xA1, (byte)0xA2, (byte)0xA3, (byte)0xA4, (byte)0xA5
    };
    public static final byte[] KEY_NFC_FORUM = {
        (byte)0xD3, (byte)0xF7, (byte)0xD3, (byte)0xF7, (byte)0xD3, (byte)0xF7
    };

    private final Tag mTag;
    private boolean mConnected;

    private MifareClassic(Tag tag) { mTag = tag; }

    /** Returns a MifareClassic instance for the given Tag, or null if unsupported. */
    public static MifareClassic get(Tag tag) {
        if (tag == null) return null;
        return new MifareClassic(tag);
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

    public boolean authenticateSectorWithKeyA(int sectorIndex, byte[] key) throws IOException {
        checkConnected();
        return false;
    }

    public boolean authenticateSectorWithKeyB(int sectorIndex, byte[] key) throws IOException {
        checkConnected();
        return false;
    }

    public byte[] readBlock(int blockIndex) throws IOException {
        checkConnected();
        return new byte[16];
    }

    public void writeBlock(int blockIndex, byte[] data) throws IOException {
        checkConnected();
    }

    public int getSectorCount() { return 16; }

    public int getBlockCount() { return 64; }

    public int getBlockCountInSector(int sectorIndex) {
        return (sectorIndex < 32) ? 4 : 16;
    }

    public int sectorToBlock(int sectorIndex) {
        if (sectorIndex < 32) return sectorIndex * 4;
        return 32 * 4 + (sectorIndex - 32) * 16;
    }

    public int getSize() { return SIZE_1K; }

    public int getType() { return TYPE_CLASSIC; }

    public int getTimeout() { return 300; }

    public void setTimeout(int timeoutMs) {}

    private void checkConnected() throws IOException {
        if (!mConnected) throw new IOException("Not connected");
    }
}
