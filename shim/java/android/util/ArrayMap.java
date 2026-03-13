package android.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.util.ArrayMap<K,V>
 * Implements {@link Map} backed by a {@link LinkedHashMap} for simplicity.
 * Provides Android's index-based accessors (keyAt, valueAt, etc.) via an
 * ordered key list derived from insertion order.
 * Pure Java — no Android or OHBridge dependencies.
 */
public class ArrayMap<K, V> implements Map<K, V> {

    private final LinkedHashMap<K, V> mMap;

    public ArrayMap() {
        mMap = new LinkedHashMap<>();
    }

    public ArrayMap(int capacity) {
        mMap = new LinkedHashMap<>(Math.max(capacity, 0) * 2, 0.75f);
    }

    public ArrayMap(ArrayMap<K, V> map) {
        mMap = new LinkedHashMap<>();
        if (map != null) mMap.putAll(map.mMap);
    }

    // -----------------------------------------------------------------------
    // Standard Map interface
    // -----------------------------------------------------------------------

    @Override public int     size()                          { return mMap.size(); }
    @Override public boolean isEmpty()                       { return mMap.isEmpty(); }
    @Override public boolean containsKey(Object key)        { return mMap.containsKey(key); }
    @Override public boolean containsValue(Object value)    { return mMap.containsValue(value); }
    @Override public V       get(Object key)                { return mMap.get(key); }
    @Override public V       put(K key, V value)            { return mMap.put(key, value); }
    @Override public V       remove(Object key)             { return mMap.remove(key); }
    @Override public void    putAll(Map<? extends K, ? extends V> m) { mMap.putAll(m); }
    @Override public void    clear()                        { mMap.clear(); }
    @Override public Set<K>  keySet()                       { return mMap.keySet(); }
    @Override public Collection<V> values()                 { return mMap.values(); }
    @Override public Set<Map.Entry<K, V>> entrySet()        { return mMap.entrySet(); }

    // -----------------------------------------------------------------------
    // Android index-based extensions
    // -----------------------------------------------------------------------

    /**
     * Returns the key at the given index (0-based, insertion order).
     * O(n) — use sparingly.
     */
    public K keyAt(int index) {
        return keyList().get(index);
    }

    /**
     * Returns the value at the given index (0-based, insertion order).
     * O(n) — use sparingly.
     */
    public V valueAt(int index) {
        return mMap.get(keyAt(index));
    }

    /**
     * Replaces the value at the given index.
     * O(n) — use sparingly.
     */
    public V setValueAt(int index, V value) {
        K key = keyAt(index);
        return mMap.put(key, value);
    }

    /**
     * Returns the index of the specified key, or -1 if not present.
     * O(n) — use sparingly.
     */
    public int indexOfKey(Object key) {
        List<K> keys = keyList();
        for (int i = 0, n = keys.size(); i < n; i++) {
            K k = keys.get(i);
            if (key == null ? k == null : key.equals(k)) return i;
        }
        return -1;
    }

    // -----------------------------------------------------------------------
    // Object overrides
    // -----------------------------------------------------------------------

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ArrayMap) return mMap.equals(((ArrayMap<?,?>) o).mMap);
        if (o instanceof Map)      return mMap.equals(o);
        return false;
    }

    @Override public int    hashCode()   { return mMap.hashCode(); }
    @Override public String toString()   { return mMap.toString(); }

    // -----------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------

    private List<K> keyList() {
        return new ArrayList<>(mMap.keySet());
    }
}
