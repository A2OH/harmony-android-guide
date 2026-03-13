package android.util;

import java.util.Arrays;

/**
 * Shim: android.util.SparseLongArray
 * Maps int keys to long values using two parallel sorted arrays.
 * Pure Java — no Android or OHBridge dependencies.
 */
public class SparseLongArray implements Cloneable {

    private static final int DEFAULT_CAPACITY = 10;

    private int[]  mKeys;
    private long[] mValues;
    private int    mSize;

    public SparseLongArray() {
        this(DEFAULT_CAPACITY);
    }

    public SparseLongArray(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = 0;
        mKeys   = new int[initialCapacity];
        mValues = new long[initialCapacity];
        mSize   = 0;
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    public void put(int key, long value) {
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

    public long get(int key) {
        return get(key);
    }

    public long get(int key, long valueIfKeyNotFound) {
        int i = Arrays.binarySearch(mKeys, 0, mSize, key);
        return i < 0 ? valueIfKeyNotFound : mValues[i];
    }

    public void delete(int key) {
        int i = Arrays.binarySearch(mKeys, 0, mSize, key);
        if (i >= 0) removeAt(i);
    }

    public void removeAt(int index) {
        checkIndex(index);
        System.arraycopy(mKeys,   index + 1, mKeys,   index, mSize - index - 1);
        System.arraycopy(mValues, index + 1, mValues, index, mSize - index - 1);
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

    public long valueAt(int index) {
        checkIndex(index);
        return mValues[index];
    }

    public void setValueAt(int index, long value) {
        checkIndex(index);
        mValues[index] = value;
    }

    // -----------------------------------------------------------------------
    // Search
    // -----------------------------------------------------------------------

    public int indexOfKey(int key) {
        return Arrays.binarySearch(mKeys, 0, mSize, key);
    }

    public int indexOfValue(long value) {
        for (int i = 0; i < mSize; i++) {
            if (mValues[i] == value) return i;
        }
        return -1;
    }

    // -----------------------------------------------------------------------
    // Bulk
    // -----------------------------------------------------------------------

    public void clear() {
        mSize = 0;
    }

    public void append(int key, long value) {
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
    public SparseLongArray clone() {
        SparseLongArray clone;
        try {
            clone = (SparseLongArray) super.clone();
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
            sb.append(mValues[i]);
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
