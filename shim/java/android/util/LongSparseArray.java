package android.util;

/**
 * Stub for android.util.LongSparseArray — sparse array keyed by long.
 */
public class LongSparseArray<E> {
    private static final Object DELETED = new Object();

    public LongSparseArray() {}

    public LongSparseArray(int initialCapacity) {}

    public E get(long key) {
        return null;
    }

    public E get(long key, E valueIfKeyNotFound) {
        return valueIfKeyNotFound;
    }

    public void put(long key, E value) {}

    public void remove(long key) {}

    public void delete(long key) {}

    public void removeAt(int index) {}

    public int size() {
        return 0;
    }

    public long keyAt(int index) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public E valueAt(int index) {
        return null;
    }

    public void setValueAt(int index, E value) {}

    public int indexOfKey(long key) {
        return -1;
    }

    public int indexOfValue(E value) {
        return -1;
    }

    public void clear() {}

    public void append(long key, E value) {}

    @Override
    public LongSparseArray<E> clone() {
        return new LongSparseArray<>();
    }

    @Override
    public String toString() {
        return "LongSparseArray{}";
    }
}
