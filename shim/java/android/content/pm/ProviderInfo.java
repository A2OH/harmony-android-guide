package android.content.pm;
import android.os.Parcelable;
import android.util.Printer;

public final class ProviderInfo extends ComponentInfo implements Parcelable {
    public static final int FLAG_SINGLE_USER = 0;
    public int authority = 0;
    public int flags = 0;
    public int forceUriPermissions = 0;
    public int grantUriPermissions = 0;
    public int initOrder = 0;
    public int multiprocess = 0;
    public int pathPermissions = 0;
    public int readPermission = 0;
    public int uriPermissionPatterns = 0;
    public int writePermission = 0;

    public ProviderInfo() {}
    public ProviderInfo(ProviderInfo p0) {}

    public int describeContents() { return 0; }
    public void dump(Printer p0, String p1) {}
}
