package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class VisualVoicemailSmsFilterSettings implements Parcelable {
    public static final int DESTINATION_PORT_ANY = 0;
    public static final int DESTINATION_PORT_DATA_SMS = 0;
    public int clientPrefix = 0;
    public int destinationPort = 0;
    public int originatingNumbers = 0;

    public VisualVoicemailSmsFilterSettings() {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
