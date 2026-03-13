package android.telecom;
import android.os.Parcel;
import android.os.Parcelable;

public final class PhoneAccountSuggestion implements Parcelable {
    public static final int REASON_FREQUENT = 0;
    public static final int REASON_INTRA_CARRIER = 0;
    public static final int REASON_NONE = 0;
    public static final int REASON_OTHER = 0;
    public static final int REASON_USER_SET = 0;

    public PhoneAccountSuggestion(PhoneAccountHandle p0, int p1, boolean p2) {}

    public int describeContents() { return 0; }
    public int getReason() { return 0; }
    public boolean shouldAutoSelect() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
