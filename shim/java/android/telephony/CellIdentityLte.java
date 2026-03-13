package android.telephony;

/**
 * Android-compatible CellIdentityLte shim. Stub implementation for mock testing.
 */
public class CellIdentityLte extends CellInfo.CellIdentity {

    public int getMcc() {
        return Integer.MAX_VALUE;
    }

    public int getMnc() {
        return Integer.MAX_VALUE;
    }

    public int getCi() {
        return Integer.MAX_VALUE;
    }

    public int getPci() {
        return Integer.MAX_VALUE;
    }

    public int getTac() {
        return Integer.MAX_VALUE;
    }

    public int getEarfcn() {
        return Integer.MAX_VALUE;
    }

    public int getBandwidth() {
        return Integer.MAX_VALUE;
    }
}
