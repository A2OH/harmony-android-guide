package android.content;
import android.net.Uri;
import android.net.Uri;

/**
 * Android-compatible UriPermission shim.
 * Represents a persisted Uri permission grant.
 * Stub — holds data values only, no system interaction.
 */
public class UriPermission {

    /** Permission mode flag: read access. */
    public static final int MODE_READ_PERMISSION = 1;

    /** Permission mode flag: write access. */
    public static final int MODE_WRITE_PERMISSION = 2;

    /** Sentinel value for persistedTime when the permission has not been persisted. */
    public static final long INVALID_TIME = Long.MIN_VALUE;

    /** The Uri this permission covers. Typed as Object for portability. */
    private final Object mUri;

    /** Bitmask of permission modes (read, write). */
    private final int mModeFlags;

    /** The time at which this permission was persisted, in milliseconds since epoch. */
    private final long mPersistedTime;

    public UriPermission(Object uri, int modeFlags, long persistedTime) {
        mUri = uri;
        mModeFlags = modeFlags;
        mPersistedTime = persistedTime;
    }

    /** Returns the Uri this permission covers. */
    public Object getUri() {
        return mUri;
    }

    /** Returns true if this permission grants read access. */
    public boolean isReadPermission() {
        return (mModeFlags & MODE_READ_PERMISSION) != 0;
    }

    /** Returns true if this permission grants write access. */
    public boolean isWritePermission() {
        return (mModeFlags & MODE_WRITE_PERMISSION) != 0;
    }

    /** Returns the raw mode flags bitmask. */
    public int getModeFlags() {
        return mModeFlags;
    }

    /**
     * Returns the time at which this permission was persisted, in milliseconds
     * since the Unix epoch, or {@link #INVALID_TIME} if not persisted.
     */
    public long getPersistedTime() {
        return mPersistedTime;
    }

    @Override
    public String toString() {
        return "UriPermission{uri=" + mUri
                + ", read=" + isReadPermission()
                + ", write=" + isWritePermission()
                + ", persistedTime=" + mPersistedTime + "}";
    }
}
