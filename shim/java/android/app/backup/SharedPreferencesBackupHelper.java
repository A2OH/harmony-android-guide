package android.app.backup;

/**
 * Android-compatible SharedPreferencesBackupHelper shim. Stub — no-op on OH.
 *
 * In Android this helper backs up and restores named SharedPreferences files.
 * On OpenHarmony, preferences persistence is handled by @ohos.data.preferences;
 * this shim satisfies compile-time dependencies only — no data is moved.
 */
public class SharedPreferencesBackupHelper implements BackupAgentHelper.BackupHelper {

    private final Object mContext;
    private final String[] mPrefGroups;

    /**
     * Construct a helper that will back up the named SharedPreferences files.
     *
     * @param context    the application context (typed as Object for shim compatibility)
     * @param prefGroups the names of the SharedPreferences files to back up
     */
    public SharedPreferencesBackupHelper(Object context, String... prefGroups) {
        mContext = context;
        mPrefGroups = (prefGroups != null) ? prefGroups : new String[0];
    }

    /** Returns the preference group names registered with this helper. */
    public String[] getPrefGroups() {
        return mPrefGroups;
    }

    // ── BackupHelper ───────────────────────────────────────────────────────────

    @Override
    public void performBackup(Object oldState, Object data, Object newState) {
        // stub — no-op on OH; preferences are managed by @ohos.data.preferences
    }

    @Override
    public void restoreEntity(Object data) {
        // stub — no-op on OH
    }

    @Override
    public void writeNewStateDescription(Object newState) {
        // stub — no-op on OH
    }
}
