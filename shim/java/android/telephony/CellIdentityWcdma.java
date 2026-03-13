package android.telephony;
import android.location.Location;
import android.net.Network;
import android.se.omapi.Channel;
import android.location.Location;
import android.net.Network;
import android.se.omapi.Channel;

import java.util.Objects;

/**
 * Android-compatible CellIdentityWcdma shim. Stub implementation for A2OH migration.
 * Returns default "unknown" values (Integer.MAX_VALUE / null) for all identity fields.
 */
public class CellIdentityWcdma {
    private final int mMcc;
    private final int mMnc;
    private final int mLac;
    private final int mCid;
    private final int mPsc;
    private final int mUarfcn;
    private final String mMccStr;
    private final String mMncStr;

    /** @hide */
    public CellIdentityWcdma() {
        mMcc = Integer.MAX_VALUE;
        mMnc = Integer.MAX_VALUE;
        mLac = Integer.MAX_VALUE;
        mCid = Integer.MAX_VALUE;
        mPsc = Integer.MAX_VALUE;
        mUarfcn = Integer.MAX_VALUE;
        mMccStr = null;
        mMncStr = null;
    }

    /** Returns the Mobile Country Code in integer format, or Integer.MAX_VALUE if unknown. */
    public int getMcc() {
        return mMcc;
    }

    /** Returns the Mobile Network Code in integer format, or Integer.MAX_VALUE if unknown. */
    public int getMnc() {
        return mMnc;
    }

    /** Returns the Mobile Country Code as a String, or null if unknown. */
    public String getMccString() {
        return mMccStr;
    }

    /** Returns the Mobile Network Code as a String, or null if unknown. */
    public String getMncString() {
        return mMncStr;
    }

    /** Returns the Location Area Code, or Integer.MAX_VALUE if unknown. */
    public int getLac() {
        return mLac;
    }

    /** Returns the Cell Identity (CID), or Integer.MAX_VALUE if unknown. */
    public int getCid() {
        return mCid;
    }

    /** Returns the Primary Scrambling Code, or Integer.MAX_VALUE if unknown. */
    public int getPsc() {
        return mPsc;
    }

    /** Returns the UTRA Absolute RF Channel Number, or Integer.MAX_VALUE if unknown. */
    public int getUarfcn() {
        return mUarfcn;
    }

    @Override
    public String toString() {
        return "CellIdentityWcdma:{" +
                " mMcc=" + mMcc +
                " mMnc=" + mMnc +
                " mMccStr=" + mMccStr +
                " mMncStr=" + mMncStr +
                " mLac=" + mLac +
                " mCid=" + mCid +
                " mPsc=" + mPsc +
                " mUarfcn=" + mUarfcn +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellIdentityWcdma)) return false;
        CellIdentityWcdma that = (CellIdentityWcdma) o;
        return mMcc == that.mMcc &&
                mMnc == that.mMnc &&
                mLac == that.mLac &&
                mCid == that.mCid &&
                mPsc == that.mPsc &&
                mUarfcn == that.mUarfcn &&
                Objects.equals(mMccStr, that.mMccStr) &&
                Objects.equals(mMncStr, that.mMncStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mMcc, mMnc, mLac, mCid, mPsc, mUarfcn, mMccStr, mMncStr);
    }
}
