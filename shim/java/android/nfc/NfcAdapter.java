package android.nfc;

/**
 * Android-compatible NfcAdapter shim. Stub for A2OH migration.
 */
public final class NfcAdapter {

    // -----------------------------------------------------------------------
    // Intent action constants
    // -----------------------------------------------------------------------
    public static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED";
    public static final String ACTION_TAG_DISCOVERED  = "android.nfc.action.TAG_DISCOVERED";
    public static final String ACTION_TECH_DISCOVERED = "android.nfc.action.TECH_DISCOVERED";

    // -----------------------------------------------------------------------
    // Callback interfaces
    // -----------------------------------------------------------------------

    public interface CreateNdefMessageCallback {
        NdefMessage createNdefMessage(Object event);
    }

    public interface CreateBeamUrisCallback {
        java.net.URI[] createBeamUris(Object event);
    }

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    private static NfcAdapter sDefaultAdapter;

    private NfcAdapter() {}

    /**
     * Returns the default NFC adapter for this device, or {@code null} if NFC
     * is not available (which is always the case in this shim layer).
     */
    public static NfcAdapter getDefaultAdapter(Object context) {
        // In the shim environment NFC hardware is not present — return null
        // exactly as the real Android API does when NFC is unavailable.
        return null;
    }

    // -----------------------------------------------------------------------
    // Instance methods — only reached if a non-null adapter is somehow used
    // -----------------------------------------------------------------------

    public boolean isEnabled() {
        return false;
    }

    public void enableForegroundDispatch(Object activity, Object pendingIntent,
            Object[] filters, String[][] techLists) {
        // no-op
    }

    public void disableForegroundDispatch(Object activity) {
        // no-op
    }

    public void enableForegroundNdefPush(Object activity, NdefMessage message) {
        // no-op
    }

    public void disableForegroundNdefPush(Object activity) {
        // no-op
    }

    @Override
    public String toString() {
        return "NfcAdapter[shim/unavailable]";
    }
}
