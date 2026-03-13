package android.database;

import android.content.ContentValues;
import android.os.Parcel;

/**
 * Android-compatible DatabaseUtils shim. Pure Java stub.
 * Static utilities for working with SQLite and Cursors.
 */
public class DatabaseUtils {

    private DatabaseUtils() {} // static-only

    // ──────────────────────────────────────────────────────────
    // String / SQL escaping
    // ──────────────────────────────────────────────────────────

    /**
     * SQL-escapes a string, surrounding it with single-quotes and doubling
     * any embedded single-quotes.
     */
    public static String sqlEscapeString(String value) {
        StringBuilder sb = new StringBuilder(value.length() + 2);
        appendEscapedSQLString(sb, value);
        return sb.toString();
    }

    /**
     * Appends a SQL-escaped string (including surrounding quotes) to sb.
     */
    public static void appendEscapedSQLString(StringBuilder sb, String value) {
        sb.append('\'');
        if (value.indexOf('\'') >= 0) {
            for (int i = 0, n = value.length(); i < n; i++) {
                char c = value.charAt(i);
                if (c == '\'') sb.append('\'');
                sb.append(c);
            }
        } else {
            sb.append(value);
        }
        sb.append('\'');
    }

    // ──────────────────────────────────────────────────────────
    // WHERE clause helpers
    // ──────────────────────────────────────────────────────────

    /**
     * Concatenates two WHERE clauses with AND, handling nulls.
     */
    public static String concatenateWhere(String a, String b) {
        if (a == null || a.isEmpty()) return b == null ? "" : b;
        if (b == null || b.isEmpty()) return a;
        return "(" + a + ") AND (" + b + ")";
    }

    /**
     * Appends a WHERE clause to a query string builder.
     */
    public static void appendWhereClause(StringBuilder s, String selection,
                                         String[] selectionArgs) {
        if (selection == null || selection.isEmpty()) return;
        s.append(" WHERE ");
        if (selectionArgs == null || selectionArgs.length == 0) {
            s.append(selection);
            return;
        }
        // Simple positional substitution (? → escaped arg)
        int argIdx = 0;
        for (int i = 0, n = selection.length(); i < n; i++) {
            char c = selection.charAt(i);
            if (c == '?' && argIdx < selectionArgs.length) {
                appendEscapedSQLString(s, selectionArgs[argIdx++]);
            } else {
                s.append(c);
            }
        }
    }

    // ──────────────────────────────────────────────────────────
    // Cursor / query utilities
    // ──────────────────────────────────────────────────────────

    /**
     * Returns the number of rows in a table matching the given selection.
     * Stub: returns 0 (no real database in shim).
     */
    public static long queryNumEntries(Object db, String table) {
        return 0L; // stub
    }

    public static long queryNumEntries(Object db, String table, String selection) {
        return 0L; // stub
    }

    public static long queryNumEntries(Object db, String table, String selection,
                                       String[] selectionArgs) {
        return 0L; // stub
    }

    /**
     * Executes a query that returns a single long. Stub: returns 0.
     */
    public static long longForQuery(Object db, String query, String[] selectionArgs) {
        return 0L; // stub
    }

    public static long longForQuery(Cursor c, String[] selectionArgs) {
        if (c != null && c.moveToFirst()) return c.getLong(0);
        return 0L;
    }

    /**
     * Executes a query that returns a single String. Stub: returns null.
     */
    public static String stringForQuery(Object db, String query, String[] selectionArgs) {
        return null; // stub
    }

    public static String stringForQuery(Cursor c, String[] selectionArgs) {
        if (c != null && c.moveToFirst()) return c.getString(0);
        return null;
    }

    // ──────────────────────────────────────────────────────────
    // ContentValues helpers
    // ──────────────────────────────────────────────────────────

    /**
     * Reads integer columns from the cursor into the given ContentValues.
     */
    public static void cursorIntToContentValues(Cursor cursor, String field,
                                                ContentValues values) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) values.put(field, cursor.getInt(idx));
    }

    public static void cursorIntToContentValues(Cursor cursor, String field,
                                                ContentValues values, String key) {
        int idx = cursor.getColumnIndex(field);
        if (idx >= 0) values.put(key, cursor.getInt(idx));
    }

    // ──────────────────────────────────────────────────────────
    // Debugging
    // ──────────────────────────────────────────────────────────

    /**
     * Dumps all rows of a cursor to stdout. Restores the original position.
     */
    public static void dumpCursor(Cursor cursor) {
        if (cursor == null) { System.out.println("null cursor"); return; }
        int pos = cursor.getPosition();
        System.out.println(">>>>> Dumping cursor " + cursor);
        String[] cols = cursor.getColumnNames();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < cols.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(cols[i]).append('=').append(cursor.getString(i));
            }
            sb.append('}');
            System.out.println(sb.toString());
            cursor.moveToNext();
        }
        System.out.println("<<<<<");
        cursor.moveToPosition(pos);
    }

    public static StringBuilder dumpCursorToString(Cursor cursor) {
        StringBuilder sb = new StringBuilder();
        if (cursor == null) { sb.append("null cursor"); return sb; }
        String[] cols = cursor.getColumnNames();
        int pos = cursor.getPosition();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sb.append('{');
            for (int i = 0; i < cols.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(cols[i]).append('=').append(cursor.getString(i));
            }
            sb.append("}\n");
            cursor.moveToNext();
        }
        cursor.moveToPosition(pos);
        return sb;
    }

    // ──────────────────────────────────────────────────────────
    // Parcel exception marshalling (stub)
    // ──────────────────────────────────────────────────────────

    /**
     * Writes an exception into a Parcel for remote procedure call results.
     * Stub — writes a code+message only.
     */
    public static void writeExceptionToParcel(Parcel reply, Exception e) {
        if (e == null) {
            reply.writeInt(0);
            return;
        }
        reply.writeInt(1);
        reply.writeString(e.getClass().getName());
        reply.writeString(e.getMessage() != null ? e.getMessage() : "");
    }

    /**
     * Reads an exception from a Parcel and re-throws it if present.
     */
    public static void readExceptionFromParcel(Parcel reply) throws RuntimeException {
        int code = reply.readInt();
        if (code == 0) return;
        String cls = reply.readString();
        String msg = reply.readString();
        throw new RuntimeException(cls + ": " + msg);
    }
}
