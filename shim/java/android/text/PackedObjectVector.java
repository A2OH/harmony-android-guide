package android.text;

/**
 * Stub: PackedObjectVector — growable 2D array of Objects.
 * Used by DynamicLayout for storing line direction objects.
 */
class PackedObjectVector<E> {

    private int mColumns;
    private Object[][] mValues;
    private int mRows;

    public PackedObjectVector(int columns) {
        mColumns = columns;
        mValues = new Object[16][columns];
        mRows = 0;
    }

    @SuppressWarnings("unchecked")
    public E getValue(int row, int column) {
        if (row < 0 || row >= mRows || column < 0 || column >= mColumns) return null;
        return (E) mValues[row][column];
    }

    public void setValue(int row, int column, E value) {
        if (row < 0 || row >= mRows || column < 0 || column >= mColumns) return;
        mValues[row][column] = value;
    }

    public int size() {
        return mRows;
    }

    public int width() {
        return mColumns;
    }

    public void insertAt(int row, E[] values) {
        if (mRows >= mValues.length) {
            Object[][] newValues = new Object[mValues.length * 2][mColumns];
            for (int i = 0; i < mRows; i++) {
                newValues[i] = mValues[i];
            }
            mValues = newValues;
        }
        for (int i = mRows; i > row; i--) {
            mValues[i] = mValues[i - 1];
        }
        mValues[row] = new Object[mColumns];
        if (values != null) {
            int len = Math.min(values.length, mColumns);
            for (int i = 0; i < len; i++) {
                mValues[row][i] = values[i];
            }
        }
        mRows++;
    }

    public void deleteAt(int row, int count) {
        if (row < 0 || row >= mRows) return;
        int end = Math.min(row + count, mRows);
        int shift = end - row;
        for (int i = row; i + shift < mRows; i++) {
            mValues[i] = mValues[i + shift];
        }
        mRows -= shift;
    }
}
