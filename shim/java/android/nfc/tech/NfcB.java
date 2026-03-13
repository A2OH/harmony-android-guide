package android.nfc.tech;
import android.nfc.Tag;

public final class NfcB implements TagTechnology {
    public NfcB() {}

    public void close() {}
    public void connect() {}
    public static NfcB get(Tag p0) { return null; }
    public byte[] getApplicationData() { return new byte[0]; }
    public int getMaxTransceiveLength() { return 0; }
    public byte[] getProtocolInfo() { return new byte[0]; }
    public Tag getTag() { return null; }
    public boolean isConnected() { return false; }
    public byte[] transceive(byte[] p0) { return new byte[0]; }
}
