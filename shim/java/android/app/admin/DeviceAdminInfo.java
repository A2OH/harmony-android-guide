package android.app.admin;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;

public final class DeviceAdminInfo implements Parcelable {
    public static final int USES_ENCRYPTED_STORAGE = 0;
    public static final int USES_POLICY_DISABLE_CAMERA = 0;
    public static final int USES_POLICY_DISABLE_KEYGUARD_FEATURES = 0;
    public static final int USES_POLICY_EXPIRE_PASSWORD = 0;
    public static final int USES_POLICY_FORCE_LOCK = 0;
    public static final int USES_POLICY_LIMIT_PASSWORD = 0;
    public static final int USES_POLICY_RESET_PASSWORD = 0;
    public static final int USES_POLICY_WATCH_LOGIN = 0;
    public static final int USES_POLICY_WIPE_DATA = 0;

    public DeviceAdminInfo(Context p0, ResolveInfo p1) {}

    public int describeContents() { return 0; }
    public void dump(Printer p0, String p1) {}
    public ActivityInfo getActivityInfo() { return null; }
    public String getPackageName() { return null; }
    public String getReceiverName() { return null; }
    public String getTagForPolicy(int p0) { return null; }
    public boolean isVisible() { return false; }
    public CharSequence loadDescription(PackageManager p0) { return null; }
    public Drawable loadIcon(PackageManager p0) { return null; }
    public CharSequence loadLabel(PackageManager p0) { return null; }
    public boolean supportsTransferOwnership() { return false; }
    public boolean usesPolicy(int p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
