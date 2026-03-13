package android.database;

import java.util.Iterator;

/**
 * Android-compatible CursorJoiner shim.
 * Performs a join of two {@link Cursor} objects on specified key columns.
 * Iterating produces {@link Result} values indicating which cursor(s) have data
 * for the current key: LEFT, RIGHT, or BOTH.
 *
 * Both cursors must be sorted in ascending order on the join columns.
 */
public final class CursorJoiner implements Iterator<CursorJoiner.Result>, Iterable<CursorJoiner.Result> {

    /**
     * Indicates which cursor has data for the current join row.
     */
    public enum Result {
        /** Only the left cursor has a row for this key. */
        LEFT,
        /** Only the right cursor has a row for this key. */
        RIGHT,
        /** Both cursors have a row for this key. */
        BOTH
    }

    private final Cursor mLeft;
    private final Cursor mRight;
    private final int[] mLeftColumnIndices;
    private final int[] mRightColumnIndices;

    private Result mNextValue;
    private boolean mNextReady;

    // State flags: whether each cursor still has rows available
    private boolean mLeftValid;
    private boolean mRightValid;

    /**
     * Creates a CursorJoiner that joins {@code left} and {@code right} on the
     * columns named in {@code leftColumns} and {@code rightColumns} respectively.
     * The column arrays must be the same length; each position is a join key.
     *
     * @param left         the left cursor, sorted ascending on leftColumns
     * @param leftColumns  the column names in the left cursor to join on
     * @param right        the right cursor, sorted ascending on rightColumns
     * @param rightColumns the column names in the right cursor to join on
     */
    public CursorJoiner(Cursor left, String[] leftColumns, Cursor right, String[] rightColumns) {
        if (leftColumns.length != rightColumns.length) {
            throw new IllegalArgumentException(
                    "leftColumns and rightColumns must have the same length");
        }
        mLeft = left;
        mRight = right;

        mLeftColumnIndices = buildColumnIndices(left, leftColumns);
        mRightColumnIndices = buildColumnIndices(right, rightColumns);

        mLeftValid  = mLeft.moveToFirst();
        mRightValid = mRight.moveToFirst();
        mNextReady  = false;
    }

    private static int[] buildColumnIndices(Cursor cursor, String[] columnNames) {
        int[] indices = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            indices[i] = cursor.getColumnIndexOrThrow(columnNames[i]);
        }
        return indices;
    }

    @Override
    public Iterator<Result> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        if (!mNextReady) {
            mNextReady = true;
            mNextValue = computeNext();
        }
        return mNextValue != null;
    }

    @Override
    public Result next() {
        if (!mNextReady) {
            mNextValue = computeNext();
        }
        mNextReady = false;
        if (mNextValue == null) {
            throw new java.util.NoSuchElementException();
        }
        return mNextValue;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("CursorJoiner does not support remove()");
    }

    private Result computeNext() {
        if (!mLeftValid && !mRightValid) {
            return null;
        }
        if (!mLeftValid) {
            mRightValid = mRight.moveToNext();
            return Result.RIGHT;
        }
        if (!mRightValid) {
            mLeftValid = mLeft.moveToNext();
            return Result.LEFT;
        }

        int cmp = compareKeys();
        if (cmp < 0) {
            mLeftValid = mLeft.moveToNext();
            return Result.LEFT;
        } else if (cmp > 0) {
            mRightValid = mRight.moveToNext();
            return Result.RIGHT;
        } else {
            mLeftValid  = mLeft.moveToNext();
            mRightValid = mRight.moveToNext();
            return Result.BOTH;
        }
    }

    /** Compares the join-key columns of the current rows in both cursors. */
    private int compareKeys() {
        for (int i = 0; i < mLeftColumnIndices.length; i++) {
            String lv = mLeft.getString(mLeftColumnIndices[i]);
            String rv = mRight.getString(mRightColumnIndices[i]);
            if (lv == null && rv == null) continue;
            if (lv == null) return -1;
            if (rv == null) return  1;
            int cmp = lv.compareTo(rv);
            if (cmp != 0) return cmp;
        }
        return 0;
    }
}
