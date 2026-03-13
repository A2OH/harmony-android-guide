package android.telecom;
import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

public final class PhoneAccountHandle implements Parcelable {
    public PhoneAccountHandle(ComponentName p0, String p1) {}
    public PhoneAccountHandle(ComponentName p0, String p1, UserHandle p2) {}

    public int describeContents() { return 0; }
    public ComponentName getComponentName() { return null; }
    public String getId() { return null; }
    public UserHandle getUserHandle() { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
