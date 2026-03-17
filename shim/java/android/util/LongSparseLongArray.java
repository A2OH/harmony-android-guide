package android.util;

/**
 * AOSP-compatible LongSparseLongArray: maps long keys to long values.
 * Used by View.measure() for the measure cache.
 *
 * Simple array-based implementation (sufficient for small cache sizes).
 */
public class LongSparseLongArray {
    private long[] mKeys;
    private long[] mValues;
    private int mSize;

    public LongSparseLongArray() {
        this(10);
    }

    public LongSparseLongArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        mKeys = new long[initialCapacity];
        mValues = new long[initialCapacity];
        mSize = 0;
    }

    public int size() { return mSize; }

    public long keyAt(int index) {
        return mKeys[index];
    }

    public long valueAt(int index) {
        return mValues[index];
    }

    public int indexOfKey(long key) {
        for (int i = 0; i < mSize; i++) {
            if (mKeys[i] == key) return i;
        }
        return -1;
    }

    public void put(long key, long value) {
        int idx = indexOfKey(key);
        if (idx >= 0) {
            mValues[idx] = value;
        } else {
            if (mSize >= mKeys.length) {
                int newCap = mKeys.length * 2;
                long[] newKeys = new long[newCap];
                long[] newValues = new long[newCap];
                System.arraycopy(mKeys, 0, newKeys, 0, mSize);
                System.arraycopy(mValues, 0, newValues, 0, mSize);
                mKeys = newKeys;
                mValues = newValues;
            }
            mKeys[mSize] = key;
            mValues[mSize] = value;
            mSize++;
        }
    }

    public long get(long key, long valueIfKeyNotFound) {
        int idx = indexOfKey(key);
        return idx >= 0 ? mValues[idx] : valueIfKeyNotFound;
    }

    public void clear() {
        mSize = 0;
    }

    public void delete(long key) {
        int idx = indexOfKey(key);
        if (idx >= 0) {
            System.arraycopy(mKeys, idx + 1, mKeys, idx, mSize - idx - 1);
            System.arraycopy(mValues, idx + 1, mValues, idx, mSize - idx - 1);
            mSize--;
        }
    }
}
