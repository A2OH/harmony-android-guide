package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public final class NetworkScanRequest implements Parcelable {
    public static final int SCAN_TYPE_ONE_SHOT = 0;
    public static final int SCAN_TYPE_PERIODIC = 0;

    public NetworkScanRequest(int p0, RadioAccessSpecifier[] p1, int p2, int p3, boolean p4, int p5, java.util.ArrayList<Object> p6) {}

    public int describeContents() { return 0; }
    public boolean getIncrementalResults() { return false; }
    public int getIncrementalResultsPeriodicity() { return 0; }
    public int getMaxSearchTime() { return 0; }
    public ArrayList<Object> getPlmns() { return null; }
    public int getScanType() { return 0; }
    public int getSearchPeriodicity() { return 0; }
    public RadioAccessSpecifier[] getSpecifiers() { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
