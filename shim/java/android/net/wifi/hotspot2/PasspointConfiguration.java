package android.net.wifi.hotspot2;
import android.net.wifi.hotspot2.pps.Credential;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Parcel;
import android.os.Parcelable;

public final class PasspointConfiguration implements Parcelable {
    public PasspointConfiguration() {}
    public PasspointConfiguration(PasspointConfiguration p0) {}

    public int describeContents() { return 0; }
    public Credential getCredential() { return null; }
    public HomeSp getHomeSp() { return null; }
    public long getSubscriptionExpirationTimeMillis() { return 0L; }
    public boolean isOsuProvisioned() { return false; }
    public void setCredential(Credential p0) {}
    public void setHomeSp(HomeSp p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
