package android.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.database.MatrixCursor
 * Pure Java — in-memory Cursor backed by Object[][] rows.
 */
public class MatrixCursor extends AbstractCursor {

    private final String[] columnNames;
    private final List<Object[]> data;
    private int position = -1;

    public MatrixCursor(String[] columnNames, int initialCapacity) {
        this.columnNames = columnNames;
        this.data = new ArrayList<>(initialCapacity);
    }

    public MatrixCursor(String[] columnNames) {
        this(columnNames, 16);
    }

    // ── RowBuilder ──────────────────────────────────────────────────────────

    public class RowBuilder {
        private final Object[] row;
        private int index = 0;

        RowBuilder(Object[] row) {
            this.row = row;
        }

        public RowBuilder add(Object columnValue) {
            if (index >= row.length) {
                throw new CursorIndexOutOfBoundsException(
                    "No more columns left.");
            }
            row[index++] = columnValue;
            return this;
        }

        public RowBuilder add(String columnName, Object value) {
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(columnName)) {
                    row[i] = value;
                    return this;
                }
            }
            throw new IllegalArgumentException("No such column: " + columnName);
        }
    }

    // ── Data manipulation ───────────────────────────────────────────────────

    public RowBuilder newRow() {
        Object[] row = new Object[columnNames.length];
        data.add(row);
        return new RowBuilder(row);
    }

    public void addRow(Object[] columnValues) {
        if (columnValues.length != columnNames.length) {
            throw new IllegalArgumentException(
                "columnValues.length = " + columnValues.length +
                ", expected " + columnNames.length);
        }
        Object[] row = new Object[columnNames.length];
        System.arraycopy(columnValues, 0, row, 0, columnNames.length);
        data.add(row);
    }

    public void addRow(Iterable<?> columnValues) {
        Object[] row = new Object[columnNames.length];
        int i = 0;
        for (Object val : columnValues) {
            if (i >= columnNames.length) {
                throw new IllegalArgumentException("Too many columns");
            }
            row[i++] = val;
        }
        data.add(row);
    }

    // ── Cursor implementation ───────────────────────────────────────────────

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) return i;
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) {
        int idx = getColumnIndex(columnName);
        if (idx < 0) {
            throw new IllegalArgumentException("column '" + columnName + "' does not exist");
        }
        return idx;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    // ── Value getters ───────────────────────────────────────────────────────

    private Object get(int column) {
        if (position < 0 || position >= data.size()) {
            throw new CursorIndexOutOfBoundsException(position, data.size());
        }
        if (column < 0 || column >= columnNames.length) {
            throw new CursorIndexOutOfBoundsException(column, columnNames.length);
        }
        return data.get(position)[column];
    }

    @Override
    public String getString(int column) {
        Object val = get(column);
        return val == null ? null : val.toString();
    }

    @Override
    public int getInt(int column) {
        Object val = get(column);
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }

    @Override
    public long getLong(int column) {
        Object val = get(column);
        if (val == null) return 0L;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    @Override
    public float getFloat(int column) {
        Object val = get(column);
        if (val == null) return 0f;
        if (val instanceof Number) return ((Number) val).floatValue();
        return Float.parseFloat(val.toString());
    }

    @Override
    public double getDouble(int column) {
        Object val = get(column);
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }

    @Override
    public short getShort(int column) {
        Object val = get(column);
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).shortValue();
        return Short.parseShort(val.toString());
    }

    @Override
    public boolean isNull(int column) {
        return get(column) == null;
    }

    @Override
    public byte[] getBlob(int column) {
        Object val = get(column);
        return (byte[]) val;
    }

    @Override
    public int getType(int column) {
        Object val = get(column);
        if (val == null) return 0; // FIELD_TYPE_NULL
        if (val instanceof Long || val instanceof Integer || val instanceof Short) return 1; // FIELD_TYPE_INTEGER
        if (val instanceof Float || val instanceof Double) return 2; // FIELD_TYPE_FLOAT
        if (val instanceof byte[]) return 4; // FIELD_TYPE_BLOB
        return 3; // FIELD_TYPE_STRING
    }

    // ── Position / Navigation ───────────────────────────────────────────────

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return moveToPosition(data.size() - 1);
    }

    @Override
    public boolean moveToNext() {
        return moveToPosition(position + 1);
    }

    @Override
    public boolean moveToPrevious() {
        return moveToPosition(position - 1);
    }

    @Override
    public boolean moveToPosition(int pos) {
        if (pos < -1) pos = -1;
        if (pos > data.size()) pos = data.size();
        position = pos;
        return position >= 0 && position < data.size();
    }

    @Override
    public boolean move(int offset) {
        return moveToPosition(position + offset);
    }

    @Override
    public boolean isBeforeFirst() {
        return data.size() > 0 && position == -1;
    }

    @Override
    public boolean isAfterLast() {
        return data.size() > 0 && position == data.size();
    }

    @Override
    public boolean isFirst() {
        return position == 0 && data.size() > 0;
    }

    @Override
    public boolean isLast() {
        return position == data.size() - 1 && data.size() > 0;
    }
}
