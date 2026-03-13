package android.telephony;
import android.os.Parcel;
import android.os.Parcel;

public final class CellIdentityCdma extends CellIdentity {
    public CellIdentityCdma() {}

    public int getBasestationId() { return 0; }
    public int getLatitude() { return 0; }
    public int getLongitude() { return 0; }
    public int getNetworkId() { return 0; }
    public int getSystemId() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
