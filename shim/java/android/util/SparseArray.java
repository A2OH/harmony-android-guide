package android.util;

import java.util.Arrays;

/**
 * Shim: android.util.SparseArray<E>
 * Backed by two parallel sorted arrays (int[] keys, Object[] values).
 * Pure Java — no Android or OHBridge dependencies.
 */
@SuppressWarnings("unchecked")
public class SparseArray<E> implements Cloneable {

    private static final int DEFAULT_CAPACITY = 10;

    private int[]    mKeys;
    private Object[] mValues;
    private int      mSize;

    public SparseArray() {
        this(DEFAULT_CAPACITY);
    }

    public SparseArray(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = 0;
        mKeys   = new int[initialCapacity];
        mValues = new Object[initialCapacity];
        mSize   = 0;
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    public void put(int key, E value) {
        int i = Arrays.binarySearch(mKeys, 0, mSize, key);
        if (i >= 0) {
            mValues[i] = value;
        } else {
            i = ~i;
            ensureCapacity(mSize + 1);
            System.arraycopy(mKeys,   i, mKeys,   i + 1, mSize - i);
            System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
            mKeys[i]   = key;
            mValues[i] = value;
            mSize++;
        }
    }

    public E get(int key) {
        return get(key, null);
    }

    public E get(int key, E valueIfKeyNotFound) {
        int i = Arrays.binarySearch(mKeys, 0, mSize, key);
        if (i < 0) return valueIfKeyNotFound;
        return (E) mValues[i];
    }

    public void delete(int key) {
        int i = Arrays.binarySearch(mKeys, 0, mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    /** Alias for {@link #delete(int)}. */
    public void remove(int key) {
        delete(key);
    }

    public void removeAt(int index) {
        checkIndex(index);
        System.arraycopy(mKeys,   index + 1, mKeys,   index, mSize - index - 1);
        System.arraycopy(mValues, index + 1, mValues, index, mSize - index - 1);
        mValues[mSize - 1] = null; // allow GC
        mSize--;
    }

    // -----------------------------------------------------------------------
    // Indexed access
    // -----------------------------------------------------------------------

    public int size() {
        return mSize;
    }

    public int keyAt(int index) {
        checkIndex(index);
        return mKeys[index];
    }

    public E valueAt(int index) {
        checkIndex(index);
        return (E) mValues[index];
    }

    public void setValueAt(int index, E value) {
        checkIndex(index);
        mValues[index] = value;
    }

    // -----------------------------------------------------------------------
    // Search
    // -----------------------------------------------------------------------

    /**
     * Returns the index of the specified key, or a negative number if not found.
     * The negative return value encodes the insertion point as {@code -(insertion point) - 1},
     * matching {@link Arrays#binarySearch} semantics.
     */
    public int indexOfKey(int key) {
        return Arrays.binarySearch(mKeys, 0, mSize, key);
    }

    /** Returns the first index whose value equals {@code value}, or -1 if not found. */
    public int indexOfValue(E value) {
        for (int i = 0; i < mSize; i++) {
            if (value == null ? mValues[i] == null : value.equals(mValues[i])) {
                return i;
            }
        }
        return -1;
    }

    // -----------------------------------------------------------------------
    // Bulk
    // -----------------------------------------------------------------------

    public void clear() {
        Arrays.fill(mValues, 0, mSize, null);
        mSize = 0;
    }

    /**
     * Appends a key/value pair. The key must be greater than all existing keys;
     * otherwise falls back to {@link #put}.
     */
    public void append(int key, E value) {
        if (mSize > 0 && key <= mKeys[mSize - 1]) {
            put(key, value);
            return;
        }
        ensureCapacity(mSize + 1);
        mKeys[mSize]   = key;
        mValues[mSize] = value;
        mSize++;
    }

    // -----------------------------------------------------------------------
    // Object overrides
    // -----------------------------------------------------------------------

    @Override
    public SparseArray<E> clone() {
        SparseArray<E> clone;
        try {
            clone = (SparseArray<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        clone.mKeys   = mKeys.clone();
        clone.mValues = mValues.clone();
        return clone;
    }

    @Override
    public String toString() {
        if (mSize == 0) return "{}";
        StringBuilder sb = new StringBuilder(mSize * 12);
        sb.append('{');
        for (int i = 0; i < mSize; i++) {
            if (i > 0) sb.append(", ");
            sb.append(mKeys[i]);
            sb.append('=');
            sb.append(mValues[i] == this ? "(this SparseArray)" : mValues[i]);
        }
        sb.append('}');
        return sb.toString();
    }

    // -----------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------

    private void ensureCapacity(int needed) {
        if (needed <= mKeys.length) return;
        int newCap = Math.max(needed, mKeys.length * 2);
        mKeys   = Arrays.copyOf(mKeys,   newCap);
        mValues = Arrays.copyOf(mValues, newCap);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= mSize) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}
