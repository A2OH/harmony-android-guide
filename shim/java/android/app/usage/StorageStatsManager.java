package android.app.usage;

import android.os.UserHandle;
import java.util.UUID;

/**
 * A2OH shim: android.app.usage.StorageStatsManager
 *
 * Provides access to the storage statistics for a device or individual
 * application. Obtained via {@code Context.getSystemService(Context.STORAGE_STATS_SERVICE)}.
 *
 * OH mapping: @ohos.file.storageStatistics
 *   getFreeBytes()            → storageStatistics.getFreeSize()
 *   getTotalBytes()           → storageStatistics.getTotalSize()
 *   queryStatsForPackage()    → storageStatistics.getCurrentBundleStats()
 *
 * This stub returns zero / empty values so callers compile and run without
 * crashing; a bridge layer is required for real data.
 */
public class StorageStatsManager {

    // ── Capacity queries ─────────────────────────────────────────────────────

    /**
     * Returns the number of available bytes on the storage volume identified
     * by {@code storageUuid}.
     *
     * @param storageUuid UUID of the storage volume (e.g. from StorageManager).
     * @return free space in bytes; always 0 in the shim.
     */
    public long getFreeBytes(UUID storageUuid) {
        return 0L;
    }

    /**
     * Returns the total capacity of the storage volume identified by
     * {@code storageUuid}, in bytes.
     *
     * @param storageUuid UUID of the storage volume.
     * @return total capacity in bytes; always 0 in the shim.
     */
    public long getTotalBytes(UUID storageUuid) {
        return 0L;
    }

    // ── Per-package query ────────────────────────────────────────────────────

    /**
     * Returns the storage statistics for the given package on the given
     * storage volume, as seen by the given user.
     *
     * @param storageUuid  UUID of the storage volume to query.
     * @param packageName  Package to query.
     * @param user         The user whose data should be considered.
     * @return A {@link StorageStats} whose byte counts are all 0 in the shim.
     */
    public StorageStats queryStatsForPackage(UUID storageUuid,
            String packageName, UserHandle user) {
        return new StorageStats();
    }
}
