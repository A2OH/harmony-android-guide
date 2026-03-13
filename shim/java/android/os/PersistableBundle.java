package android.os;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Android-compatible PersistableBundle shim.
 * Only persists String, int, long, double, boolean, String[].
 */
public final class PersistableBundle {
    private final Map<String, Object> mMap = new HashMap<>();

    public PersistableBundle() {}

    public PersistableBundle(int capacity) {}

    public PersistableBundle(PersistableBundle b) {
        if (b != null) mMap.putAll(b.mMap);
    }

    // --- put ---
    public void putString(String key, String value)      { mMap.put(key, value); }
    public void putInt(String key, int value)            { mMap.put(key, value); }
    public void putLong(String key, long value)          { mMap.put(key, value); }
    public void putDouble(String key, double value)      { mMap.put(key, value); }
    public void putBoolean(String key, boolean value)    { mMap.put(key, value); }
    public void putStringArray(String key, String[] val) { mMap.put(key, val); }
    public void putIntArray(String key, int[] val)       { mMap.put(key, val); }
    public void putLongArray(String key, long[] val)     { mMap.put(key, val); }
    public void putDoubleArray(String key, double[] val) { mMap.put(key, val); }

    // --- get ---
    public String  getString(String key)               { return getString(key, null); }
    public String  getString(String key, String def)   { Object v = mMap.get(key); return (v instanceof String) ? (String) v : def; }
    public int     getInt(String key)                  { return getInt(key); }
    public int     getInt(String key, int def)         { Object v = mMap.get(key); return (v instanceof Integer) ? (Integer) v : def; }
    public long    getLong(String key)                 { return getLong(key); }
    public long    getLong(String key, long def)       { Object v = mMap.get(key); return (v instanceof Long) ? (Long) v : def; }
    public double  getDouble(String key)               { return getDouble(key, 0.0); }
    public double  getDouble(String key, double def)   { Object v = mMap.get(key); return (v instanceof Double) ? (Double) v : def; }
    public boolean getBoolean(String key)              { return getBoolean(key, false); }
    public boolean getBoolean(String key, boolean def) { Object v = mMap.get(key); return (v instanceof Boolean) ? (Boolean) v : def; }
    public String[] getStringArray(String key)         { Object v = mMap.get(key); return (v instanceof String[]) ? (String[]) v : null; }

    // --- metadata ---
    public boolean containsKey(String key) { return mMap.containsKey(key); }
    public void    remove(String key)      { mMap.remove(key); }
    public void    clear()                 { mMap.clear(); }
    public boolean isEmpty()              { return mMap.isEmpty(); }
    public int     size()                 { return mMap.size(); }
    public Set<String> keySet()           { return mMap.keySet(); }

    @Override
    public String toString() { return "PersistableBundle[" + mMap + "]"; }
}
