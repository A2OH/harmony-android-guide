package android.app;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class AuthenticationRequiredException extends SecurityException implements Parcelable {
    public AuthenticationRequiredException(Throwable p0, PendingIntent p1) {}

    public int describeContents() { return 0; }
    public PendingIntent getUserAction() { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
