package android.telephony;

/**
 * Android-compatible CellInfoWcdma shim. Stub implementation for mock testing.
 */
public class CellInfoWcdma extends CellInfo {

    @Override
    public CellIdentity getCellIdentity() {
        return null;
    }

    @Override
    public CellSignalStrength getCellSignalStrength() {
        return null;
    }
}
