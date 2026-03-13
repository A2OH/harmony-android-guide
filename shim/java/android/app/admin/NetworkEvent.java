package android.app.admin;
import android.os.Parcelable;
import android.os.Parcelable;

public class NetworkEvent implements Parcelable {
    public NetworkEvent() {}

    public int describeContents() { return 0; }
    public long getId() { return 0L; }
    public String getPackageName() { return null; }
    public long getTimestamp() { return 0L; }
    public void writeToParcel(android.os.Parcel dest, int flags) {}
}
