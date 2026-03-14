package android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.content.SharedPreferences
 *
 * Backed by a HashMap (pure Java, no JNI/OHBridge needed).
 * Each named SharedPreferences instance maintains its own in-memory map.
 * Editor supports put*, remove, clear, commit, and apply.
 * Change listeners are notified on commit/apply.
 */
public class SharedPreferences {
    private static final String TAG = "SharedPreferences";

    /** Global registry of named preference stores, so the same name returns the same data. */
    private static final Map<String, SharedPreferences> sInstances =
            new HashMap<String, SharedPreferences>();

    private final String name;
    private final Map<String, Object> store = new HashMap<String, Object>();
    private final List<OnSharedPreferenceChangeListener> listeners =
            new ArrayList<OnSharedPreferenceChangeListener>();

    /**
     * Package-private constructor. Use Context.getSharedPreferences() or
     * SharedPreferences.getInstance() to obtain instances.
     */
    SharedPreferences(String name) {
        this.name = name;
    }

    /**
     * Returns a named SharedPreferences instance, creating it if needed.
     * Multiple callers with the same name share the same backing map.
     */
    public static synchronized SharedPreferences getInstance(String name) {
        SharedPreferences sp = sInstances.get(name);
        if (sp == null) {
            sp = new SharedPreferences(name);
            sInstances.put(name, sp);
        }
        return sp;
    }

    // ── Getters ─────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public Map<String, ?> getAll() {
        synchronized (store) {
            // Return a defensive copy
            Map<String, Object> copy = new HashMap<String, Object>();
            for (Map.Entry<String, Object> e : store.entrySet()) {
                Object v = e.getValue();
                // Deep-copy StringSets so caller can't mutate our store
                if (v instanceof Set) {
                    copy.put(e.getKey(), new HashSet<String>((Set<String>) v));
                } else {
                    copy.put(e.getKey(), v);
                }
            }
            return copy;
        }
    }

    public String getString(String key, String defValue) {
        synchronized (store) {
            Object v = store.get(key);
            return (v instanceof String) ? (String) v : defValue;
        }
    }

    public int getInt(String key, int defValue) {
        synchronized (store) {
            Object v = store.get(key);
            return (v instanceof Integer) ? (Integer) v : defValue;
        }
    }

    public long getLong(String key, long defValue) {
        synchronized (store) {
            Object v = store.get(key);
            return (v instanceof Long) ? (Long) v : defValue;
        }
    }

    public float getFloat(String key, float defValue) {
        synchronized (store) {
            Object v = store.get(key);
            return (v instanceof Float) ? (Float) v : defValue;
        }
    }

    public boolean getBoolean(String key, boolean defValue) {
        synchronized (store) {
            Object v = store.get(key);
            return (v instanceof Boolean) ? (Boolean) v : defValue;
        }
    }

    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String key, Set<String> defValues) {
        synchronized (store) {
            Object v = store.get(key);
            if (v instanceof Set) {
                // Return a defensive copy
                return new HashSet<String>((Set<String>) v);
            }
            return defValues;
        }
    }

    public boolean contains(String key) {
        synchronized (store) {
            return store.containsKey(key);
        }
    }

    public Editor edit() {
        return new Editor(this);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (listener == null) return;
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners(Set<String> changedKeys) {
        List<OnSharedPreferenceChangeListener> snapshot;
        synchronized (listeners) {
            if (listeners.isEmpty()) return;
            snapshot = new ArrayList<OnSharedPreferenceChangeListener>(listeners);
        }
        for (String key : changedKeys) {
            for (OnSharedPreferenceChangeListener l : snapshot) {
                l.onSharedPreferenceChanged(this, key);
            }
        }
    }

    // ── Editor ──────────────────────────────────────────────────────

    public static class Editor {
        private final SharedPreferences prefs;
        private final Map<String, Object> pending = new HashMap<String, Object>();
        private final Set<String> removals = new HashSet<String>();
        private boolean clearRequested = false;

        Editor(SharedPreferences prefs) {
            this.prefs = prefs;
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
            // Store a defensive copy
            pending.put(key, new HashSet<String>(values));
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
         * Writes pending changes to the backing store synchronously.
         * Returns true on success.
         */
        public boolean commit() {
            try {
                Set<String> changed = applyInternal();
                prefs.notifyListeners(changed);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Writes pending changes to the backing store.
         * In real Android this is asynchronous; in the shim it's synchronous.
         */
        public void apply() {
            try {
                Set<String> changed = applyInternal();
                prefs.notifyListeners(changed);
            } catch (Exception e) {
                // swallow, matching Android behavior
            }
        }

        private Set<String> applyInternal() {
            Set<String> changedKeys = new HashSet<String>();
            synchronized (prefs.store) {
                if (clearRequested) {
                    changedKeys.addAll(prefs.store.keySet());
                    prefs.store.clear();
                }

                for (String key : removals) {
                    if (prefs.store.containsKey(key)) {
                        changedKeys.add(key);
                        prefs.store.remove(key);
                    }
                }

                for (Map.Entry<String, Object> entry : pending.entrySet()) {
                    String key = entry.getKey();
                    Object newVal = entry.getValue();
                    Object oldVal = prefs.store.get(key);
                    if (oldVal == null || !oldVal.equals(newVal)) {
                        changedKeys.add(key);
                    }
                    prefs.store.put(key, newVal);
                }
            }

            pending.clear();
            removals.clear();
            clearRequested = false;
            return changedKeys;
        }
    }

    // ── Listener interface ──

    public interface OnSharedPreferenceChangeListener {
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
    }
}
