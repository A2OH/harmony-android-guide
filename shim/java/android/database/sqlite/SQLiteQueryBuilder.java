package android.database.sqlite;
import android.content.res.Configuration;
import android.content.res.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Android-compatible SQLiteQueryBuilder shim.
 * Helper class for building SQL SELECT queries. Mirrors the public surface of
 * {@code android.database.sqlite.SQLiteQueryBuilder} including the static
 * {@link #buildQueryString} utility and the most commonly used instance methods.
 */
public class SQLiteQueryBuilder {

    private static final Pattern PATTERN_COLUMN_NAME =
            Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");

    private String mTables = "";
    private StringBuilder mWhereClause = null;
    private boolean mDistinct = false;
    private Map<String, String> mProjectionMap = null;
    private boolean mStrictColumns = false;

    // -------------------------------------------------------------------------
    // Static helper
    // -------------------------------------------------------------------------

    /**
     * Builds a SQL query string from individual components.
     *
     * @param distinct     true to include DISTINCT keyword
     * @param tables       the table(s) to query (FROM clause)
     * @param columns      the columns to select, or null for *
     * @param where        a WHERE clause (without the keyword), or null
     * @param groupBy      a GROUP BY expression, or null
     * @param having       a HAVING clause (without the keyword), or null
     * @param orderBy      an ORDER BY expression, or null
     * @param limit        a LIMIT expression, or null
     * @return the SQL SELECT string
     */
    public static String buildQueryString(
            boolean distinct,
            String tables,
            String[] columns,
            String where,
            String groupBy,
            String having,
            String orderBy,
            String limit) {

        if (isEmpty(groupBy) && !isEmpty(having)) {
            throw new IllegalArgumentException(
                    "HAVING clauses are only permitted when using a GROUP BY clause");
        }

        StringBuilder sb = new StringBuilder(120);
        sb.append("SELECT ");
        if (distinct) sb.append("DISTINCT ");

        if (columns != null && columns.length > 0) {
            appendColumns(sb, columns);
        } else {
            sb.append("* ");
        }

        sb.append("FROM ");
        sb.append(tables);

        appendClause(sb, " WHERE ",    where);
        appendClause(sb, " GROUP BY ", groupBy);
        appendClause(sb, " HAVING ",   having);
        appendClause(sb, " ORDER BY ", orderBy);
        appendClause(sb, " LIMIT ",    limit);

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    /** Sets the list of tables to query. May include JOINs. */
    public void setTables(String inTables) {
        mTables = (inTables == null) ? "" : inTables;
    }

    /** Returns the current table string. */
    public String getTables() {
        return mTables;
    }

    /**
     * Sets a projection map that maps column names used in the query to the
     * actual column expressions in the database.
     *
     * @param columnMap a map from external column names to internal expressions
     */
    public void setProjectionMap(Map<String, String> columnMap) {
        mProjectionMap = columnMap;
    }

    /** Returns the current projection map. */
    public Map<String, String> getProjectionMap() {
        return mProjectionMap;
    }

    /**
     * Mark the query as DISTINCT.
     *
     * @param distinct true to add DISTINCT to the SELECT
     */
    public void setDistinct(boolean distinct) {
        mDistinct = distinct;
    }

    /** Returns whether DISTINCT is set. */
    public boolean isDistinct() {
        return mDistinct;
    }

    /**
     * Appends a WHERE clause chunk. Multiple calls are AND-ed together.
     *
     * @param inWhere a partial WHERE expression (without AND/WHERE keywords)
     */
    public void appendWhere(CharSequence inWhere) {
        if (mWhereClause == null) {
            mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        if (mWhereClause.length() > 0) {
            mWhereClause.append(" AND ");
        }
        mWhereClause.append(inWhere);
    }

    /**
     * Appends a WHERE clause chunk enclosing it in parentheses.
     *
     * @param inWhere a partial WHERE expression
     */
    public void appendWhereEscapeString(String inWhere) {
        appendWhere("('" + inWhere.replace("'", "''") + "')");
    }

    // -------------------------------------------------------------------------
    // Query building
    // -------------------------------------------------------------------------

    /**
     * Builds the SQL query string for this builder using the given projection,
     * selection, and order parameters.
     *
     * @param projectionIn  columns to return, or null for *
     * @param selection     extra WHERE clause, or null
     * @param selectionArgs not used in string building; kept for API compat
     * @param groupBy       GROUP BY expression, or null
     * @param having        HAVING clause, or null
     * @param sortOrder     ORDER BY expression, or null
     * @return the SQL SELECT string
     */
    public String buildQuery(
            String[] projectionIn,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String sortOrder) {
        return buildQuery(projectionIn, selection, selectionArgs, groupBy, having, sortOrder, null);
    }

    /**
     * Builds the SQL query string including an optional LIMIT clause.
     */
    public String buildQuery(
            String[] projectionIn,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String sortOrder,
            String limit) {

        String[] projection = computeProjection(projectionIn);
        String where = computeWhere(selection);

        return buildQueryString(mDistinct, mTables, projection, where,
                groupBy, having, sortOrder, limit);
    }

    /**
     * Convenience wrapper that performs the query against the given
     * {@link SQLiteDatabase} and returns a cursor. The database parameter is
     * typed as Object for portability.
     *
     * @param db           the database to query (Object for portability)
     * @param projectionIn columns to return
     * @param selection    WHERE clause
     * @param selectionArgs bind arguments for the WHERE clause
     * @param groupBy      GROUP BY expression
     * @param having       HAVING clause
     * @param sortOrder    ORDER BY expression
     * @return an {@link SQLiteCursor} stub
     */
    public Object query(
            Object db,
            String[] projectionIn,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String sortOrder) {
        return query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, null);
    }

    /**
     * Convenience wrapper with an additional LIMIT clause.
     */
    public Object query(
            Object db,
            String[] projectionIn,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String sortOrder,
            String limit) {

        String sql = buildQuery(projectionIn, selection, selectionArgs,
                groupBy, having, sortOrder, limit);
        System.out.println("[SQLiteQueryBuilder] query: " + sql);
        // Return a stub cursor; in a real port this would call db.rawQuery(sql, selectionArgs)
        return new SQLiteCursor((SQLiteCursorDriver) db, mTables, sql);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String[] computeProjection(String[] projectionIn) {
        if (projectionIn == null || projectionIn.length == 0) {
            if (mProjectionMap != null) {
                Set<String> keySet = mProjectionMap.keySet();
                return keySet.toArray(new String[0]);
            }
            return null;
        }
        if (mProjectionMap != null) {
            String[] result = new String[projectionIn.length];
            for (int i = 0; i < projectionIn.length; i++) {
                String col = projectionIn[i];
                String mapped = mProjectionMap.get(col);
                result[i] = (mapped != null) ? mapped : col;
            }
            return result;
        }
        return projectionIn;
    }

    private String computeWhere(String selection) {
        boolean hasBuilderWhere = mWhereClause != null && mWhereClause.length() > 0;
        boolean hasExtraWhere   = !isEmpty(selection);

        if (hasBuilderWhere && hasExtraWhere) {
            return "(" + mWhereClause + ") AND (" + selection + ")";
        } else if (hasBuilderWhere) {
            return mWhereClause.toString();
        } else if (hasExtraWhere) {
            return selection;
        }
        return null;
    }

    private static void appendClause(StringBuilder sb, String name, String clause) {
        if (!isEmpty(clause)) {
            sb.append(name);
            sb.append(clause);
        }
    }

    private static void appendColumns(StringBuilder sb, String[] columns) {
        int n = columns.length;
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(", ");
            sb.append(columns[i]);
        }
        sb.append(' ');
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
