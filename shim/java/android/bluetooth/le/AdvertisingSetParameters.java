package android.bluetooth.le;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class AdvertisingSetParameters implements Parcelable {
    public static final int INTERVAL_HIGH = 0;
    public static final int INTERVAL_LOW = 0;
    public static final int INTERVAL_MAX = 0;
    public static final int INTERVAL_MEDIUM = 0;
    public static final int INTERVAL_MIN = 0;
    public static final int TX_POWER_HIGH = 0;
    public static final int TX_POWER_LOW = 0;
    public static final int TX_POWER_MAX = 0;
    public static final int TX_POWER_MEDIUM = 0;
    public static final int TX_POWER_MIN = 0;
    public static final int TX_POWER_ULTRA_LOW = 0;

    public AdvertisingSetParameters() {}

    public int describeContents() { return 0; }
    public int getInterval() { return 0; }
    public int getPrimaryPhy() { return 0; }
    public int getSecondaryPhy() { return 0; }
    public int getTxPowerLevel() { return 0; }
    public boolean includeTxPower() { return false; }
    public boolean isAnonymous() { return false; }
    public boolean isConnectable() { return false; }
    public boolean isLegacy() { return false; }
    public boolean isScannable() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
