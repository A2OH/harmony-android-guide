package android.telephony;

/**
 * Android-compatible SignalStrength shim. Stub implementation for mock testing.
 */
public class SignalStrength {

    public int getGsmSignalStrength() {
        return 99;
    }

    public int getLevel() {
        return 0;
    }

    public int getDbm() {
        return Integer.MAX_VALUE;
    }

    public int getAsuLevel() {
        return 0;
    }

    public boolean isGsm() {
        return false;
    }

    public int getCdmaDbm() {
        return Integer.MAX_VALUE;
    }

    public int getEvdoDbm() {
        return Integer.MAX_VALUE;
    }
}
