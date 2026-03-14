package android.os;

import java.io.File;

/**
 * Android-compatible FileObserver shim. Monitors file system events.
 * This is a stub/no-op implementation for OpenHarmony migration —
 * inotify is not wired up; startWatching/stopWatching are no-ops.
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
    private boolean mWatching;

    /** Create a FileObserver monitoring the given path for ALL_EVENTS. */
    public FileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    /** Create a FileObserver monitoring the given path for the specified event mask. */
    public FileObserver(String path, int mask) {
        mPath = path;
        mMask = mask;
        mWatching = false;
    }

    /** Create a FileObserver monitoring the given file for ALL_EVENTS (API 29+). */
    public FileObserver(File file) {
        this(file.getPath(), ALL_EVENTS);
    }

    /** Create a FileObserver monitoring the given file for the specified event mask (API 29+). */
    public FileObserver(File file, int mask) {
        this(file.getPath(), mask);
    }

    /**
     * Start watching for events. Stub — sets the watching flag but does not
     * register any inotify watches.
     */
    public void startWatching() {
        mWatching = true;
    }

    /**
     * Stop watching for events. Stub — clears the watching flag.
     */
    public void stopWatching() {
        mWatching = false;
    }

    /**
     * Called when a monitored event occurs.
     *
     * @param event the type of event that occurred
     * @param path  the path relative to the monitored directory, or {@code null}
     */
    public abstract void onEvent(int event, String path);
}
