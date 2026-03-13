package android.database;

public class DatabaseUtils {
    public DatabaseUtils() {}

    public static final int STATEMENT_ABORT = 0;
    public static final int STATEMENT_ATTACH = 0;
    public static final int STATEMENT_BEGIN = 0;
    public static final int STATEMENT_COMMIT = 0;
    public static final int STATEMENT_DDL = 0;
    public static final int STATEMENT_OTHER = 0;
    public static final int STATEMENT_PRAGMA = 0;
    public static final int STATEMENT_SELECT = 0;
    public static final int STATEMENT_UNPREPARED = 0;
    public static final int STATEMENT_UPDATE = 0;
    public static void appendEscapedSQLString(Object p0, Object p1) {}
    public static Object appendSelectionArgs(Object p0, Object p1) { return null; }
    public static void appendValueToSql(Object p0, Object p1) {}
    public static void bindObjectToProgram(Object p0, Object p1, Object p2) {}
    public static Object blobFileDescriptorForQuery(Object p0, Object p1, Object p2) { return null; }
    public static Object blobFileDescriptorForQuery(Object p0, Object p1) { return null; }
    public static Object concatenateWhere(Object p0, Object p1) { return null; }
    public static void createDbFromSqlStatements(Object p0, Object p1, Object p2, Object p3) {}
    public static void cursorDoubleToContentValues(Object p0, Object p1, Object p2, Object p3) {}
    public static void cursorDoubleToContentValuesIfPresent(Object p0, Object p1, Object p2) {}
    public static void cursorDoubleToCursorValues(Object p0, Object p1, Object p2) {}
    public static void cursorFloatToContentValuesIfPresent(Object p0, Object p1, Object p2) {}
    public static void cursorIntToContentValues(Object p0, Object p1, Object p2) {}
    public static void cursorIntToContentValues(Object p0, Object p1, Object p2, Object p3) {}
    public static void cursorIntToContentValuesIfPresent(Object p0, Object p1, Object p2) {}
    public static void cursorLongToContentValues(Object p0, Object p1, Object p2) {}
    public static void cursorLongToContentValues(Object p0, Object p1, Object p2, Object p3) {}
    public static void cursorLongToContentValuesIfPresent(Object p0, Object p1, Object p2) {}
    public static void cursorRowToContentValues(Object p0, Object p1) {}
    public static void cursorShortToContentValuesIfPresent(Object p0, Object p1, Object p2) {}
    public static void cursorStringToContentValues(Object p0, Object p1, Object p2) {}
    public static void cursorStringToContentValues(Object p0, Object p1, Object p2, Object p3) {}
    public static void cursorStringToContentValuesIfPresent(Object p0, Object p1, Object p2) {}
    public static void cursorStringToInsertHelper(Object p0, Object p1, Object p2, Object p3) {}
    public static void dumpCurrentRow(Object p0) {}
    public static void dumpCurrentRow(Object p0, Object p1) {}
    public static Object dumpCurrentRowToString(Object p0) { return null; }
    public static void dumpCursor(Object p0) {}
    public static void dumpCursor(Object p0, Object p1) {}
    public static Object dumpCursorToString(Object p0) { return null; }
    public static Object getCollationKey(Object p0) { return null; }
    public static Object getHexCollationKey(Object p0) { return null; }
    public static int getSqlStatementType(Object p0) { return 0; }
    public static long longForQuery(Object p0, Object p1, Object p2) { return 0L; }
    public static long longForQuery(Object p0, Object p1) { return 0L; }
    public static long queryNumEntries(Object p0, Object p1) { return 0L; }
    public static long queryNumEntries(Object p0, Object p1, Object p2) { return 0L; }
    public static long queryNumEntries(Object p0, Object p1, Object p2, Object p3) { return 0L; }
    public static void readExceptionFromParcel(Object p0) {}
    public static void readExceptionWithFileNotFoundExceptionFromParcel(Object p0) {}
    public static void readExceptionWithOperationApplicationExceptionFromParcel(Object p0) {}
    public static Object sqlEscapeString(Object p0) { return null; }
    public static Object stringForQuery(Object p0, Object p1, Object p2) { return null; }
    public static Object stringForQuery(Object p0, Object p1) { return null; }
    public static void writeExceptionToParcel(Object p0, Object p1) {}
}
