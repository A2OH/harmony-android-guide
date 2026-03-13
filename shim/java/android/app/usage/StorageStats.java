package android.app.usage;

/**
 * A2OH shim: android.app.usage.StorageStats
 *
 * Holds the storage-usage statistics for a particular package/UID, as
 * returned by {@link StorageStatsManager}.
 *
 * All values are zero in this stub; a real bridge implementation would
 * populate them from the OH storage query API.
 */
public final class StorageStats {

    private long mAppBytes;
    private long mCacheBytes;
    private long mDataBytes;

    /** @hide — created by StorageStatsManager, not by app code */
    StorageStats() {}

    /**
     * Returns the size of the application's APK / native-library files, in bytes.
     */
    public long getAppBytes() {
        return mAppBytes;
    }

    /**
     * Returns the size of the application's cache directory contents, in bytes.
     */
    public long getCacheBytes() {
        return mCacheBytes;
    }

    /**
     * Returns the size of all other persistent data owned by the application, in bytes.
     */
    public long getDataBytes() {
        return mDataBytes;
    }
}
