package android.os;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.os.BaseBundle — abstract base class providing common
 * key-value storage for Bundle and PersistableBundle.
 */
public abstract class BaseBundle {

    final Map<String, Object> map;

    BaseBundle() {
        this.map = new HashMap<>();
    }

    BaseBundle(int capacity) {
        this.map = new HashMap<>(capacity);
    }

    BaseBundle(BaseBundle other) {
        this.map = new HashMap<>(other.map);
    }

    // ── Size / state ──

    public int size() { return map.size(); }
    public boolean isEmpty() { return map.isEmpty(); }
    public void clear() { map.clear(); }
    public boolean containsKey(String key) { return map.containsKey(key); }
    public void remove(String key) { map.remove(key); }
    public Set<String> keySet() { return map.keySet(); }

    // ── Object get ──

    public Object get(String key) { return map.get(key); }

    // ── String ──

    public void putString(String key, String value) { map.put(key, value); }
    public String getString(String key) { return getString(key, null); }
    public String getString(String key, String defaultValue) {
        Object v = map.get(key);
        return v instanceof String ? (String) v : defaultValue;
    }

    // ── int ──

    public void putInt(String key, int value) { map.put(key, value); }
    public int getInt(String key) { return getInt(key, 0); }
    public int getInt(String key, int defaultValue) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).intValue() : defaultValue;
    }

    // ── long ──

    public void putLong(String key, long value) { map.put(key, value); }
    public long getLong(String key) { return getLong(key, 0L); }
    public long getLong(String key, long defaultValue) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).longValue() : defaultValue;
    }

    // ── double ──

    public void putDouble(String key, double value) { map.put(key, value); }
    public double getDouble(String key) { return getDouble(key, 0.0); }
    public double getDouble(String key, double defaultValue) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).doubleValue() : defaultValue;
    }

    // ── boolean ──

    public void putBoolean(String key, boolean value) { map.put(key, value); }
    public boolean getBoolean(String key) { return getBoolean(key, false); }
    public boolean getBoolean(String key, boolean defaultValue) {
        Object v = map.get(key);
        return v instanceof Boolean ? (Boolean) v : defaultValue;
    }

    // ── String[] ──

    public void putStringArray(String key, String[] value) { map.put(key, value); }
    public String[] getStringArray(String key) {
        Object v = map.get(key);
        return v instanceof String[] ? (String[]) v : null;
    }

    // ── int[] ──

    public void putIntArray(String key, int[] value) { map.put(key, value); }
    public int[] getIntArray(String key) {
        Object v = map.get(key);
        return v instanceof int[] ? (int[]) v : null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + map.toString() + "]";
    }
}
