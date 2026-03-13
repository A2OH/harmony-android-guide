package android.telephony;
import android.location.Location;
import android.se.omapi.Channel;
import android.location.Location;
import android.se.omapi.Channel;

import java.util.Objects;

/**
 * Android-compatible CellIdentityGsm shim. Stub implementation for A2OH migration.
 * All identity fields return Integer.MAX_VALUE (unknown), matching AOSP defaults.
 */
public class CellIdentityGsm {

    public CellIdentityGsm() {}

    /** @return MCC as integer, or Integer.MAX_VALUE if unknown. */
    public int getMcc() {
        return Integer.MAX_VALUE;
    }

    /** @return MNC as integer, or Integer.MAX_VALUE if unknown. */
    public int getMnc() {
        return Integer.MAX_VALUE;
    }

    /** @return MCC as string, or null if unknown. */
    public String getMccString() {
        return null;
    }

    /** @return MNC as string, or null if unknown. */
    public String getMncString() {
        return null;
    }

    /** @return 16-bit Location Area Code, or Integer.MAX_VALUE if unknown. */
    public int getLac() {
        return Integer.MAX_VALUE;
    }

    /** @return Cell Identity, or Integer.MAX_VALUE if unknown. */
    public int getCid() {
        return Integer.MAX_VALUE;
    }

    /** @return Absolute RF Channel Number, or Integer.MAX_VALUE if unknown. */
    public int getArfcn() {
        return Integer.MAX_VALUE;
    }

    /** @return Base Station Identity Code, or Integer.MAX_VALUE if unknown. */
    public int getBsic() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "CellIdentityGsm:{" +
            " mMcc=" + getMccString() +
            " mMnc=" + getMncString() +
            " mLac=" + getLac() +
            " mCid=" + getCid() +
            " mArfcn=" + getArfcn() +
            " mBsic=" + getBsic() +
            "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellIdentityGsm)) return false;
        CellIdentityGsm that = (CellIdentityGsm) o;
        return getMcc() == that.getMcc()
            && getMnc() == that.getMnc()
            && getLac() == that.getLac()
            && getCid() == that.getCid()
            && getArfcn() == that.getArfcn()
            && getBsic() == that.getBsic()
            && Objects.equals(getMccString(), that.getMccString())
            && Objects.equals(getMncString(), that.getMncString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMcc(), getMnc(), getLac(), getCid(),
            getArfcn(), getBsic(), getMccString(), getMncString());
    }
}
