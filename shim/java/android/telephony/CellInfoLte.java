package android.telephony;

/**
 * Android-compatible CellInfoLte shim. Stub implementation for mock testing.
 */
public class CellInfoLte extends CellInfo {

    private final CellIdentityLte mCellIdentity = new CellIdentityLte();
    private final CellSignalStrengthLte mCellSignalStrength = new CellSignalStrengthLte();

    @Override
    public CellIdentityLte getCellIdentity() {
        return mCellIdentity;
    }

    @Override
    public CellSignalStrengthLte getCellSignalStrength() {
        return mCellSignalStrength;
    }
}
