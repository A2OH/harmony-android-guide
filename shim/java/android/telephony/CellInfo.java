package android.telephony;

/**
 * Android-compatible CellInfo shim. Stub implementation for mock testing.
 */
public class CellInfo {

    public abstract static class CellIdentity {
        public String getOperatorAlphaLong() { return ""; }
        public String getOperatorAlphaShort() { return ""; }
    }

    public abstract static class CellSignalStrength {
        public int getDbm() { return 0; }
        public int getAsuLevel() { return 0; }
        public int getLevel() { return 0; }
    }

    public boolean isRegistered() {
        return false;
    }

    public CellIdentity getCellIdentity() { return null; }

    public CellSignalStrength getCellSignalStrength() { return null; }

    public long getTimeStamp() {
        return 0L;
    }
}
