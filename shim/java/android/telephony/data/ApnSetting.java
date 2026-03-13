package android.telephony.data;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ApnSetting implements Parcelable {
    public static final int AUTH_TYPE_CHAP = 0;
    public static final int AUTH_TYPE_NONE = 0;
    public static final int AUTH_TYPE_PAP = 0;
    public static final int AUTH_TYPE_PAP_OR_CHAP = 0;
    public static final int MVNO_TYPE_GID = 0;
    public static final int MVNO_TYPE_ICCID = 0;
    public static final int MVNO_TYPE_IMSI = 0;
    public static final int MVNO_TYPE_SPN = 0;
    public static final int PROTOCOL_IP = 0;
    public static final int PROTOCOL_IPV4V6 = 0;
    public static final int PROTOCOL_IPV6 = 0;
    public static final int PROTOCOL_NON_IP = 0;
    public static final int PROTOCOL_PPP = 0;
    public static final int PROTOCOL_UNSTRUCTURED = 0;
    public static final int TYPE_CBS = 0;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_DUN = 0;
    public static final int TYPE_EMERGENCY = 0;
    public static final int TYPE_FOTA = 0;
    public static final int TYPE_HIPRI = 0;
    public static final int TYPE_IA = 0;
    public static final int TYPE_IMS = 0;
    public static final int TYPE_MCX = 0;
    public static final int TYPE_MMS = 0;
    public static final int TYPE_SUPL = 0;
    public static final int TYPE_XCAP = 0;

    public ApnSetting() {}

    public int describeContents() { return 0; }
    public String getApnName() { return null; }
    public int getApnTypeBitmask() { return 0; }
    public int getAuthType() { return 0; }
    public int getCarrierId() { return 0; }
    public String getEntryName() { return null; }
    public int getId() { return 0; }
    public String getMmsProxyAddressAsString() { return null; }
    public int getMmsProxyPort() { return 0; }
    public Uri getMmsc() { return null; }
    public int getMvnoType() { return 0; }
    public int getNetworkTypeBitmask() { return 0; }
    public String getOperatorNumeric() { return null; }
    public String getPassword() { return null; }
    public int getProtocol() { return 0; }
    public String getProxyAddressAsString() { return null; }
    public int getProxyPort() { return 0; }
    public int getRoamingProtocol() { return 0; }
    public String getUser() { return null; }
    public boolean isEnabled() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
