package android.telephony;

/**
 * Android-compatible CellSignalStrengthLte shim. Stub implementation for mock testing.
 */
public class CellSignalStrengthLte extends CellInfo.CellSignalStrength {

    @Override
    public int getDbm() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getAsuLevel() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public int getRsrp() {
        return Integer.MAX_VALUE;
    }

    public int getRsrq() {
        return Integer.MAX_VALUE;
    }

    public int getRssnr() {
        return Integer.MAX_VALUE;
    }

    public int getCqi() {
        return Integer.MAX_VALUE;
    }

    public int getTimingAdvance() {
        return Integer.MAX_VALUE;
    }
}
