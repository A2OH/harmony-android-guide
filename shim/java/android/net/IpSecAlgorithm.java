package android.net;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class IpSecAlgorithm implements Parcelable {
    public static final int AUTH_CRYPT_AES_GCM = 0;
    public static final int AUTH_HMAC_MD5 = 0;
    public static final int AUTH_HMAC_SHA1 = 0;
    public static final int AUTH_HMAC_SHA256 = 0;
    public static final int AUTH_HMAC_SHA384 = 0;
    public static final int AUTH_HMAC_SHA512 = 0;
    public static final int CRYPT_AES_CBC = 0;

    public IpSecAlgorithm(String p0, byte[] p1) {}
    public IpSecAlgorithm(String p0, byte[] p1, int p2) {}

    public int describeContents() { return 0; }
    public int getTruncationLengthBits() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
