package android.nfc.cardemulation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Android-compatible HostApduService stub.
 * Abstract base class for Host Card Emulation (HCE) services.
 *
 * Subclasses must implement processCommandApdu() and onDeactivated().
 */
public abstract class HostApduService extends Service {
    /** Deactivation reason: the NFC link was lost. */
    public static final int DEACTIVATION_LINK_LOSS  = 0;
    /** Deactivation reason: another AID was selected. */
    public static final int DEACTIVATION_DESELECTED = 1;

    @Override
    public final IBinder onBind(Intent intent) {
        // HCE services are bound by the NFC stack, not by apps directly.
        return null;
    }

    /**
     * Called by the system when an APDU command is received from a reader.
     *
     * @param commandApdu  The APDU received from the remote device.
     * @param extras       Additional extras, may be null.
     * @return             The response APDU to be sent back, or null to send later via sendResponseApdu().
     */
    public abstract byte[] processCommandApdu(byte[] commandApdu, android.os.Bundle extras);

    /**
     * Called when the service is deactivated.
     *
     * @param reason  Either {@link #DEACTIVATION_LINK_LOSS} or {@link #DEACTIVATION_DESELECTED}.
     */
    public abstract void onDeactivated(int reason);

    /**
     * Send a response APDU back to the remote device.
     * May be called from any thread.
     *
     * @param responseApdu The response APDU bytes to send.
     */
    public final void sendResponseApdu(byte[] responseApdu) {
        // Stub: no-op in shim layer
    }
}
