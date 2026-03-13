package android.nfc.cardemulation;
import android.app.Activity;
import android.content.ComponentName;
import android.nfc.NfcAdapter;

public final class NfcFCardEmulation {
    public NfcFCardEmulation() {}

    public boolean disableService(Activity p0) { return false; }
    public boolean enableService(Activity p0, ComponentName p1) { return false; }
    public static NfcFCardEmulation getInstance(NfcAdapter p0) { return null; }
    public String getNfcid2ForService(ComponentName p0) { return null; }
    public String getSystemCodeForService(ComponentName p0) { return null; }
    public boolean registerSystemCodeForService(ComponentName p0, String p1) { return false; }
    public boolean setNfcid2ForService(ComponentName p0, String p1) { return false; }
    public boolean unregisterSystemCodeForService(ComponentName p0) { return false; }
}
