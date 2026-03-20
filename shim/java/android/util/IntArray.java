package android.util;

/** Auto-generated stub for AOSP compilation. */
public class IntArray {
    private int[] mValues = new int[10];
    private int mSize = 0;

    public IntArray() {}
    public IntArray(int initialCapacity) { mValues = new int[initialCapacity]; }

    public void add(int value) {
        if (mSize >= mValues.length) {
            int[] newValues = new int[mValues.length * 2];
            System.arraycopy(mValues, 0, newValues, 0, mSize);
            mValues = newValues;
        }
        mValues[mSize++] = value;
    }
    public int get(int index) { return mValues[index]; }
    public int size() { return mSize; }
    public void clear() { mSize = 0; }
    public int binarySearch(int value) { return java.util.Arrays.binarySearch(mValues, 0, mSize, value); }
    public int[] toArray() { return java.util.Arrays.copyOf(mValues, mSize); }
    public int[] getRawArray() { return mValues; }
}
