package android.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache {

    private final LinkedHashMap map;
    private int maxSize;
    private int size;

    private int putCount;
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    public LruCache() {
        this(16);
    }

    public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap(0, 0.75f, true);
    }

    public final Object get(Object key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        Object mapValue;
        synchronized (this) {
            mapValue = map.get(key);
            if (mapValue != null) {
                hitCount++;
                return mapValue;
            }
            missCount++;
        }

        // Attempt to create a value
        Object createdValue = create(key);
        if (createdValue == null) {
            return null;
        }

        synchronized (this) {
            createCount++;
            mapValue = map.put(key, createdValue);
            if (mapValue != null) {
                // Conflict: put the old value back
                map.put(key, mapValue);
            } else {
                size += safeSizeOf(key, createdValue);
            }
        }

        if (mapValue != null) {
            entryRemoved(false, key, createdValue, mapValue);
            return mapValue;
        } else {
            trimToSize(maxSize);
            return createdValue;
        }
    }

    public final Object put(Object key, Object value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }

        Object previous;
        synchronized (this) {
            putCount++;
            size += safeSizeOf(key, value);
            previous = map.put(key, value);
            if (previous != null) {
                size -= safeSizeOf(key, previous);
            }
        }

        if (previous != null) {
            entryRemoved(false, key, previous, value);
        }
        trimToSize(maxSize);
        return previous;
    }

    public void trimToSize(Object maxSizeObj) {
        int targetSize;
        if (maxSizeObj instanceof Integer) {
            targetSize = (Integer) maxSizeObj;
        } else if (maxSizeObj instanceof Number) {
            targetSize = ((Number) maxSizeObj).intValue();
        } else {
            return;
        }
        trimToSize(targetSize);
    }

    public void trimToSize(int targetSize) {
        while (true) {
            Object key;
            Object value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(
                        "sizeOf() is reporting inconsistent results");
                }

                if (size <= targetSize || map.isEmpty()) {
                    break;
                }

                Map.Entry toEvict = (Map.Entry) map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= safeSizeOf(key, value);
                evictionCount++;
            }

            entryRemoved(true, key, value, null);
        }
    }

    public final Object remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        Object previous;
        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) {
                size -= safeSizeOf(key, previous);
            }
        }

        if (previous != null) {
            entryRemoved(false, key, previous, null);
        }

        return previous;
    }

    public void entryRemoved(Object evicted, Object key, Object oldValue, Object newValue) {
        // Overload to accept Object for stub compatibility
        boolean ev = (evicted instanceof Boolean) ? (Boolean) evicted : false;
        entryRemoved(ev, key, oldValue, newValue);
    }

    protected void entryRemoved(boolean evicted, Object key, Object oldValue, Object newValue) {
        // Override point — default is no-op
    }

    public Object create(Object key) {
        return null;
    }

    private int safeSizeOf(Object key, Object value) {
        int result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + "=" + value);
        }
        return result;
    }

    public int sizeOf(Object key, Object value) {
        return 1;
    }

    public final void evictAll() {
        trimToSize(-1); // -1 will evict everything
    }

    public final synchronized int size() {
        return size;
    }

    public final synchronized int maxSize() {
        return maxSize;
    }

    public final synchronized int hitCount() {
        return hitCount;
    }

    public final synchronized int missCount() {
        return missCount;
    }

    public final synchronized int createCount() {
        return createCount;
    }

    public final synchronized int putCount() {
        return putCount;
    }

    public final synchronized int evictionCount() {
        return evictionCount;
    }

    public final synchronized Map snapshot() {
        return new LinkedHashMap(map);
    }

    public void resize(Object maxSizeObj) {
        if (maxSizeObj instanceof Integer) {
            int newMaxSize = (Integer) maxSizeObj;
            if (newMaxSize <= 0) {
                throw new IllegalArgumentException("maxSize <= 0");
            }
            synchronized (this) {
                maxSize = newMaxSize;
            }
            trimToSize(newMaxSize);
        }
    }

    @Override
    public final synchronized String toString() {
        int accesses = hitCount + missCount;
        int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
        return "LruCache[maxSize=" + maxSize + ",hits=" + hitCount + ",misses=" + missCount + ",hitRate=" + hitPercent + "%]";
    }
}
