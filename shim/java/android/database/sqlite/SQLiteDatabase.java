package android.database.sqlite;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.MatrixCursor;
import android.os.CancellationSignal;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/* Regex-free split for KitKat Dalvik (no Pattern.compileImpl native) */
class SplitHelper {
    static String[] ws(String s) {
        s = s.trim(); if (s.isEmpty()) return new String[0];
        List<String> r = new ArrayList<String>(); int st = 0; boolean w = false;
        for (int i = 0; i <= s.length(); i++) {
            boolean sp = (i == s.length()) || Character.isWhitespace(s.charAt(i));
            if (w && sp) { r.add(s.substring(st, i)); w = false; }
            else if (!w && !sp) { st = i; w = true; }
        } return r.toArray(new String[0]);
    }
    static String[] ch(String s, char d) {
        List<String> r = new ArrayList<String>(); int st = 0;
        for (int i = 0; i <= s.length(); i++)
            if (i == s.length() || s.charAt(i) == d) { r.add(s.substring(st, i)); st = i+1; }
        return r.toArray(new String[0]);
    }
    static String[] andSplit(String s) {
        // Split on " AND " case-insensitive without regex
        List<String> r = new ArrayList<String>(); String u = s.toUpperCase(Locale.US);
        int st = 0, idx;
        while ((idx = u.indexOf(" AND ", st)) >= 0) { r.add(s.substring(st, idx)); st = idx + 5; }
        r.add(s.substring(st)); return r.toArray(new String[0]);
    }
}

/**
 * Android-compatible SQLiteDatabase shim.
 * In-memory database backed by Java data structures (Maps/Lists).
 * Supports basic CRUD operations, transactions, and simple WHERE matching.
 * Does NOT parse real SQL -- uses ContentValues-based insert/update/delete
 * and returns MatrixCursor results from query methods.
 */
public final class SQLiteDatabase extends SQLiteClosable {
    /** Factory for creating Cursor objects. */
    public interface CursorFactory {
        android.database.Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
            String editTable, SQLiteQuery query);
    }

    public static final int CONFLICT_ABORT = 2;
    public static final int CONFLICT_FAIL = 3;
    public static final int CONFLICT_IGNORE = 4;
    public static final int CONFLICT_NONE = 0;
    public static final int CONFLICT_REPLACE = 5;
    public static final int CONFLICT_ROLLBACK = 1;
    public static final int CREATE_IF_NECESSARY = 0x10000000;
    public static final int ENABLE_WRITE_AHEAD_LOGGING = 0x20000000;
    public static final int MAX_SQL_CACHE_SIZE = 100;
    public static final int NO_LOCALIZED_COLLATORS = 0x10;
    public static final int OPEN_READONLY = 0x00000001;
    public static final int OPEN_READWRITE = 0x00000000;
    public static final int SQLITE_MAX_LIKE_PATTERN_LENGTH = 50000;

    // -- Internal table storage -----------------------------------------------

    /**
     * Represents a single in-memory table with named columns and row data.
     */
    private static class Table {
        final String name;
        final List<String> columns;
        final List<Map<String, Object>> rows;
        final AtomicLong nextRowId;

        Table(String name, List<String> columns) {
            this.name = name;
            this.columns = new ArrayList<>(columns);
            this.rows = new ArrayList<>();
            this.nextRowId = new AtomicLong(1);
        }
    }

    // -- Instance state -------------------------------------------------------

    private final Map<String, Table> tables = new LinkedHashMap<>();
    String path;
    private int version;
    private boolean open = true;
    private boolean readOnly;
    private int flags;
    private boolean walEnabled;
    private long maxSize = 1024 * 1024 * 16; // 16 MB default
    private long pageSize = 4096;
    private int maxSqlCacheSize = MAX_SQL_CACHE_SIZE;

    // Transaction state
    private boolean inTransaction;
    private boolean transactionSuccessful;
    private SQLiteTransactionListener transactionListener;
    private int transactionNestingDepth;

    // Phone bridge: real framework SQLiteDatabase object (null if in-memory shim)
    Object realDb;

    // -- Constructor ----------------------------------------------------------

    public SQLiteDatabase() {
        this.path = ":memory:";
        if (SQLiteBridge.isOnPhone()) {
            realDb = SQLiteBridge.openRealDatabase(":memory:");
        }
    }

    private SQLiteDatabase(String path, int flags) {
        this.path = path != null ? path : ":memory:";
        this.flags = flags;
        this.readOnly = (flags & OPEN_READONLY) != 0;
        if (SQLiteBridge.isOnPhone()) {
            realDb = SQLiteBridge.openRealDatabase(this.path);
            if (realDb != null) System.out.println("[SQLiteDatabase] Using real SQLite for: " + this.path);
        }
    }

    // -- Static factory methods -----------------------------------------------

    private void throwIfNotOpen() {
        if (!open) {
            throw new IllegalStateException("attempt to re-open an already-closed object");
        }
    }

    public static SQLiteDatabase openDatabase(String path, Object cursorFactory, int flags) {
        return new SQLiteDatabase(path, flags);
    }

    public static SQLiteDatabase openDatabase(File path, Object params) {
        return new SQLiteDatabase(path != null ? path.getAbsolutePath() : ":memory:", CREATE_IF_NECESSARY);
    }

    public static SQLiteDatabase openDatabase(String path, Object cursorFactory, int flags, DatabaseErrorHandler errorHandler) {
        return new SQLiteDatabase(path, flags);
    }

    public static SQLiteDatabase openOrCreateDatabase(File file, Object cursorFactory) {
        return new SQLiteDatabase(file != null ? file.getAbsolutePath() : ":memory:", CREATE_IF_NECESSARY);
    }

    public static SQLiteDatabase openOrCreateDatabase(String path, Object cursorFactory, DatabaseErrorHandler errorHandler) {
        return new SQLiteDatabase(path, CREATE_IF_NECESSARY);
    }

    public static SQLiteDatabase create(Object cursorFactory) {
        return new SQLiteDatabase(":memory:", CREATE_IF_NECESSARY);
    }

    public static boolean deleteDatabase(File file) {
        if (file == null) return false;
        boolean deleted = file.delete();
        // Also attempt journal and WAL files
        File journal = new File(file.getPath() + "-journal");
        if (journal.exists()) journal.delete();
        File wal = new File(file.getPath() + "-wal");
        if (wal.exists()) wal.delete();
        File shm = new File(file.getPath() + "-shm");
        if (shm.exists()) shm.delete();
        return deleted;
    }

    public static String findEditTable(String tables) {
        if (tables == null || tables.isEmpty()) return null;
        // Find the first word -- the table name before any JOIN or comma
        int space = tables.indexOf(' ');
        int comma = tables.indexOf(',');
        if (space >= 0 && (comma < 0 || space < comma)) {
            return tables.substring(0, space);
        }
        if (comma >= 0) {
            return tables.substring(0, comma);
        }
        return tables;
    }

    public static int releaseMemory() {
        return 0; // no-op in shim
    }

    // -- Transaction management -----------------------------------------------

    public void beginTransaction() {
        if (realDb != null) { SQLiteBridge.beginTransaction(realDb); return; }
        beginTransactionWithListener(null);
    }

    public void beginTransactionNonExclusive() {
        beginTransactionWithListener(null);
    }

    public void beginTransactionWithListener(SQLiteTransactionListener listener) {
        throwIfNotOpen();
        if (inTransaction) {
            transactionNestingDepth++;
            return;
        }
        inTransaction = true;
        transactionSuccessful = false;
        transactionListener = listener;
        transactionNestingDepth = 0;
        if (listener != null) {
            listener.onBegin();
        }
    }

    public void beginTransactionWithListenerNonExclusive(SQLiteTransactionListener listener) {
        beginTransactionWithListener(listener);
    }

    public void setTransactionSuccessful() {
        if (realDb != null) { SQLiteBridge.setTransactionSuccessful(realDb); return; }
        throwIfNotOpen();
        if (!inTransaction) {
            throw new IllegalStateException("no transaction pending");
        }
        transactionSuccessful = true;
    }

    public void endTransaction() {
        if (realDb != null) { SQLiteBridge.endTransaction(realDb); return; }
        throwIfNotOpen();
        if (!inTransaction) {
            throw new IllegalStateException("no transaction pending");
        }
        if (transactionNestingDepth > 0) {
            transactionNestingDepth--;
            return;
        }
        SQLiteTransactionListener listener = transactionListener;
        try {
            if (transactionSuccessful) {
                if (listener != null) listener.onCommit();
            } else {
                if (listener != null) listener.onRollback();
            }
        } finally {
            inTransaction = false;
            transactionSuccessful = false;
            transactionListener = null;
        }
    }

    public boolean inTransaction() {
        return inTransaction;
    }

    public boolean yieldIfContendedSafely() {
        return false;
    }

    public boolean yieldIfContendedSafely(long sleepAfterYieldDelay) {
        return false;
    }

    // -- Database state -------------------------------------------------------

    public boolean isOpen() {
        return open;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getPath() {
        return path;
    }

    public int getVersion() {
        if (realDb != null) return SQLiteBridge.getVersion(realDb);
        return version;
    }

    public void setVersion(int version) {
        if (realDb != null) { SQLiteBridge.setVersion(realDb, version); return; }
        throwIfNotOpen();
        this.version = version;
    }

    public long getMaximumSize() {
        return maxSize;
    }

    public long setMaximumSize(long numBytes) {
        throwIfNotOpen();
        // Round up to next page boundary
        long pageCount = numBytes / pageSize;
        if (numBytes % pageSize != 0) pageCount++;
        this.maxSize = pageCount * pageSize;
        return this.maxSize;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long numBytes) {
        throwIfNotOpen();
        this.pageSize = numBytes;
    }

    public void setMaxSqlCacheSize(int cacheSize) {
        throwIfNotOpen();
        if (cacheSize < 0 || cacheSize > MAX_SQL_CACHE_SIZE) {
            throw new IllegalStateException("expected value between 0 and " + MAX_SQL_CACHE_SIZE);
        }
        this.maxSqlCacheSize = cacheSize;
    }

    public boolean needUpgrade(int newVersion) {
        return version < newVersion;
    }

    public boolean isDatabaseIntegrityOk() {
        return open;
    }

    public boolean isDbLockedByCurrentThread() {
        return inTransaction;
    }

    public boolean isWriteAheadLoggingEnabled() {
        return walEnabled;
    }

    public boolean enableWriteAheadLogging() {
        throwIfNotOpen();
        walEnabled = true;
        return true;
    }

    public void disableWriteAheadLogging() {
        throwIfNotOpen();
        walEnabled = false;
    }

    public List<?> getAttachedDbs() {
        throwIfNotOpen();
        List<Object> result = new ArrayList<>();
        return result;
    }

    @Override
    public void onAllReferencesReleased() {
        open = false;
    }

    public void setForeignKeyConstraintsEnabled(boolean enable) {
        throwIfNotOpen();
    }

    public void setLocale(Locale locale) {
        throwIfNotOpen();
    }

    public void setCustomAggregateFunction(String functionName, Object function) {
        throwIfNotOpen();
    }

    public void setCustomScalarFunction(String functionName, Object function) {
        throwIfNotOpen();
    }

    public void validateSql(String sql, CancellationSignal cancellationSignal) {
        throwIfNotOpen();
        if (sql == null) throw new IllegalArgumentException("sql must not be null");
    }

    // -- SQL execution --------------------------------------------------------

    public void execSQL(String sql) {
        throwIfNotOpen();
        if (sql == null) throw new IllegalArgumentException("sql must not be null");
        if (realDb != null) { SQLiteBridge.execSQL(realDb, sql); return; }
        parseAndExecDDL(sql);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        throwIfNotOpen();
        if (sql == null) throw new IllegalArgumentException("sql must not be null");
        if (realDb != null) { SQLiteBridge.execSQL(realDb, sql, bindArgs); return; }
        parseAndExecDDL(sql);
    }

    public void execPerConnectionSQL(String sql, Object[] bindArgs) {
        execSQL(sql, bindArgs);
    }

    /**
     * Very basic DDL parser: handles CREATE TABLE and DROP TABLE.
     * Other statements are silently accepted.
     */
    private void parseAndExecDDL(String sql) {
        String trimmed = sql.trim();
        String upper = trimmed.toUpperCase(Locale.US);

        if (upper.startsWith("CREATE TABLE")) {
            parseCreateTable(trimmed);
        } else if (upper.startsWith("DROP TABLE")) {
            parseDropTable(trimmed);
        }
        // Other SQL (CREATE INDEX, ALTER TABLE, etc.) accepted silently
    }

    private void parseCreateTable(String sql) {
        // CREATE TABLE [IF NOT EXISTS] tableName (col1 type, col2 type, ...)
        String upper = sql.toUpperCase(Locale.US);
        int parenOpen = sql.indexOf('(');
        if (parenOpen < 0) return;

        String prefix = sql.substring(0, parenOpen).trim();
        // Extract table name: last word before '('
        String[] prefixParts = SplitHelper.ws(prefix);
        String tableName = prefixParts[prefixParts.length - 1];
        // Remove backticks/brackets/quotes
        tableName = stripQuotes(tableName);

        // If table already exists and IF NOT EXISTS is present, skip
        if (tables.containsKey(tableName.toLowerCase(Locale.US))) {
            if (upper.contains("IF NOT EXISTS")) return;
            // Otherwise silently replace (for simplicity)
        }

        // Parse column definitions
        int parenClose = sql.lastIndexOf(')');
        if (parenClose <= parenOpen) return;
        String colDefs = sql.substring(parenOpen + 1, parenClose);
        List<String> columns = parseColumnNames(colDefs);

        if (columns.isEmpty()) {
            columns.add("_id"); // fallback
        }

        tables.put(tableName.toLowerCase(Locale.US), new Table(tableName, columns));
    }

    private List<String> parseColumnNames(String colDefs) {
        List<String> columns = new ArrayList<>();
        // Split on commas, but respect parentheses (for constraints like FOREIGN KEY(...))
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < colDefs.length(); i++) {
            char c = colDefs.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == ',' && depth == 0) {
                String col = extractColumnName(current.toString().trim());
                if (col != null) columns.add(col);
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        String last = extractColumnName(current.toString().trim());
        if (last != null) columns.add(last);
        return columns;
    }

    private String extractColumnName(String colDef) {
        if (colDef.isEmpty()) return null;
        String upper = colDef.toUpperCase(Locale.US).trim();
        // Skip table-level constraints
        if (upper.startsWith("PRIMARY KEY") || upper.startsWith("UNIQUE") ||
            upper.startsWith("CHECK") || upper.startsWith("FOREIGN KEY") ||
            upper.startsWith("CONSTRAINT")) {
            return null;
        }
        // First token is the column name
        String[] parts = SplitHelper.ws(colDef.trim());
        return stripQuotes(parts[0]);
    }

    private String stripQuotes(String s) {
        if (s.length() >= 2) {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if ((first == '`' && last == '`') ||
                (first == '"' && last == '"') ||
                (first == '[' && last == ']') ||
                (first == '\'' && last == '\'')) {
                return s.substring(1, s.length() - 1);
            }
        }
        return s;
    }

    private void parseDropTable(String sql) {
        String[] parts = SplitHelper.ws(sql.trim());
        // DROP TABLE [IF EXISTS] name
        String tableName = parts[parts.length - 1];
        tableName = stripQuotes(tableName);
        tables.remove(tableName.toLowerCase(Locale.US));
    }

    // -- Compiled statements --------------------------------------------------

    public SQLiteStatement compileStatement(String sql) {
        throwIfNotOpen();
        if (sql == null) throw new IllegalArgumentException("sql must not be null");
        return new SQLiteStatement(sql);
    }

    // -- Insert ---------------------------------------------------------------

    public long insert(String table, String nullColumnHack, ContentValues values) {
        if (realDb != null) return SQLiteBridge.insert(realDb, table, nullColumnHack, values);
        try {
            return insertWithOnConflict(table, nullColumnHack, values, CONFLICT_NONE);
        } catch (Exception e) {
            return -1;
        }
    }

    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        return insertWithOnConflict(table, nullColumnHack, values, CONFLICT_NONE);
    }

    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        throwIfNotOpen();
        if (table == null) throw new IllegalArgumentException("table must not be null");

        String key = table.toLowerCase(Locale.US);
        Table t = tables.get(key);
        if (t == null) {
            // Auto-create table from ContentValues columns
            List<String> cols = new ArrayList<>();
            if (initialValues != null && initialValues.size() > 0) {
                cols.addAll(initialValues.keySet());
            } else if (nullColumnHack != null) {
                cols.add(nullColumnHack);
            } else {
                cols.add("_id");
            }
            // Ensure _id column exists
            boolean hasId = false;
            for (String c : cols) {
                if (c.equalsIgnoreCase("_id")) { hasId = true; break; }
            }
            if (!hasId) cols.add(0, "_id");
            t = new Table(table, cols);
            tables.put(key, t);
        }

        long rowId = t.nextRowId.getAndIncrement();

        Map<String, Object> row = new HashMap<>();
        row.put("_id", rowId);

        if (initialValues != null) {
            for (String col : initialValues.keySet()) {
                row.put(col, initialValues.get(col));
                // Auto-add column if not already tracked
                if (!containsIgnoreCase(t.columns, col)) {
                    t.columns.add(col);
                }
            }
        }

        // Handle CONFLICT_REPLACE: remove existing row with same _id
        if (conflictAlgorithm == CONFLICT_REPLACE && initialValues != null) {
            Object idVal = initialValues.get("_id");
            if (idVal != null) {
                removeRowsMatching(t, "_id = ?", new String[]{ idVal.toString() });
                // Use the provided _id
                row.put("_id", idVal);
            }
        }

        t.rows.add(row);
        return rowId;
    }

    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        try {
            return insertWithOnConflict(table, nullColumnHack, initialValues, CONFLICT_REPLACE);
        } catch (Exception e) {
            return -1;
        }
    }

    public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues) {
        return insertWithOnConflict(table, nullColumnHack, initialValues, CONFLICT_REPLACE);
    }

    // -- Update ---------------------------------------------------------------

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (realDb != null) return SQLiteBridge.update(realDb, table, values, whereClause, whereArgs);
        return updateWithOnConflict(table, values, whereClause, whereArgs, CONFLICT_NONE);
    }

    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
        throwIfNotOpen();
        if (table == null) throw new IllegalArgumentException("table must not be null");

        String key = table.toLowerCase(Locale.US);
        Table t = tables.get(key);
        if (t == null) return 0;

        int count = 0;
        for (Map<String, Object> row : t.rows) {
            if (matchesWhere(row, whereClause, whereArgs)) {
                if (values != null) {
                    for (String col : values.keySet()) {
                        row.put(col, values.get(col));
                        if (!containsIgnoreCase(t.columns, col)) {
                            t.columns.add(col);
                        }
                    }
                }
                count++;
            }
        }
        return count;
    }

    // -- Delete ---------------------------------------------------------------

    public int delete(String table, String whereClause, String[] whereArgs) {
        if (realDb != null) return SQLiteBridge.delete(realDb, table, whereClause, whereArgs);
        throwIfNotOpen();
        if (table == null) throw new IllegalArgumentException("table must not be null");

        String key = table.toLowerCase(Locale.US);
        Table t = tables.get(key);
        if (t == null) return 0;

        return removeRowsMatching(t, whereClause, whereArgs);
    }

    private int removeRowsMatching(Table t, String whereClause, String[] whereArgs) {
        if (whereClause == null || whereClause.trim().isEmpty()) {
            int count = t.rows.size();
            t.rows.clear();
            return count;
        }
        List<Map<String, Object>> toRemove = new ArrayList<>();
        for (Map<String, Object> row : t.rows) {
            if (matchesWhere(row, whereClause, whereArgs)) {
                toRemove.add(row);
            }
        }
        t.rows.removeAll(toRemove);
        return toRemove.size();
    }

    // -- Query ----------------------------------------------------------------

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return queryInternal(table, columns, selection, selectionArgs, orderBy, limit);
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        return queryInternal(table, columns, selection, selectionArgs, orderBy, limit);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (realDb != null) return SQLiteBridge.query(realDb, table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return queryInternal(table, columns, selection, selectionArgs, orderBy, null);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return queryInternal(table, columns, selection, selectionArgs, orderBy, limit);
    }

    private Cursor queryInternal(String table, String[] columns, String selection, String[] selectionArgs, String orderBy, String limit) {
        throwIfNotOpen();
        if (table == null) throw new IllegalArgumentException("table must not be null");

        String key = table.toLowerCase(Locale.US);
        Table t = tables.get(key);
        if (t == null) {
            // Return empty cursor with requested columns
            String[] cols = columns != null ? columns : new String[]{"_id"};
            return new MatrixCursor(cols);
        }

        // Determine result columns
        String[] resultColumns;
        if (columns == null || columns.length == 0) {
            resultColumns = t.columns.toArray(new String[0]);
        } else {
            resultColumns = columns;
        }

        MatrixCursor cursor = new MatrixCursor(resultColumns);

        // Filter rows
        List<Map<String, Object>> matchedRows = new ArrayList<>();
        for (Map<String, Object> row : t.rows) {
            if (matchesWhere(row, selection, selectionArgs)) {
                matchedRows.add(row);
            }
        }

        // Apply ordering (basic: single column ASC/DESC)
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            sortRows(matchedRows, orderBy);
        }

        // Apply limit
        int maxRows = matchedRows.size();
        if (limit != null && !limit.trim().isEmpty()) {
            try {
                // Handle "offset, count" or just "count"
                String trimmedLimit = limit.trim();
                if (trimmedLimit.contains(",")) {
                    String[] parts = SplitHelper.ch(trimmedLimit, ',');
                    // offset, count
                    int offset = Integer.parseInt(parts[0].trim());
                    int count = Integer.parseInt(parts[1].trim());
                    if (offset > 0 && offset < matchedRows.size()) {
                        matchedRows = matchedRows.subList(offset, matchedRows.size());
                    } else if (offset >= matchedRows.size()) {
                        matchedRows = new ArrayList<>();
                    }
                    maxRows = Math.min(count, matchedRows.size());
                } else {
                    maxRows = Math.min(Integer.parseInt(trimmedLimit), matchedRows.size());
                }
            } catch (NumberFormatException e) {
                // ignore bad limit
            }
        }

        // Build result rows
        for (int i = 0; i < maxRows && i < matchedRows.size(); i++) {
            Map<String, Object> row = matchedRows.get(i);
            Object[] rowData = new Object[resultColumns.length];
            for (int j = 0; j < resultColumns.length; j++) {
                rowData[j] = row.get(resultColumns[j]);
            }
            cursor.addRow(rowData);
        }

        return cursor;
    }

    public Cursor queryWithFactory(Object cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return queryInternal(table, columns, selection, selectionArgs, orderBy, limit);
    }

    public Cursor queryWithFactory(Object cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        return queryInternal(table, columns, selection, selectionArgs, orderBy, limit);
    }

    // -- Raw queries ----------------------------------------------------------

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        if (realDb != null) return SQLiteBridge.rawQuery(realDb, sql, selectionArgs);
        return rawQueryInternal(sql, selectionArgs);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        return rawQueryInternal(sql, selectionArgs);
    }

    public Cursor rawQueryWithFactory(Object cursorFactory, String sql, String[] selectionArgs, String editTable) {
        return rawQueryInternal(sql, selectionArgs);
    }

    public Cursor rawQueryWithFactory(Object cursorFactory, String sql, String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {
        return rawQueryInternal(sql, selectionArgs);
    }

    /**
     * Basic raw query handler. Supports:
     * - SELECT * FROM tableName [WHERE ...]
     * - SELECT col1, col2 FROM tableName [WHERE ...]
     * Falls back to an empty cursor for unparseable queries.
     */
    private Cursor rawQueryInternal(String sql, String[] selectionArgs) {
        throwIfNotOpen();
        if (sql == null) throw new IllegalArgumentException("sql must not be null");

        String trimmed = sql.trim();
        String upper = trimmed.toUpperCase(Locale.US);

        if (!upper.startsWith("SELECT")) {
            // Non-SELECT: execute and return empty cursor
            parseAndExecDDL(trimmed);
            return new MatrixCursor(new String[]{"result"});
        }

        // Parse: SELECT <columns> FROM <table> [WHERE <condition>]
        int fromIdx = upper.indexOf(" FROM ");
        if (fromIdx < 0) {
            return new MatrixCursor(new String[]{"result"});
        }

        // Extract column list
        String colPart = trimmed.substring(6, fromIdx).trim(); // after "SELECT"
        String afterFrom = trimmed.substring(fromIdx + 6).trim();

        // Extract table name and optional WHERE
        String tableName;
        String whereClause = null;
        int whereIdx = afterFrom.toUpperCase(Locale.US).indexOf(" WHERE ");
        int orderIdx = afterFrom.toUpperCase(Locale.US).indexOf(" ORDER ");
        int limitIdx = afterFrom.toUpperCase(Locale.US).indexOf(" LIMIT ");
        int groupIdx = afterFrom.toUpperCase(Locale.US).indexOf(" GROUP ");

        // Find the end of the table name
        int endOfTable = afterFrom.length();
        if (whereIdx >= 0 && whereIdx < endOfTable) endOfTable = whereIdx;
        if (orderIdx >= 0 && orderIdx < endOfTable) endOfTable = orderIdx;
        if (limitIdx >= 0 && limitIdx < endOfTable) endOfTable = limitIdx;
        if (groupIdx >= 0 && groupIdx < endOfTable) endOfTable = groupIdx;

        tableName = afterFrom.substring(0, endOfTable).trim();
        // Handle aliased tables: "tableName t" -> "tableName"
        int spaceInTable = tableName.indexOf(' ');
        if (spaceInTable > 0) tableName = tableName.substring(0, spaceInTable);
        tableName = stripQuotes(tableName);

        if (whereIdx >= 0) {
            int endOfWhere = afterFrom.length();
            if (orderIdx >= 0 && orderIdx > whereIdx) endOfWhere = Math.min(endOfWhere, orderIdx);
            if (limitIdx >= 0 && limitIdx > whereIdx) endOfWhere = Math.min(endOfWhere, limitIdx);
            if (groupIdx >= 0 && groupIdx > whereIdx) endOfWhere = Math.min(endOfWhere, groupIdx);
            whereClause = afterFrom.substring(whereIdx + 7, endOfWhere).trim();
        }

        // Handle aggregate: SELECT COUNT(*) FROM table [WHERE ...]
        if (colPart.toUpperCase(Locale.US).equals("COUNT(*)")) {
            String key = tableName.toLowerCase(Locale.US);
            Table t = tables.get(key);
            int rowCount = 0;
            if (t != null) {
                if (whereClause != null && !whereClause.isEmpty()) {
                    for (Map<String, Object> row : t.rows) {
                        if (matchesWhere(row, whereClause, selectionArgs)) {
                            rowCount++;
                        }
                    }
                } else {
                    rowCount = t.rows.size();
                }
            }
            MatrixCursor mc = new MatrixCursor(new String[]{"COUNT(*)"});
            mc.addRow(new Object[]{rowCount});
            return mc;
        }

        // Determine columns
        String[] columns = null;
        if (!colPart.equals("*")) {
            String[] rawCols = SplitHelper.ch(colPart, ',');
            columns = new String[rawCols.length];
            for (int i = 0; i < rawCols.length; i++) {
                columns[i] = stripQuotes(rawCols[i].trim());
                // Handle "col AS alias" - use alias
                String upperCol = columns[i].toUpperCase(Locale.US);
                int asIdx = upperCol.indexOf(" AS ");
                if (asIdx >= 0) {
                    columns[i] = columns[i].substring(asIdx + 4).trim();
                    columns[i] = stripQuotes(columns[i]);
                }
            }
        }

        // Extract ordering
        String orderBy = null;
        if (orderIdx >= 0) {
            String afterOrder = afterFrom.substring(orderIdx + 7);
            // ORDER BY ... up to LIMIT or end
            int nextLimitIdx = afterOrder.toUpperCase(Locale.US).indexOf(" LIMIT ");
            orderBy = (nextLimitIdx >= 0) ? afterOrder.substring(0, nextLimitIdx).trim() : afterOrder.trim();
        }

        // Extract limit
        String limit = null;
        if (limitIdx >= 0) {
            limit = afterFrom.substring(limitIdx + 7).trim();
        }

        return queryInternal(tableName, columns, whereClause, selectionArgs, orderBy, limit);
    }

    // -- WHERE clause matching ------------------------------------------------

    /**
     * Simple WHERE clause matcher supporting:
     * - column = ? (with selectionArgs)
     * - column = 'value'
     * - column = value
     * - col1 = ? AND col2 = ?
     * - null/empty whereClause (matches everything)
     */
    private boolean matchesWhere(Map<String, Object> row, String whereClause, String[] whereArgs) {
        if (whereClause == null || whereClause.trim().isEmpty()) return true;

        String clause = whereClause.trim();

        // Split on AND (case-insensitive) -- basic support
        String[] conditions;
        conditions = SplitHelper.andSplit(clause);

        int argIndex = 0;
        for (String condition : conditions) {
            condition = condition.trim();
            if (condition.isEmpty()) continue;

            // Parse: column op value
            // Support =, !=, <>, IS NULL, IS NOT NULL
            String upperCond = condition.toUpperCase(Locale.US).trim();

            if (upperCond.contains(" IS NOT NULL")) {
                String col = condition.substring(0, upperCond.indexOf(" IS NOT NULL")).trim();
                col = stripQuotes(col);
                if (row.get(col) == null) return false;
                continue;
            }
            if (upperCond.contains(" IS NULL")) {
                String col = condition.substring(0, upperCond.indexOf(" IS NULL")).trim();
                col = stripQuotes(col);
                if (row.get(col) != null) return false;
                continue;
            }

            // Handle operators: =, !=, <>, <, >, <=, >=
            String col = null;
            String val = null;
            String op = null;

            for (String testOp : new String[]{"!=", "<>", "<=", ">=", "=", "<", ">"}) {
                int opIdx = condition.indexOf(testOp);
                if (opIdx >= 0) {
                    col = condition.substring(0, opIdx).trim();
                    val = condition.substring(opIdx + testOp.length()).trim();
                    op = testOp;
                    break;
                }
            }

            if (col == null || op == null) continue; // unparseable, skip
            col = stripQuotes(col);

            // Resolve value
            String compareValue;
            if ("?".equals(val)) {
                compareValue = (whereArgs != null && argIndex < whereArgs.length)
                        ? whereArgs[argIndex++] : null;
            } else {
                // Strip quotes from literal values
                compareValue = stripQuotes(val);
                if (compareValue.equalsIgnoreCase("NULL")) compareValue = null;
            }

            Object rowVal = row.get(col);
            String rowStr = rowVal != null ? rowVal.toString() : null;

            boolean match;
            switch (op) {
                case "=":
                    if (compareValue == null) {
                        match = (rowVal == null);
                    } else {
                        match = compareValue.equals(rowStr);
                    }
                    break;
                case "!=":
                case "<>":
                    if (compareValue == null) {
                        match = (rowVal != null);
                    } else {
                        match = !compareValue.equals(rowStr);
                    }
                    break;
                case "<":
                    match = compareNumeric(rowStr, compareValue) < 0;
                    break;
                case ">":
                    match = compareNumeric(rowStr, compareValue) > 0;
                    break;
                case "<=":
                    match = compareNumeric(rowStr, compareValue) <= 0;
                    break;
                case ">=":
                    match = compareNumeric(rowStr, compareValue) >= 0;
                    break;
                default:
                    match = true;
                    break;
            }
            if (!match) return false;
        }
        return true;
    }

    private int compareNumeric(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        // Avoid Double.parseDouble (needs native parseDblImpl on KitKat)
        // Use simple long comparison for integers, fallback to string compare
        try {
            long la = Long.parseLong(a);
            long lb = Long.parseLong(b);
            return Long.compare(la, lb);
        } catch (NumberFormatException e) {
            return a.compareTo(b);
        }
    }

    // -- Sorting --------------------------------------------------------------

    private void sortRows(List<Map<String, Object>> rows, String orderBy) {
        // Parse: "column [ASC|DESC]"
        String trimmed = orderBy.trim();
        // Support "BY column" prefix (from ORDER BY parsing)
        if (trimmed.toUpperCase(Locale.US).startsWith("BY ")) {
            trimmed = trimmed.substring(3).trim();
        }
        String[] parts = SplitHelper.ws(trimmed);
        final String sortCol = stripQuotes(parts[0]);
        final boolean ascending = !(parts.length > 1 && parts[1].toUpperCase(Locale.US).equals("DESC"));

        Collections.sort(rows, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                Object va = a.get(sortCol);
                Object vb = b.get(sortCol);
                int cmp = compareNumeric(
                        va != null ? va.toString() : null,
                        vb != null ? vb.toString() : null);
                return ascending ? cmp : -cmp;
            }
        });
    }

    // -- Utility --------------------------------------------------------------

    private boolean containsIgnoreCase(List<String> list, String target) {
        for (String s : list) {
            if (s.equalsIgnoreCase(target)) return true;
        }
        return false;
    }
}
