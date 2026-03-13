package android.net.wifi.aware;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class Characteristics implements Parcelable {
    public static final int WIFI_AWARE_CIPHER_SUITE_NCS_SK_128 = 0;
    public static final int WIFI_AWARE_CIPHER_SUITE_NCS_SK_256 = 0;

    public Characteristics() {}

    public int describeContents() { return 0; }
    public int getMaxMatchFilterLength() { return 0; }
    public int getMaxServiceNameLength() { return 0; }
    public int getMaxServiceSpecificInfoLength() { return 0; }
    public int getSupportedCipherSuites() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
