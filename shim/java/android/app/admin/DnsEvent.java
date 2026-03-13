package android.app.admin;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public final class DnsEvent extends NetworkEvent implements Parcelable {
    public DnsEvent() {}

    public String getHostname() { return null; }
    public List<?> getInetAddresses() { return null; }
    public int getTotalResolvedAddressCount() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
