package android.text;

/**
 * Stub: PackedIntVector — growable 2D array of ints.
 * Used by DynamicLayout for storing line metadata.
 */
class PackedIntVector {

    private int mColumns;
    private int[][] mValues;
    private int mRows;

    public PackedIntVector(int columns) {
        mColumns = columns;
        mValues = new int[16][columns];
        mRows = 0;
    }

    public int getValue(int row, int column) {
        if (row < 0 || row >= mRows || column < 0 || column >= mColumns) return 0;
        return mValues[row][column];
    }

    public void setValue(int row, int column, int value) {
        if (row < 0 || row >= mRows || column < 0 || column >= mColumns) return;
        mValues[row][column] = value;
    }

    public void setValueInternal(int row, int column, int value) {
        setValue(row, column, value);
    }

    public int size() {
        return mRows;
    }

    public int width() {
        return mColumns;
    }

    public void insertAt(int row, int[] values) {
        if (mRows >= mValues.length) {
            int[][] newValues = new int[mValues.length * 2][mColumns];
            for (int i = 0; i < mRows; i++) {
                newValues[i] = mValues[i];
            }
            mValues = newValues;
        }
        // Shift rows down
        for (int i = mRows; i > row; i--) {
            mValues[i] = mValues[i - 1];
        }
        mValues[row] = new int[mColumns];
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

    public void adjustValuesBelow(int startRow, int column, int delta) {
        for (int i = startRow; i < mRows; i++) {
            mValues[i][column] += delta;
        }
    }
}
