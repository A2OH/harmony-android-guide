package android.content.pm;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class SigningInfo implements Parcelable {
    public SigningInfo() {}
    public SigningInfo(SigningInfo p0) {}

    public int describeContents() { return 0; }
    public Signature[] getApkContentsSigners() { return null; }
    public Signature[] getSigningCertificateHistory() { return null; }
    public boolean hasMultipleSigners() { return false; }
    public boolean hasPastSigningCertificates() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
