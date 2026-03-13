package android.nfc;

/**
 * Android-compatible NfcAdapter stub.
 */
public class NfcAdapter {
    public static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED";
    public static final String ACTION_TAG_DISCOVERED = "android.nfc.action.TAG_DISCOVERED";
    public static final String ACTION_TECH_DISCOVERED = "android.nfc.action.TECH_DISCOVERED";
    public static final String EXTRA_TAG = "android.nfc.extra.TAG";
    public static final String EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES";

    public static NfcAdapter getDefaultAdapter(Object context) {
        return null;
    }

    public boolean isEnabled() { return false; }

    public void enableForegroundDispatch(Object activity, Object intent,
            Object[] filters, Object[][] techLists) {
        /* no-op */
    }

    public void disableForegroundDispatch(Object activity) {
        /* no-op */
    }

    public void setNdefPushMessage(Object message, Object activity, Object... activities) {
        /* no-op */
    }
}
