package android.nfc.tech;
import android.nfc.Tag;
import android.nfc.Tag;

public final class NfcV implements TagTechnology {
    public NfcV() {}

    public void close() {}
    public void connect() {}
    public static NfcV get(Tag p0) { return null; }
    public byte getDsfId() { return 0; }
    public int getMaxTransceiveLength() { return 0; }
    public byte getResponseFlags() { return 0; }
    public Tag getTag() { return null; }
    public boolean isConnected() { return false; }
    public byte[] transceive(byte[] p0) { return new byte[0]; }
}
