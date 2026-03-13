package android.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible MatrixCursor shim. Pure Java stub.
 * A mutable cursor backed by an array of Object rows.
 */
public class MatrixCursor extends AbstractCursor {

    private final String[] mColumnNames;
    private final List<Object[]> mRows;
    private final int mColumnCount;

    // ──────────────────────────────────────────────────────────
    // Constructors
    // ──────────────────────────────────────────────────────────

    public MatrixCursor(String[] columnNames) {
        this(columnNames, 16);
    }

    public MatrixCursor(String[] columnNames, int initialCapacity) {
        mColumnNames  = columnNames;
        mColumnCount  = columnNames.length;
        mRows         = new ArrayList<>(initialCapacity);
    }

    // ──────────────────────────────────────────────────────────
    // Row building
    // ──────────────────────────────────────────────────────────

    /**
     * Adds a pre-built row. The array length must equal getColumnCount().
     */
    public void addRow(Object[] columnValues) {
        if (columnValues.length != mColumnCount) {
            throw new IllegalArgumentException(
                "columnValues.length " + columnValues.length +
                " != mColumnCount " + mColumnCount);
        }
        Object[] row = new Object[mColumnCount];
        System.arraycopy(columnValues, 0, row, 0, mColumnCount);
        mRows.add(row);
    }

    /**
     * Returns a RowBuilder for fluent row construction.
     * Call add() for each column value then discard the builder.
     */
    public RowBuilder newRow() {
        Object[] row = new Object[mColumnCount];
        mRows.add(row);
        return new RowBuilder(row);
    }

    // ──────────────────────────────────────────────────────────
    // AbstractCursor implementation
    // ──────────────────────────────────────────────────────────

    @Override
    public int getCount() {
        return mRows.size();
    }

    @Override
    public String[] getColumnNames() {
        return mColumnNames;
    }

    private Object get(int columnIndex) {
        if (mPos < 0 || mPos >= mRows.size()) {
            throw new android.database.CursorIndexOutOfBoundsException(mPos, mRows.size());
        }
        return mRows.get(mPos)[columnIndex];
    }

    @Override
    public String getString(int columnIndex) {
        Object val = get(columnIndex);
        return val == null ? null : val.toString();
    }

    @Override
    public short getShort(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).shortValue();
        return Short.parseShort(val.toString());
    }

    @Override
    public int getInt(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }

    @Override
    public long getLong(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null) return 0L;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    @Override
    public float getFloat(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null) return 0f;
        if (val instanceof Number) return ((Number) val).floatValue();
        return Float.parseFloat(val.toString());
    }

    @Override
    public double getDouble(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null) return null;
        if (val instanceof byte[]) return (byte[]) val;
        return val.toString().getBytes();
    }

    @Override
    public int getType(int columnIndex) {
        Object val = get(columnIndex);
        if (val == null)               return FIELD_TYPE_NULL;
        if (val instanceof byte[])     return FIELD_TYPE_BLOB;
        if (val instanceof Float
         || val instanceof Double)     return FIELD_TYPE_FLOAT;
        if (val instanceof Number)     return FIELD_TYPE_INTEGER;
        return FIELD_TYPE_STRING;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return get(columnIndex) == null;
    }

    // ──────────────────────────────────────────────────────────
    // RowBuilder inner class
    // ──────────────────────────────────────────────────────────

    /**
     * Builds a row column-by-column. Obtained via {@link #newRow()}.
     */
    public final class RowBuilder {
        private final Object[] mRow;
        private int mIndex;

        RowBuilder(Object[] row) {
            mRow   = row;
            mIndex = 0;
        }

        /**
         * Adds a value for the next column (in column-definition order).
         * @return this builder, for chaining
         */
        public RowBuilder add(Object columnValue) {
            if (mIndex >= mColumnCount) {
                throw new android.database.CursorIndexOutOfBoundsException(
                    "Too many values for row: expected " + mColumnCount);
            }
            mRow[mIndex++] = columnValue;
            return this;
        }

        /**
         * Adds a value for a named column.
         * @return this builder, for chaining
         */
        public RowBuilder add(String columnName, Object value) {
            int col = getColumnIndexOrThrow(columnName);
            mRow[col] = value;
            return this;
        }
    }
}
