package android.content;
import android.util.Log;
import android.util.Log;

import com.ohos.shim.bridge.OHBridge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.content.SharedPreferences → @ohos.data.preferences
 * Tier 1 — near-direct mapping.
 *
 * OH Preferences is async; this shim wraps it synchronously to match
 * the Android API contract. The Editor.apply() batches writes and
 * calls OHBridge.preferencesFlush() on commit.
 */
public class SharedPreferences {
    private final long handle;
    private final String name;

    SharedPreferences(String name) {
        this.name = name;
        this.handle = OHBridge.preferencesOpen(name);
    }

    public String getString(String key, String defValue) {
        return OHBridge.preferencesGetString(handle, key, defValue);
    }

    public int getInt(String key, int defValue) {
        return OHBridge.preferencesGetInt(handle, key, defValue);
    }

    public long getLong(String key, long defValue) {
        return OHBridge.preferencesGetLong(handle, key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return OHBridge.preferencesGetFloat(handle, key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return OHBridge.preferencesGetBoolean(handle, key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        // OH Preferences doesn't have native string set support.
        // Store as comma-separated string internally.
        String raw = OHBridge.preferencesGetString(handle, key, null);
        if (raw == null) return defValues;
        Set<String> result = new HashSet<>();
        for (String s : raw.split("\u001F")) { // unit separator
            if (!s.isEmpty()) result.add(s);
        }
        return result;
    }

    public Map<String, ?> getAll() {
        // Limited implementation — OH Preferences doesn't expose getAll easily.
        // Return empty map as fallback.
        return new HashMap<>();
    }

    public boolean contains(String key) {
        // Check by reading with a sentinel default
        return OHBridge.preferencesGetString(handle, key, null) != null;
    }

    public Editor edit() {
        return new Editor(handle);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // TODO: Map to OH Preferences.on('change') via bridge
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // TODO: Map to OH Preferences.off('change') via bridge
    }

    // ── Editor ──────────────────────────────────────────────────────

    public static class Editor {
        private final long handle;
        private final Map<String, Object> pending = new HashMap<>();
        private final Set<String> removals = new HashSet<>();
        private boolean clearRequested = false;

        Editor(long handle) {
            this.handle = handle;
        }

        public Editor putString(String key, String value) {
            pending.put(key, value);
            removals.remove(key);
            return this;
        }

        public Editor putInt(String key, int value) {
            pending.put(key, value);
            removals.remove(key);
            return this;
        }

        public Editor putLong(String key, long value) {
            pending.put(key, value);
            removals.remove(key);
            return this;
        }

        public Editor putFloat(String key, float value) {
            pending.put(key, value);
            removals.remove(key);
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            pending.put(key, value);
            removals.remove(key);
            return this;
        }

        public Editor putStringSet(String key, Set<String> values) {
            if (values == null) {
                return remove(key);
            }
            // Store as unit-separator-joined string
            StringBuilder sb = new StringBuilder();
            for (String s : values) {
                if (sb.length() > 0) sb.append('\u001F');
                sb.append(s);
            }
            pending.put(key, sb.toString());
            removals.remove(key);
            return this;
        }

        public Editor remove(String key) {
            removals.add(key);
            pending.remove(key);
            return this;
        }

        public Editor clear() {
            clearRequested = true;
            pending.clear();
            removals.clear();
            return this;
        }

        /**
         * Writes to OH Preferences and flushes to disk.
         * Android commit() is synchronous and returns success.
         */
        public boolean commit() {
            try {
                applyInternal();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Writes to OH Preferences and flushes asynchronously.
         * In the shim we still flush synchronously since OH flush() must be called.
         */
        public void apply() {
            try {
                applyInternal();
            } catch (Exception e) {
                android.util.Log.e("SharedPreferences", "apply() failed", e);
            }
        }

        private void applyInternal() {
            if (clearRequested) {
                OHBridge.preferencesClear(handle);
            }

            for (String key : removals) {
                OHBridge.preferencesRemove(handle, key);
            }

            for (Map.Entry<String, Object> entry : pending.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    OHBridge.preferencesPutString(handle, key, (String) value);
                } else if (value instanceof Integer) {
                    OHBridge.preferencesPutInt(handle, key, (Integer) value);
                } else if (value instanceof Long) {
                    OHBridge.preferencesPutLong(handle, key, (Long) value);
                } else if (value instanceof Float) {
                    OHBridge.preferencesPutFloat(handle, key, (Float) value);
                } else if (value instanceof Boolean) {
                    OHBridge.preferencesPutBoolean(handle, key, (Boolean) value);
                }
            }

            OHBridge.preferencesFlush(handle);
            pending.clear();
            removals.clear();
            clearRequested = false;
        }
    }

    // ── Listener interface ──

    public interface OnSharedPreferenceChangeListener {
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
    }
}
