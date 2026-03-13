package android.app.backup;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Android-compatible BackupAgentHelper shim. Stub — no-op on OpenHarmony.
 *
 * BackupAgentHelper is a convenience wrapper around BackupAgent that dispatches
 * backup/restore operations to a set of registered BackupHelper objects.
 *
 * OH mapping: No direct equivalent. Use @ohos.data.preferences for key-value
 * data and @ohos.data.rdb for structured data persistence.
 */
public class BackupAgentHelper extends BackupAgent {

    /**
     * Interface implemented by objects that know how to back up and restore
     * a specific slice of an app's data.
     */
    public interface BackupHelper {
        /**
         * Called by BackupAgentHelper to perform an incremental backup of
         * this helper's data. Stub — both parameters are Object here.
         */
        void performBackup(Object oldState, Object data, Object newState);

        /**
         * Called by BackupAgentHelper to restore this helper's data.
         * Stub — both parameters are Object here.
         */
        void restoreEntity(Object data);

        /**
         * Called after all restore entities have been delivered, to allow
         * the helper to save its new state.
         */
        void writeNewStateDescription(Object newState);
    }

    // Ordered map of key → helper, preserving registration order
    private final Map<String, BackupHelper> mHelpers = new LinkedHashMap<>();

    /**
     * Register a BackupHelper under the given key. The key is used to route
     * backup/restore data to the correct helper.
     *
     * @param keyPrefix a unique, stable string key for this helper
     * @param helper    the helper to register
     */
    public void addHelper(String keyPrefix, BackupHelper helper) {
        if (keyPrefix == null) throw new IllegalArgumentException("keyPrefix must not be null");
        if (helper == null)    throw new IllegalArgumentException("helper must not be null");
        mHelpers.put(keyPrefix, helper);
    }

    // ── BackupAgent implementation (dispatch to helpers) ───────────────────────

    @Override
    public void onBackup(Object oldState, Object data, Object newState) {
        for (BackupHelper helper : mHelpers.values()) {
            helper.performBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(Object data, int appVersionCode, Object newState) {
        for (BackupHelper helper : mHelpers.values()) {
            helper.restoreEntity(data);
            helper.writeNewStateDescription(newState);
        }
    }
}
