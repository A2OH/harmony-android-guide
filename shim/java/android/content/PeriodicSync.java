package android.content;
import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PeriodicSync implements Parcelable {
    public int account = 0;
    public int authority = 0;
    public int extras = 0;
    public int period = 0;

    public PeriodicSync(Account p0, String p1, Bundle p2, long p3) {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
