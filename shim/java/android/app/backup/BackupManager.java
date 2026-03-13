package android.app.backup;

/**
 * Android-compatible BackupManager shim. Stub — no-op on OpenHarmony.
 *
 * OH mapping: android.app.backup.BackupManager has no direct OH equivalent.
 * Data persistence should use @ohos.data.preferences or RdbStore instead.
 */
public class BackupManager {

    private final Object mContext;

    public BackupManager(Object context) {
        mContext = context;
    }

    /**
     * Notify the backup system that the data for this app has changed.
     * Stub — no-op on OH.
     */
    public void dataChanged() {
        // no-op
    }

    /**
     * Request an immediate backup. Stub — always calls onRestoreFinished(0).
     */
    public int requestRestore(RestoreObserver observer) {
        if (observer != null) {
            observer.restoreFinished(0);
        }
        return 0;
    }

    /**
     * Convenience static form: mark a specific package's data as changed.
     * Stub — no-op on OH.
     */
    public static void dataChanged(String packageName) {
        // no-op
    }

    // ── RestoreObserver stub ───────────────────────────────────────────────────

    public static abstract class RestoreObserver {
        /** Called before the first restore package is delivered. */
        public void restoreStarting(int numPackages) {}

        /** Called once per package being restored. */
        public void onUpdate(int nowBeingRestored, String currentPackage) {}

        /**
         * Called when the restore is finished.
         * @param error 0 on success, non-zero on failure.
         */
        public abstract void restoreFinished(int error);
    }
}
