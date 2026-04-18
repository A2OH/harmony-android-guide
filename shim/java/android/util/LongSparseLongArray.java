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
    private int mInitialCapacity;

    public LongSparseLongArray() {
        this(10);
    }

    public LongSparseLongArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        mInitialCapacity = initialCapacity;
        mKeys = new long[initialCapacity];
        mValues = new long[initialCapacity];
        mSize = 0;
    }

    private void ensureStorage(int minCapacity) {
        if (mInitialCapacity < 1) {
            mInitialCapacity = 1;
        }
        if (mSize < 0) {
            mSize = 0;
        }
        if (minCapacity < 1) {
            minCapacity = Math.max(mInitialCapacity, 1);
        }
        if (mKeys == null || mValues == null) {
            int cap = Math.max(minCapacity, Math.max(mInitialCapacity, mSize));
            if (cap < 1) {
                cap = 1;
            }
            mKeys = new long[cap];
            mValues = new long[cap];
            if (mSize > cap) {
                mSize = cap;
            }
            return;
        }
        int currentCap = Math.min(mKeys.length, mValues.length);
        if (currentCap < minCapacity) {
            int newCap = currentCap < 1 ? 1 : currentCap;
            while (newCap < minCapacity) {
                newCap *= 2;
            }
            long[] newKeys = new long[newCap];
            long[] newValues = new long[newCap];
            if (mSize > currentCap) {
                mSize = currentCap;
            }
            System.arraycopy(mKeys, 0, newKeys, 0, mSize);
            System.arraycopy(mValues, 0, newValues, 0, mSize);
            mKeys = newKeys;
            mValues = newValues;
        }
    }

    public int size() {
        if (mSize < 0) {
            mSize = 0;
        }
        return mSize;
    }

    public long keyAt(int index) {
        ensureStorage(index + 1);
        return mKeys[index];
    }

    public long valueAt(int index) {
        ensureStorage(index + 1);
        return mValues[index];
    }

    public int indexOfKey(long key) {
        if (mKeys == null || mValues == null || mSize <= 0) {
            if (mSize < 0) {
                mSize = 0;
            }
            return -1;
        }
        for (int i = 0; i < mSize; i++) {
            if (mKeys[i] == key) return i;
        }
        return -1;
    }

    public void put(long key, long value) {
        ensureStorage(Math.max(mSize + 1, 1));
        int idx = indexOfKey(key);
        if (idx >= 0) {
            mValues[idx] = value;
        } else {
            ensureStorage(mSize + 1);
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
