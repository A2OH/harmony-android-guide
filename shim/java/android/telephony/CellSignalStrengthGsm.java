package android.telephony;

import java.util.Objects;

/**
 * Android-compatible CellSignalStrengthGsm shim. Stub implementation for mock testing.
 */
public class CellSignalStrengthGsm extends CellInfo.CellSignalStrength {

    @Override
    public int getDbm() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getAsuLevel() {
        return 99;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public int getTimingAdvance() {
        return Integer.MAX_VALUE;
    }

    public int getBitErrorRate() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "CellSignalStrengthGsm:"
                + " ss=" + getDbm()
                + " ber=" + getBitErrorRate()
                + " ta=" + getTimingAdvance()
                + " level=" + getLevel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellSignalStrengthGsm)) return false;
        CellSignalStrengthGsm that = (CellSignalStrengthGsm) o;
        return getDbm() == that.getDbm()
                && getAsuLevel() == that.getAsuLevel()
                && getLevel() == that.getLevel()
                && getTimingAdvance() == that.getTimingAdvance()
                && getBitErrorRate() == that.getBitErrorRate();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDbm(), getAsuLevel(), getLevel(),
                getTimingAdvance(), getBitErrorRate());
    }
}
