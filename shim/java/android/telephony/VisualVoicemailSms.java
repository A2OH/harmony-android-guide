package android.telephony;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.telecom.PhoneAccountHandle;

public final class VisualVoicemailSms implements Parcelable {
    public VisualVoicemailSms() {}

    public int describeContents() { return 0; }
    public Bundle getFields() { return null; }
    public String getMessageBody() { return null; }
    public PhoneAccountHandle getPhoneAccountHandle() { return null; }
    public String getPrefix() { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
