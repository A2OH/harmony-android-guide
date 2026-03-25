package androidx.savedstate;

import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple SavedStateRegistry for Westlake engine.
 * Stores saved state bundles by key.
 */
public class SavedStateRegistry {

    private final Map<String, Bundle> savedState = new HashMap<>();
    private boolean restored = false;

    public Bundle consumeRestoredStateForKey(String key) {
        return savedState.remove(key);
    }

    public void registerSavedStateProvider(String key, SavedStateProvider provider) {
        // Store the provider — will be called during onSaveInstanceState
    }

    public void unregisterSavedStateProvider(String key) {
        savedState.remove(key);
    }

    public boolean isRestored() {
        return restored;
    }

    public void performRestore(Bundle savedState) {
        restored = true;
        if (savedState != null) {
            // Restore saved state bundles
            for (String key : savedState.keySet()) {
                Object val = savedState.get(key);
                if (val instanceof Bundle) {
                    this.savedState.put(key, (Bundle) val);
                }
            }
        }
    }

    public void performSave(Bundle outBundle) {
        for (Map.Entry<String, Bundle> entry : savedState.entrySet()) {
            outBundle.putBundle(entry.getKey(), entry.getValue());
        }
    }

    public interface SavedStateProvider {
        Bundle saveState();
    }
}
