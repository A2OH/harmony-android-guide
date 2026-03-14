package android.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.content.SharedPreferences -> @ohos.data.preferences
 * Tier 1 -- near-direct mapping.
 *
 * OH Preferences is async; this shim wraps it synchronously to match
 * the Android API contract. The Editor.apply() batches writes and
 * calls OHBridge.preferencesFlush() on commit.
 *
 * All OHBridge calls are done via reflection to avoid compile-time
 * dependency on the native bridge class.
 */
public class SharedPreferences {
    private static final String TAG = "SharedPreferences";

    private final long handle;
    private final String name;

    SharedPreferences(String name) {
        this.name = name;
        Object result = callBridge("preferencesOpen",
                new Class<?>[]{ String.class }, name);
        this.handle = (result instanceof Number) ? ((Number) result).longValue() : 0L;
    }

    // ── Reflection helper ───────────────────────────────────────────

    private static Object callBridge(String method, Class<?>[] types, Object... args) {
        try {
            Class<?> c = Class.forName("com.ohos.shim.bridge.OHBridge");
            return c.getMethod(method, types).invoke(null, args);
        } catch (Throwable t) { return null; }
    }

    // ── Getters ─────────────────────────────────────────────────────

    public String getString(String key, String defValue) {
        Object result = callBridge("preferencesGetString",
                new Class<?>[]{ long.class, String.class, String.class },
                handle, key, defValue);
        return (result instanceof String) ? (String) result : defValue;
    }

    public int getInt(String key, int defValue) {
        Object result = callBridge("preferencesGetInt",
                new Class<?>[]{ long.class, String.class, int.class },
                handle, key, defValue);
        return (result instanceof Number) ? ((Number) result).intValue() : defValue;
    }

    public long getLong(String key, long defValue) {
        Object result = callBridge("preferencesGetLong",
                new Class<?>[]{ long.class, String.class, long.class },
                handle, key, defValue);
        return (result instanceof Number) ? ((Number) result).longValue() : defValue;
    }

    public float getFloat(String key, float defValue) {
        Object result = callBridge("preferencesGetFloat",
                new Class<?>[]{ long.class, String.class, float.class },
                handle, key, defValue);
        return (result instanceof Number) ? ((Number) result).floatValue() : defValue;
    }

    public boolean getBoolean(String key, boolean defValue) {
        Object result = callBridge("preferencesGetBoolean",
                new Class<?>[]{ long.class, String.class, boolean.class },
                handle, key, defValue);
        return (result instanceof Boolean) ? (Boolean) result : defValue;
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        // OH Preferences doesn't have native string set support.
        // Store as unit-separator-joined string internally.
        Object result = callBridge("preferencesGetString",
                new Class<?>[]{ long.class, String.class, String.class },
                handle, key, null);
        if (!(result instanceof String)) return defValues;
        String raw = (String) result;
        Set<String> out = new HashSet<String>();
        for (String s : raw.split("\u001F")) { // unit separator
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    public Map<String, ?> getAll() {
        // Limited implementation -- OH Preferences doesn't expose getAll easily.
        // Return empty map as fallback.
        return new HashMap<String, Object>();
    }

    public boolean contains(String key) {
        // Check by reading with a sentinel default
        Object result = callBridge("preferencesGetString",
                new Class<?>[]{ long.class, String.class, String.class },
                handle, key, null);
        return result != null;
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
        private final Map<String, Object> pending = new HashMap<String, Object>();
        private final Set<String> removals = new HashSet<String>();
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
            pending.put(key, Integer.valueOf(value));
            removals.remove(key);
            return this;
        }

        public Editor putLong(String key, long value) {
            pending.put(key, Long.valueOf(value));
            removals.remove(key);
            return this;
        }

        public Editor putFloat(String key, float value) {
            pending.put(key, Float.valueOf(value));
            removals.remove(key);
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            pending.put(key, Boolean.valueOf(value));
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
                android.util.Log.e(TAG, "apply() failed", e);
            }
        }

        private void applyInternal() {
            if (clearRequested) {
                callBridge("preferencesClear",
                        new Class<?>[]{ long.class }, handle);
            }

            for (String key : removals) {
                callBridge("preferencesRemove",
                        new Class<?>[]{ long.class, String.class }, handle, key);
            }

            for (Map.Entry<String, Object> entry : pending.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    callBridge("preferencesPutString",
                            new Class<?>[]{ long.class, String.class, String.class },
                            handle, key, (String) value);
                } else if (value instanceof Integer) {
                    callBridge("preferencesPutInt",
                            new Class<?>[]{ long.class, String.class, int.class },
                            handle, key, (Integer) value);
                } else if (value instanceof Long) {
                    callBridge("preferencesPutLong",
                            new Class<?>[]{ long.class, String.class, long.class },
                            handle, key, (Long) value);
                } else if (value instanceof Float) {
                    callBridge("preferencesPutFloat",
                            new Class<?>[]{ long.class, String.class, float.class },
                            handle, key, (Float) value);
                } else if (value instanceof Boolean) {
                    callBridge("preferencesPutBoolean",
                            new Class<?>[]{ long.class, String.class, boolean.class },
                            handle, key, (Boolean) value);
                }
            }

            callBridge("preferencesFlush",
                    new Class<?>[]{ long.class }, handle);
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
