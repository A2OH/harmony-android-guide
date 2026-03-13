package android.telephony;

/**
 * Android-compatible CellInfoGsm shim. Stub implementation for mock testing.
 * Returns null for identity and signal strength since CellIdentityGsm /
 * CellSignalStrengthGsm are not yet present in the shim tree.
 */
public class CellInfoGsm extends CellInfo {

    @Override
    public CellInfo.CellIdentity getCellIdentity() {
        return null;
    }

    @Override
    public CellInfo.CellSignalStrength getCellSignalStrength() {
        return null;
    }
}
