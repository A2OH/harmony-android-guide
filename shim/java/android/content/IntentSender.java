package android.content;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

public class IntentSender implements Parcelable {
    public IntentSender() {}

    public int describeContents() { return 0; }
    public String getCreatorPackage() { return null; }
    public int getCreatorUid() { return 0; }
    public UserHandle getCreatorUserHandle() { return null; }
    public static IntentSender readIntentSenderOrNullFromParcel(Parcel p0) { return null; }
    public void sendIntent(Context p0, int p1, Intent p2, Object p3, Handler p4) {}
    public void sendIntent(Context p0, int p1, Intent p2, Object p3, Handler p4, String p5) {}
    public static void writeIntentSenderOrNullToParcel(IntentSender p0, Parcel p1) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
