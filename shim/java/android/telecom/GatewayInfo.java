package android.telecom;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class GatewayInfo implements Parcelable {
    public GatewayInfo(String p0, Uri p1, Uri p2) {}

    public int describeContents() { return 0; }
    public Uri getGatewayAddress() { return null; }
    public String getGatewayProviderPackageName() { return null; }
    public Uri getOriginalAddress() { return null; }
    public boolean isEmpty() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
