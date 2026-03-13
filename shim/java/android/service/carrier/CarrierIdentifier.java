package android.service.carrier;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class CarrierIdentifier implements Parcelable {
    public CarrierIdentifier(String p0, String p1, String p2, String p3, String p4, String p5) {}
    public CarrierIdentifier(String p0, String p1, String p2, String p3, String p4, String p5, int p6, int p7) {}
    public CarrierIdentifier(byte[] p0, String p1, String p2) {}

    public int describeContents() { return 0; }
    public int getCarrierId() { return 0; }
    public String getMcc() { return null; }
    public String getMnc() { return null; }
    public int getSpecificCarrierId() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
