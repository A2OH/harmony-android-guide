package android.os.storage;

public class OnObbStateChangeListener {
    public static final int ERROR_ALREADY_MOUNTED = 0;
    public static final int ERROR_COULD_NOT_MOUNT = 0;
    public static final int ERROR_COULD_NOT_UNMOUNT = 0;
    public static final int ERROR_INTERNAL = 0;
    public static final int ERROR_NOT_MOUNTED = 0;
    public static final int ERROR_PERMISSION_DENIED = 0;
    public static final int MOUNTED = 0;
    public static final int UNMOUNTED = 0;

    public OnObbStateChangeListener() {}

    public void onObbStateChange(String p0, int p1) {}
}
