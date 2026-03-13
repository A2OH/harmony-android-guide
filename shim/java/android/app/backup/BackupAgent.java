package android.app.backup;

/**
 * Android-compatible BackupAgent shim. Abstract stub — no-op on OpenHarmony.
 *
 * OH mapping: No direct equivalent. Persistent data should migrate to
 * @ohos.data.preferences (key-value) or @ohos.data.rdb (structured).
 *
 * Subclass this and implement onBackup() and onRestore() to preserve
 * compile-time compatibility with Android backup code.
 */
public abstract class BackupAgent {

    /** Backup data type constant: a regular file. */
    public static final int TYPE_FILE = 1;

    /** Backup data type constant: a directory. */
    public static final int TYPE_DIRECTORY = 2;

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    /**
     * Called when the BackupAgent is first instantiated.
     * Override to perform one-time initialization.
     */
    public void onCreate() {}

    /**
     * Called when the BackupAgent is no longer needed.
     */
    public void onDestroy() {}

    // ── Abstract backup / restore ──────────────────────────────────────────────

    /**
     * Called by the backup system when it is time to write backup data.
     * Implementations should write data to the provided BackupDataOutput.
     * Stub signature — BackupDataOutput and ParcelFileDescriptor are Object here.
     *
     * @param oldState   stream representing the last backup state; may be null
     * @param data       output stream for the backup data
     * @param newState   output stream to write the new backup state
     */
    public abstract void onBackup(Object oldState, Object data, Object newState)
            throws Exception;

    /**
     * Called by the backup system when it is time to restore data.
     * Implementations should read data from the provided BackupDataInput.
     * Stub signature — BackupDataInput is Object here.
     *
     * @param data      input stream containing the backup data
     * @param appVersionCode version of the app that produced the backup
     * @param newState  output stream to write the post-restore state
     */
    public abstract void onRestore(Object data, int appVersionCode, Object newState)
            throws Exception;
}
