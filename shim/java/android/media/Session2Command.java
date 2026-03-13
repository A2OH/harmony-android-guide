package android.media;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class Session2Command implements Parcelable {
    public static final int COMMAND_CODE_CUSTOM = 0;

    public Session2Command(int p0) {}
    public Session2Command(String p0, Bundle p1) {}

    public int describeContents() { return 0; }
    public int getCommandCode() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
