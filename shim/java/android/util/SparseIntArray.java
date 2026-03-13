package android.util;

import java.util.Arrays;

/**
 * Shim: android.util.SparseIntArray
 * Maps int keys to int values using two parallel sorted arrays.
 * Pure Java — no Android or OHBridge dependencies.
 */
public class SparseIntArray implements Cloneable {

    private static final int DEFAULT_CAPACITY = 10;

    private int[] mKeys;
    private int[] mValues;
    private int   mSize;

    public SparseIntArray() {
        this(DEFAULT_CAPACITY);
    }

    public SparseIntArray(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = 0;
        mKeys   = new int[initialCapacity];
        mValues = new int[initialCapacity];
        mSize   = 0;
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    public void put(int key, int value) {
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

    public int get(int key) {
        return get(key, 0);
    }

    public int get(int key, int valueIfKeyNotFound) {
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

    public int valueAt(int index) {
        checkIndex(index);
        return mValues[index];
    }

    public void setValueAt(int index, int value) {
        checkIndex(index);
        mValues[index] = value;
    }

    // -----------------------------------------------------------------------
    // Search
    // -----------------------------------------------------------------------

    public int indexOfKey(int key) {
        return Arrays.binarySearch(mKeys, 0, mSize, key);
    }

    public int indexOfValue(int value) {
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

    public void append(int key, int value) {
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
    public SparseIntArray clone() {
        SparseIntArray clone;
        try {
            clone = (SparseIntArray) super.clone();
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
        StringBuilder sb = new StringBuilder(mSize * 10);
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
