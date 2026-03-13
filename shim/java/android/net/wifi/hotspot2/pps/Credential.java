package android.net.wifi.hotspot2.pps;
import android.os.Parcel;
import android.os.Parcelable;

public final class Credential implements Parcelable {
    public Credential() {}
    public Credential(Credential p0) {}

    public int describeContents() { return 0; }
    public Object getCaCertificate() { return null; }
    public Object getCertCredential() { return null; }
    public Object[] getClientCertificateChain() { return null; }
    public Object getClientPrivateKey() { return null; }
    public String getRealm() { return null; }
    public Object getSimCredential() { return null; }
    public Object getUserCredential() { return null; }
    public void setCaCertificate(Object p0) {}
    public void setCertCredential(Object p0) {}
    public void setClientCertificateChain(Object[] p0) {}
    public void setClientPrivateKey(Object p0) {}
    public void setRealm(String p0) {}
    public void setSimCredential(Object p0) {}
    public void setUserCredential(Object p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
