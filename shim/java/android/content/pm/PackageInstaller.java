package android.content.pm;
import android.graphics.Bitmap;
import android.os.Handler;

public class PackageInstaller {
    public static final int ACTION_SESSION_COMMITTED = 0;
    public static final int ACTION_SESSION_DETAILS = 0;
    public static final int ACTION_SESSION_UPDATED = 0;
    public static final int EXTRA_OTHER_PACKAGE_NAME = 0;
    public static final int EXTRA_PACKAGE_NAME = 0;
    public static final int EXTRA_SESSION = 0;
    public static final int EXTRA_SESSION_ID = 0;
    public static final int EXTRA_STATUS = 0;
    public static final int EXTRA_STATUS_MESSAGE = 0;
    public static final int EXTRA_STORAGE_PATH = 0;
    public static final int STATUS_FAILURE = 0;
    public static final int STATUS_FAILURE_ABORTED = 0;
    public static final int STATUS_FAILURE_BLOCKED = 0;
    public static final int STATUS_FAILURE_CONFLICT = 0;
    public static final int STATUS_FAILURE_INCOMPATIBLE = 0;
    public static final int STATUS_FAILURE_INVALID = 0;
    public static final int STATUS_FAILURE_STORAGE = 0;
    public static final int STATUS_PENDING_USER_ACTION = 0;
    public static final int STATUS_SUCCESS = 0;

    public PackageInstaller() {}

    public void abandonSession(int p0) {}
    public int createSession(Object p0) { return 0; }
    public void registerSessionCallback(Object p0) {}
    public void registerSessionCallback(Object p0, Handler p1) {}
    public void unregisterSessionCallback(Object p0) {}
    public void updateSessionAppIcon(int p0, Bitmap p1) {}
    public void updateSessionAppLabel(int p0, CharSequence p1) {}
}
