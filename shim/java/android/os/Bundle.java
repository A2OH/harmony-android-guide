package android.os;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.os.Bundle — pure Java implementation.
 * No OH bridge needed — Bundle is just a typed key-value container
 * used for passing data between components.
 *
 * Internally backed by a HashMap, same as AOSP.
 */
public final class Bundle implements Cloneable, Serializable {
    /** An empty, immutable Bundle. */
    public static final Bundle EMPTY = new Bundle();

    private final Map<String, Object> map;

    public Bundle() {
        this.map = new HashMap<>();
    }

    public Bundle(int capacity) {
        this.map = new HashMap<>(capacity);
    }

    public Bundle(Bundle other) {
        this.map = new HashMap<>(other.map);
    }

    // ── Size / state ──

    public int size() { return map.size(); }
    public boolean isEmpty() { return map.isEmpty(); }
    public void clear() { map.clear(); }
    public boolean containsKey(String key) { return map.containsKey(key); }
    public void remove(String key) { map.remove(key); }
    public Set<String> keySet() { return map.keySet(); }

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

    // ── float ──

    public void putFloat(String key, float value) { map.put(key, value); }
    public float getFloat(String key) { return getFloat(key, 0f); }
    public float getFloat(String key, float defaultValue) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).floatValue() : defaultValue;
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

    // ── byte[] ──

    public void putByteArray(String key, byte[] value) { map.put(key, value); }
    public byte[] getByteArray(String key) {
        Object v = map.get(key);
        return v instanceof byte[] ? (byte[]) v : null;
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

    // ── Bundle nesting ──

    public void putBundle(String key, Bundle value) { map.put(key, value); }
    public Bundle getBundle(String key) {
        Object v = map.get(key);
        return v instanceof Bundle ? (Bundle) v : null;
    }

    // ── Serializable ──

    public void putSerializable(String key, Serializable value) { map.put(key, value); }
    public Serializable getSerializable(String key) {
        Object v = map.get(key);
        return v instanceof Serializable ? (Serializable) v : null;
    }

    // ── Parcelable stub ──
    // Full Parcelable support requires deeper shim work; for now store as Object.

    public void putParcelable(String key, Object value) { map.put(key, value); }
    public Object getParcelable(String key) { return map.get(key); }

    // ── putAll ──

    public void putAll(Bundle other) {
        if (other != null) {
            map.putAll(other.map);
        }
    }

    // ── Generic get ──

    public Object get(String key) { return map.get(key); }

    @Override
    public Bundle clone() {
        return new Bundle(this);
    }

    @Override
    public String toString() {
        return "Bundle[" + map.toString() + "]";
    }

    /**
     * Convert to a flat JSON-like map for passing through the JNI bridge.
     * Used internally by Intent shim when calling OHBridge.startAbility().
     */
    public Map<String, Object> toMap() {
        return new HashMap<>(map);
    }
}
