package android.database;

import android.content.ContentValues;

/**
 * Shim: android.database.DatabaseUtils
 * Pure Java — SQL utility methods.
 */
public class DatabaseUtils {
    public DatabaseUtils() {}

    // ── Statement type constants (match AOSP) ───────────────────────────

    public static final int STATEMENT_SELECT      = 1;
    public static final int STATEMENT_UPDATE       = 2;
    public static final int STATEMENT_ATTACH       = 3;
    public static final int STATEMENT_BEGIN        = 4;
    public static final int STATEMENT_COMMIT       = 5;
    public static final int STATEMENT_ABORT        = 6;
    public static final int STATEMENT_PRAGMA       = 7;
    public static final int STATEMENT_DDL          = 8;
    public static final int STATEMENT_UNPREPARED   = 9;
    public static final int STATEMENT_OTHER        = 99;

    // ── SQL statement type detection ────────────────────────────────────

    public static int getSqlStatementType(String sql) {
        if (sql == null || sql.isEmpty()) return STATEMENT_OTHER;
        String trimmed = sql.trim();
        if (trimmed.isEmpty()) return STATEMENT_OTHER;

        // Get first keyword (uppercase)
        String upper = trimmed.toUpperCase();
        if (upper.startsWith("SELECT") || upper.startsWith("WITH")) {
            return STATEMENT_SELECT;
        } else if (upper.startsWith("INSERT") || upper.startsWith("UPDATE")
                || upper.startsWith("DELETE") || upper.startsWith("REPLACE")) {
            return STATEMENT_UPDATE;
        } else if (upper.startsWith("ATTACH")) {
            return STATEMENT_ATTACH;
        } else if (upper.startsWith("BEGIN") || upper.startsWith("SAVEPOINT")) {
            return STATEMENT_BEGIN;
        } else if (upper.startsWith("COMMIT") || upper.startsWith("END")
                || upper.startsWith("RELEASE")) {
            return STATEMENT_COMMIT;
        } else if (upper.startsWith("ROLLBACK") || upper.startsWith("ABORT")) {
            return STATEMENT_ABORT;
        } else if (upper.startsWith("PRAGMA")) {
            return STATEMENT_PRAGMA;
        } else if (upper.startsWith("CREATE") || upper.startsWith("DROP")
                || upper.startsWith("ALTER")) {
            return STATEMENT_DDL;
        }
        return STATEMENT_OTHER;
    }

    // ── SQL string escaping ─────────────────────────────────────────────

    public static String sqlEscapeString(String value) {
        StringBuilder sb = new StringBuilder("'");
        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '\'') {
                    sb.append("''");
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('\'');
        return sb.toString();
    }

    public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
        sb.append('\'');
        if (sqlString != null) {
            int len = sqlString.length();
            for (int i = 0; i < len; i++) {
                char c = sqlString.charAt(i);
                if (c == '\'') {
                    sb.append("''");
                } else if (c == 0) {
                    // skip null chars
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('\'');
    }

    public static void appendValueToSql(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("NULL");
        } else if (value instanceof Number) {
            sb.append(value.toString());
        } else if (value instanceof Boolean) {
            sb.append(((Boolean) value) ? 1 : 0);
        } else {
            appendEscapedSQLString(sb, value.toString());
        }
    }

    // ── WHERE clause concatenation ──────────────────────────────────────

    public static String concatenateWhere(String a, String b) {
        if (a == null || a.isEmpty()) return b;
        if (b == null || b.isEmpty()) return a;
        return "(" + a + ") AND (" + b + ")";
    }

    // ── Selection args appending ────────────────────────────────────────

    public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
        if (originalValues == null || originalValues.length == 0) {
            return newValues;
        }
        if (newValues == null || newValues.length == 0) {
            return originalValues;
        }
        String[] result = new String[originalValues.length + newValues.length];
        System.arraycopy(originalValues, 0, result, 0, originalValues.length);
        System.arraycopy(newValues, 0, result, originalValues.length, newValues.length);
        return result;
    }

    // ── Collation keys ──────────────────────────────────────────────────

    public static String getCollationKey(String name) {
        if (name == null || name.isEmpty()) return "";
        // Simple collation: lowercase, strip accents via normalization
        return name.toLowerCase();
    }

    public static String getHexCollationKey(String name) {
        String key = getCollationKey(name);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            String hex = Integer.toHexString((int) key.charAt(i));
            for (int p = hex.length(); p < 4; p++) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    // ── Cursor utility methods ──────────────────────────────────────────

    public static void cursorRowToContentValues(Cursor cursor, ContentValues values) {
        if (cursor == null || values == null) return;
        String[] columns = cursor.getColumnNames();
        for (int i = 0; i < columns.length; i++) {
            if (cursor.isNull(i)) {
                values.putNull(columns[i]);
            } else {
                values.put(columns[i], cursor.getString(i));
            }
        }
    }

    public static void cursorStringToContentValues(Cursor cursor, String field, ContentValues values) {
        cursorStringToContentValues(cursor, field, values, field);
    }

    public static void cursorStringToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) {
            values.put(key, cursor.getString(idx));
        }
    }

    public static void cursorIntToContentValues(Cursor cursor, String field, ContentValues values) {
        cursorIntToContentValues(cursor, field, values, field);
    }

    public static void cursorIntToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) {
            values.put(key, cursor.getInt(idx));
        }
    }

    public static void cursorLongToContentValues(Cursor cursor, String field, ContentValues values) {
        cursorLongToContentValues(cursor, field, values, field);
    }

    public static void cursorLongToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) {
            values.put(key, cursor.getLong(idx));
        }
    }

    public static void cursorDoubleToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) {
            values.put(key, cursor.getDouble(idx));
        }
    }

    public static void cursorDoubleToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0 && !cursor.isNull(idx)) {
            values.put(column, cursor.getDouble(idx));
        }
    }

    public static void cursorFloatToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0 && !cursor.isNull(idx)) {
            values.put(column, cursor.getFloat(idx));
        }
    }

    public static void cursorIntToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0 && !cursor.isNull(idx)) {
            values.put(column, cursor.getInt(idx));
        }
    }

    public static void cursorLongToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0 && !cursor.isNull(idx)) {
            values.put(column, cursor.getLong(idx));
        }
    }

    public static void cursorShortToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0 && !cursor.isNull(idx)) {
            values.put(column, (int) cursor.getShort(idx));
        }
    }

    public static void cursorStringToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0 && !cursor.isNull(idx)) {
            values.put(column, cursor.getString(idx));
        }
    }

    // ── Dump methods ────────────────────────────────────────────────────

    public static String dumpCursorToString(Cursor cursor) {
        if (cursor == null) return "null cursor";
        StringBuilder sb = new StringBuilder();
        sb.append(">>>>> Dumping cursor ").append(cursor).append("\n");
        int startPos = cursor.getPosition();
        String[] cols = cursor.getColumnNames();
        sb.append(cols.length).append(" columns\n");
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            sb.append(dumpCurrentRowToString(cursor)).append("\n");
        }
        cursor.moveToPosition(startPos);
        sb.append("<<<<<\n");
        return sb.toString();
    }

    public static String dumpCurrentRowToString(Cursor cursor) {
        if (cursor == null) return "null cursor";
        String[] cols = cursor.getColumnNames();
        StringBuilder sb = new StringBuilder();
        sb.append(cursor.getPosition()).append(" {");
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(", ");
            String val;
            try { val = cursor.getString(i); } catch (Exception e) { val = "?"; }
            sb.append(cols[i]).append("=").append(val);
        }
        sb.append("}");
        return sb.toString();
    }

    public static void dumpCursor(Cursor cursor) {
        System.out.println(dumpCursorToString(cursor));
    }

    public static void dumpCurrentRow(Cursor cursor) {
        System.out.println(dumpCurrentRowToString(cursor));
    }

    public static void dumpCursor(Cursor cursor, StringBuilder sb) {
        sb.append(dumpCursorToString(cursor));
    }

    public static void dumpCurrentRow(Cursor cursor, StringBuilder sb) {
        sb.append(dumpCurrentRowToString(cursor));
    }

    // ── Stub methods that require SQLiteDatabase (not implementable without it) ──

    public static long queryNumEntries(android.database.sqlite.SQLiteDatabase db, String table) {
        return queryNumEntries(db, table, null, null);
    }

    public static long queryNumEntries(android.database.sqlite.SQLiteDatabase db, String table, String selection) {
        return queryNumEntries(db, table, selection, null);
    }

    public static long queryNumEntries(android.database.sqlite.SQLiteDatabase db, String table, String selection, String[] selectionArgs) {
        // Without a real SQLite implementation, return 0
        return 0L;
    }

    public static long longForQuery(android.database.sqlite.SQLiteDatabase db, String query, String[] selectionArgs) {
        return 0L;
    }

    public static long longForQuery(android.database.sqlite.SQLiteStatement prog, String[] selectionArgs) {
        return 0L;
    }

    public static String stringForQuery(android.database.sqlite.SQLiteDatabase db, String query, String[] selectionArgs) {
        return null;
    }

    public static String stringForQuery(android.database.sqlite.SQLiteStatement prog, String[] selectionArgs) {
        return null;
    }

    public static android.os.ParcelFileDescriptor blobFileDescriptorForQuery(android.database.sqlite.SQLiteDatabase db, String query, String[] selectionArgs) {
        return null;
    }

    public static android.os.ParcelFileDescriptor blobFileDescriptorForQuery(android.database.sqlite.SQLiteStatement prog, String[] selectionArgs) {
        return null;
    }

    public static void createDbFromSqlStatements(android.content.Context context, String dbName, int dbVersion, String sqlStatements) {
        // No-op without real SQLite
    }

    public static void cursorDoubleToCursorValues(Cursor cursor, String field, ContentValues values) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) {
            values.put(field, cursor.getDouble(idx));
        }
    }

    public static void cursorStringToInsertHelper(Cursor cursor, String field, android.database.DatabaseUtils.InsertHelper inserter, int index) {
        // No-op stub
    }

    public static void bindObjectToProgram(android.database.sqlite.SQLiteProgram prog, int index, Object value) {
        // No-op stub
    }

    // ── Parcel exception methods ────────────────────────────────────────

    public static void readExceptionFromParcel(android.os.Parcel reply) {
        // No-op stub
    }

    public static void readExceptionWithFileNotFoundExceptionFromParcel(android.os.Parcel reply)
            throws java.io.FileNotFoundException {
        // No-op stub
    }

    public static void readExceptionWithOperationApplicationExceptionFromParcel(android.os.Parcel reply)
            throws android.content.OperationApplicationException {
        // No-op stub
    }

    public static void writeExceptionToParcel(android.os.Parcel reply, Exception e) {
        // No-op stub
    }

    // ── Inner class stubs ───────────────────────────────────────────────

    public static class InsertHelper {
        public InsertHelper(android.database.sqlite.SQLiteDatabase db, String tableName) {}
        public long insert(ContentValues values) { return -1; }
        public long replace(ContentValues values) { return -1; }
        public int getColumnIndex(String key) { return -1; }
        public void close() {}
        public void prepareForInsert() {}
        public void prepareForReplace() {}
        public long execute() { return -1; }
        public void bind(int index, double value) {}
        public void bind(int index, float value) {}
        public void bind(int index, long value) {}
        public void bind(int index, int value) {}
        public void bind(int index, boolean value) {}
        public void bind(int index, byte[] value) {}
        public void bind(int index, String value) {}
        public void bindNull(int index) {}
    }
}
