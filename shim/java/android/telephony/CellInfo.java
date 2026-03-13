package android.telephony;

/**
 * Android-compatible CellInfo shim. Stub implementation for mock testing.
 */
public abstract class CellInfo {

    public abstract static class CellIdentity {
        public String getOperatorAlphaLong() { return ""; }
        public String getOperatorAlphaShort() { return ""; }
    }

    public abstract static class CellSignalStrength {
        public abstract int getDbm();
        public abstract int getAsuLevel();
        public abstract int getLevel();
    }

    public boolean isRegistered() {
        return false;
    }

    public abstract CellIdentity getCellIdentity();

    public abstract CellSignalStrength getCellSignalStrength();

    public long getTimeStamp() {
        return 0L;
    }
}
