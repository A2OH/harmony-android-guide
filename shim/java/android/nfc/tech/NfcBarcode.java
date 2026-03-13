package android.nfc.tech;
import android.nfc.Tag;
import android.nfc.Tag;

public final class NfcBarcode implements TagTechnology {
    public static final int TYPE_KOVIO = 0;
    public static final int TYPE_UNKNOWN = 0;

    public NfcBarcode() {}

    public void close() {}
    public void connect() {}
    public static NfcBarcode get(Tag p0) { return null; }
    public byte[] getBarcode() { return new byte[0]; }
    public Tag getTag() { return null; }
    public int getType() { return 0; }
    public boolean isConnected() { return false; }
}
