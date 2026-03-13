package android.telephony;

import java.util.List;

/**
 * Android-compatible TelephonyCallback shim (API 31+).
 * Replaces the deprecated PhoneStateListener. Stub implementation for A2OH migration.
 *
 * Register via TelephonyManager.registerTelephonyCallback().
 * Implement one or more inner listener interfaces to receive specific state changes.
 */
public class TelephonyCallback {

    /**
     * Object for call state changes.
     */
    public interface CallStateListener {
        void onCallStateChanged(int state);
    }

    /**
     * Object for data connection state changes.
     */
    public interface DataConnectionStateListener {
        void onDataConnectionStateChanged(int state, int networkType);
    }

    /**
     * Object for signal strength changes.
     */
    public interface SignalStrengthsListener {
        void onSignalStrengthsChanged(Object signalStrength);
    }

    /**
     * Object for cell info changes.
     */
    public interface CellInfoListener {
        void onCellInfoChanged(List cellInfoList);
    }

    /**
     * Object for service state changes.
     */
    public interface ServiceStateListener {
        void onServiceStateChanged(Object serviceState);
    }

    /**
     * Object for display info changes (e.g. 5G override indicators).
     */
    public interface DisplayInfoListener {
        void onDisplayInfoChanged(Object telephonyDisplayInfo);
    }
}
