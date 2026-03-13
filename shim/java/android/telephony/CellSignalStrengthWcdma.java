package android.telephony;

import java.util.Objects;

/**
 * Android-compatible CellSignalStrengthWcdma shim. Stub implementation for mock testing.
 *
 * All signal-strength getters return "unknown" sentinel values
 * (Integer.MAX_VALUE for dBm/EcNo, 99 for ASU, 0 for level).
 */
public class CellSignalStrengthWcdma {

    private int mRssi = Integer.MAX_VALUE;
    private int mBer = Integer.MAX_VALUE;
    private int mRscp = Integer.MAX_VALUE;
    private int mEcNo = Integer.MAX_VALUE;
    private int mLevel = 0;

    public CellSignalStrengthWcdma() {}

    /** Returns the RSSI in dBm, or Integer.MAX_VALUE if unknown. */
    public int getDbm() {
        return Integer.MAX_VALUE;
    }

    /** Returns the ASU level (0-31, 99=unknown). */
    public int getAsuLevel() {
        return 99;
    }

    /** Returns the signal level (0-4), 0 = none. */
    public int getLevel() {
        return 0;
    }

    /** Returns the EcNo in dB, or Integer.MAX_VALUE if unknown. */
    public int getEcNo() {
        return Integer.MAX_VALUE;
    }

    /** Returns the RSCP in dBm, or Integer.MAX_VALUE if unknown. */
    public int getRscp() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "CellSignalStrengthWcdma:"
                + " rssi=" + mRssi
                + " ber=" + mBer
                + " rscp=" + mRscp
                + " ecno=" + mEcNo
                + " level=" + mLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellSignalStrengthWcdma)) return false;
        CellSignalStrengthWcdma other = (CellSignalStrengthWcdma) o;
        return mRssi == other.mRssi
                && mBer == other.mBer
                && mRscp == other.mRscp
                && mEcNo == other.mEcNo
                && mLevel == other.mLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mRssi, mBer, mRscp, mEcNo, mLevel);
    }
}
