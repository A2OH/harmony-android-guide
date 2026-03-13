package android.os;

/**
 * Android-compatible FileObserver shim. Monitors file system events.
 * This is a stub/no-op implementation for OpenHarmony migration.
 */
public abstract class FileObserver {

    public static final int ACCESS        = 0x00000001;
    public static final int MODIFY        = 0x00000002;
    public static final int ATTRIB        = 0x00000004;
    public static final int CLOSE_WRITE   = 0x00000008;
    public static final int CLOSE_NOWRITE = 0x00000010;
    public static final int OPEN          = 0x00000020;
    public static final int MOVED_FROM    = 0x00000040;
    public static final int MOVED_TO      = 0x00000080;
    public static final int CREATE        = 0x00000100;
    public static final int DELETE        = 0x00000200;
    public static final int DELETE_SELF   = 0x00000400;
    public static final int MOVE_SELF     = 0x00000800;
    public static final int ALL_EVENTS    =
            ACCESS | MODIFY | ATTRIB | CLOSE_WRITE | CLOSE_NOWRITE |
            OPEN | MOVED_FROM | MOVED_TO | CREATE | DELETE |
            DELETE_SELF | MOVE_SELF;

    private final String mPath;
    private final int mMask;

    public FileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    public FileObserver(String path, int mask) {
        mPath = path;
        mMask = mask;
    }

    public void startWatching() {
        // no-op: inotify not wired up in shim
    }

    public void stopWatching() {
        // no-op
    }

    public abstract void onEvent(int event, String path);
}
