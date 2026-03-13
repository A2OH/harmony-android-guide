package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.database.sqlite.SQLiteDatabase → @ohos.data.relationalStore.RdbStore
 * Tier 1 — near-direct mapping.
 */
public class SQLiteDatabase {
    private final long handle;

    SQLiteDatabase(long handle) {
        this.handle = handle;
    }

    // ── Execute ──

    public void execSQL(String sql) {
        OHBridge.rdbStoreExecSQL(handle, sql);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        // Bind args by replacing ? placeholders
        String bound = bindArgs(sql, bindArgs);
        OHBridge.rdbStoreExecSQL(handle, bound);
    }

    // ── Query ──

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        String bound = bindArgs(sql, selectionArgs);
        long rsHandle = OHBridge.rdbStoreQuery(handle, bound, null);
        return new CursorWrapper(rsHandle);
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null);
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit) {
        StringBuilder sql = new StringBuilder("SELECT ");
        if (columns == null || columns.length == 0) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }
        sql.append(" FROM ").append(table);
        if (selection != null && !selection.isEmpty()) {
            sql.append(" WHERE ").append(selection);
        }
        if (groupBy != null && !groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(groupBy);
        }
        if (having != null && !having.isEmpty()) {
            sql.append(" HAVING ").append(having);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null && !limit.isEmpty()) {
            sql.append(" LIMIT ").append(limit);
        }
        return rawQuery(sql.toString(), selectionArgs);
    }

    // ── Insert ──

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return insertWithOnConflict(table, nullColumnHack, values, CONFLICT_NONE);
    }

    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        long id = insert(table, nullColumnHack, values);
        if (id == -1) throw new android.database.SQLException("Failed to insert row");
        return id;
    }

    public long replace(String table, String nullColumnHack, ContentValues values) {
        return insertWithOnConflict(table, nullColumnHack, values, CONFLICT_REPLACE);
    }

    public static final int CONFLICT_NONE = 0;
    public static final int CONFLICT_ROLLBACK = 1;
    public static final int CONFLICT_ABORT = 2;
    public static final int CONFLICT_FAIL = 3;
    public static final int CONFLICT_IGNORE = 4;
    public static final int CONFLICT_REPLACE = 5;

    public long insertWithOnConflict(String table, String nullColumnHack,
                                      ContentValues values, int conflictAlgorithm) {
        return OHBridge.rdbStoreInsert(handle, table, values.toJson());
    }

    // ── Update ──

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return OHBridge.rdbStoreUpdate(handle, values.toJson(), table, whereClause, whereArgs);
    }

    // ── Delete ──

    public int delete(String table, String whereClause, String[] whereArgs) {
        return OHBridge.rdbStoreDelete(handle, table, whereClause, whereArgs);
    }

    // ── Transactions ──

    public void beginTransaction() {
        OHBridge.rdbStoreBeginTransaction(handle);
    }

    public void setTransactionSuccessful() {
        // In OH, this is a no-op — commit happens at endTransaction
    }

    public void endTransaction() {
        OHBridge.rdbStoreCommit(handle);
    }

    // ── Lifecycle ──

    public void close() {
        OHBridge.rdbStoreClose(handle);
    }

    public boolean isOpen() {
        return handle != 0;
    }

    // ── Helpers ──

    private static String bindArgs(String sql, Object[] args) {
        if (args == null || args.length == 0) return sql;
        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '?' && argIndex < args.length) {
                Object arg = args[argIndex++];
                if (arg == null) {
                    result.append("NULL");
                } else if (arg instanceof Number) {
                    result.append(arg);
                } else {
                    result.append("'").append(arg.toString().replace("'", "''")).append("'");
                }
            } else {
                result.append(sql.charAt(i));
            }
        }
        return result.toString();
    }
}
