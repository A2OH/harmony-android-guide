package android.util;

/**
 * SparseArray maps integers to Objects, intended to be more memory-efficient
 * than using a HashMap to map Integers to Objects. Matches AOSP API surface.
 */
@SuppressWarnings("unchecked")
public class SparseArray<E> implements Cloneable {

    private static final Object DELETED = new Object();

    private int[] mKeys;
    private Object[] mValues;
    private int mSize;
    private boolean mGarbage;

    public SparseArray() {
        this(10);
    }

    public SparseArray(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = 0;
        mKeys = new int[initialCapacity];
        mValues = new Object[initialCapacity];
        mSize = 0;
        mGarbage = false;
    }

    @Override
    public SparseArray<E> clone() {
        SparseArray<E> c = null;
        try {
            c = (SparseArray<E>) super.clone();
            c.mKeys = mKeys.clone();
            c.mValues = mValues.clone();
        } catch (CloneNotSupportedException e) {
            // Cannot happen
        }
        return c;
    }

    /**
     * Gets the Object mapped from the specified key, or null if no such mapping.
     */
    public E get(int key) {
        return get(key, null);
    }

    /**
     * Gets the Object mapped from the specified key, or the specified Object if no such mapping.
     */
    public E get(int key, E valueIfKeyNotFound) {
        if (mGarbage) gc();
        int i = binarySearch(mKeys, mSize, key);
        if (i < 0 || mValues[i] == DELETED) {
            return valueIfKeyNotFound;
        }
        return (E) mValues[i];
    }

    /**
     * Removes the mapping from the specified key, if there was any.
     */
    public void delete(int key) {
        if (mGarbage) gc();
        int i = binarySearch(mKeys, mSize, key);
        if (i >= 0 && mValues[i] != DELETED) {
            mValues[i] = DELETED;
            mGarbage = true;
        }
    }

    /**
     * Alias for delete(int).
     */
    public void remove(int key) {
        delete(key);
    }

    /**
     * Removes the mapping at the specified index.
     */
    public void removeAt(int index) {
        if (mGarbage) gc();
        if (index >= 0 && index < mSize && mValues[index] != DELETED) {
            mValues[index] = DELETED;
            mGarbage = true;
        }
    }

    /**
     * Remove a range of mappings as a batch.
     */
    public void removeAtRange(int index, int size) {
        if (mGarbage) gc();
        int end = Math.min(mSize, index + size);
        for (int i = index; i < end; i++) {
            if (mValues[i] != DELETED) {
                mValues[i] = DELETED;
                mGarbage = true;
            }
        }
    }

    /**
     * Returns true if the key exists in the mapping.
     */
    public boolean contains(int key) {
        return indexOfKey(key) >= 0;
    }

    /**
     * Adds a mapping from the specified key to the specified value, replacing the previous
     * mapping if there was one.
     */
    public void put(int key, E value) {
        if (mGarbage) gc();
        int i = binarySearch(mKeys, mSize, key);
        if (i >= 0) {
            mValues[i] = value;
        } else {
            i = ~i;
            if (i < mSize && mValues[i] == DELETED) {
                mKeys[i] = key;
                mValues[i] = value;
                return;
            }
            if (mGarbage && mSize >= mKeys.length) {
                gc();
                i = ~binarySearch(mKeys, mSize, key);
            }
            ensureCapacity(mSize + 1);
            if (i < mSize) {
                System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
                System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
            }
            mKeys[i] = key;
            mValues[i] = value;
            mSize++;
        }
    }

    /**
     * Returns the number of key-value mappings.
     */
    public int size() {
        if (mGarbage) gc();
        return mSize;
    }

    /**
     * Given an index in the range 0...size()-1, returns the key.
     */
    public int keyAt(int index) {
        if (mGarbage) gc();
        return mKeys[index];
    }

    /**
     * Given an index in the range 0...size()-1, returns the value.
     */
    public E valueAt(int index) {
        if (mGarbage) gc();
        return (E) mValues[index];
    }

    /**
     * Given an index in the range 0...size()-1, sets a new value.
     */
    public void setValueAt(int index, E value) {
        if (mGarbage) gc();
        mValues[index] = value;
    }

    /**
     * Returns the index for which keyAt would return the specified key,
     * or a negative number if the key is not mapped.
     */
    public int indexOfKey(int key) {
        if (mGarbage) gc();
        return binarySearch(mKeys, mSize, key);
    }

    /**
     * Returns an index for which valueAt would return the specified value,
     * or a negative number if no such value exists.
     */
    public int indexOfValue(E value) {
        if (mGarbage) gc();
        for (int i = 0; i < mSize; i++) {
            if (mValues[i] == value) return i;
        }
        return -1;
    }

    /**
     * Removes all key-value mappings.
     */
    public void clear() {
        int n = mSize;
        Object[] values = mValues;
        for (int i = 0; i < n; i++) {
            values[i] = null;
        }
        mSize = 0;
        mGarbage = false;
    }

    /**
     * Puts a key/value pair, optimized for the case where the key is greater than all existing keys.
     */
    public void append(int key, E value) {
        if (mSize > 0 && key > mKeys[mSize - 1]) {
            if (mGarbage && mSize >= mKeys.length) {
                gc();
            }
            ensureCapacity(mSize + 1);
            mKeys[mSize] = key;
            mValues[mSize] = value;
            mSize++;
        } else {
            put(key, value);
        }
    }

    @Override
    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(mSize * 28);
        buffer.append('{');
        for (int i = 0; i < mSize; i++) {
            if (i > 0) buffer.append(", ");
            buffer.append(mKeys[i]);
            buffer.append('=');
            Object val = mValues[i];
            if (val != this) {
                buffer.append(val);
            } else {
                buffer.append("(this Map)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    // --- Stub-compat overloads that accept Object (for backward compatibility) ---

    /** @hide compat overload */
    public void append(Object p0, Object p1) {
        append(((Number) p0).intValue(), (E) p1);
    }
    /** @hide compat overload */
    public boolean contains(Object p0) {
        return contains(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public void delete(Object p0) {
        delete(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public Object get(Object p0) {
        return get(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public Object get(Object p0, Object p1) {
        return get(((Number) p0).intValue(), (E) p1);
    }
    /** @hide compat overload */
    public int indexOfKey(Object p0) {
        return indexOfKey(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public int keyAt(Object p0) {
        return keyAt(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public void put(Object p0, Object p1) {
        put(((Number) p0).intValue(), (E) p1);
    }
    /** @hide compat overload */
    public void remove(Object p0) {
        remove(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public void removeAt(Object p0) {
        removeAt(((Number) p0).intValue());
    }
    /** @hide compat overload */
    public void removeAtRange(Object p0, Object p1) {
        removeAtRange(((Number) p0).intValue(), ((Number) p1).intValue());
    }
    /** @hide compat overload */
    public void setValueAt(Object p0, Object p1) {
        setValueAt(((Number) p0).intValue(), (E) p1);
    }
    /** @hide compat overload */
    public Object valueAt(Object p0) {
        return valueAt(((Number) p0).intValue());
    }

    // --- Internal helpers ---

    private void gc() {
        int n = mSize;
        int o = 0;
        int[] keys = mKeys;
        Object[] values = mValues;
        for (int i = 0; i < n; i++) {
            Object val = values[i];
            if (val != DELETED) {
                if (i != o) {
                    keys[o] = keys[i];
                    values[o] = val;
                    values[i] = null;
                }
                o++;
            }
        }
        mGarbage = false;
        mSize = o;
    }

    private void ensureCapacity(int minCapacity) {
        if (mKeys.length < minCapacity) {
            int newCapacity = Math.max(minCapacity, mKeys.length * 2);
            int[] newKeys = new int[newCapacity];
            Object[] newValues = new Object[newCapacity];
            System.arraycopy(mKeys, 0, newKeys, 0, mSize);
            System.arraycopy(mValues, 0, newValues, 0, mSize);
            mKeys = newKeys;
            mValues = newValues;
        }
    }

    private static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midVal = array[mid];
            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        return ~lo;
    }
}
