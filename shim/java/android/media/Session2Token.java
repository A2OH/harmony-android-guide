package android.media;
import android.content.ComponentName;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public final class Session2Token implements Parcelable {
    public static final int TYPE_SESSION = 0;
    public static final int TYPE_SESSION_SERVICE = 0;

    public Session2Token(Context p0, ComponentName p1) {}

    public int describeContents() { return 0; }
    public int getType() { return 0; }
    public int getUid() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
