package androidx.savedstate;

import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;

public final class SavedStateRegistry {
    private final Map<String, SavedStateProvider> mProviders = new HashMap<>();
    private Bundle mRestoredState;
    private boolean mRestored = false;

    public interface SavedStateProvider {
        Bundle saveState();
    }

    public void registerSavedStateProvider(String key, SavedStateProvider provider) {
        mProviders.put(key, provider);
    }

    // Obfuscated name used by McDonald's DEX
    public void c(String key, SavedStateProvider provider) {
        registerSavedStateProvider(key, provider);
    }

    public void unregisterSavedStateProvider(String key) {
        mProviders.remove(key);
    }
    public void e(String key) { unregisterSavedStateProvider(key); }

    public Bundle consumeRestoredStateForKey(String key) {
        if (mRestoredState == null || key == null) {
            return null;
        }
        Bundle state = mRestoredState.getBundle(key);
        mRestoredState.remove(key);
        return state;
    }
    public Bundle a(String key) { return consumeRestoredStateForKey(key); }

    public SavedStateProvider b(String key) {
        return mProviders.get(key);
    }

    public void d(Class<?> recreatedClass) {
        // Recreation callbacks are not supported in the shim.
    }

    public boolean isRestored() { return mRestored; }

    public void performRestore(Bundle savedState) {
        mRestored = true;
        mRestoredState = savedState;
    }
    public void performSave(Bundle outBundle) {
        for (Map.Entry<String, SavedStateProvider> e : mProviders.entrySet()) {
            Bundle b = e.getValue().saveState();
            if (b != null) outBundle.putBundle(e.getKey(), b);
        }
    }
}
