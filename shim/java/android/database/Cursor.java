package android.database;

/**
 * Shim: android.database.Cursor interface → @ohos.data.relationalStore.ResultSet
 * Tier 1 — near-direct mapping.
 */
public interface Cursor {
    boolean moveToFirst();
    boolean moveToNext();
    boolean moveToPosition(int position);
    boolean moveToPrevious();
    boolean moveToLast();
    boolean isAfterLast();
    boolean isBeforeFirst();
    boolean isFirst();
    boolean isLast();

    int getPosition();
    int getCount();
    int getColumnCount();

    int getColumnIndex(String columnName);
    int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException;
    String getColumnName(int columnIndex);
    String[] getColumnNames();

    String getString(int columnIndex);
    int getInt(int columnIndex);
    long getLong(int columnIndex);
    float getFloat(int columnIndex);
    double getDouble(int columnIndex);
    short getShort(int columnIndex);
    byte[] getBlob(int columnIndex);

    boolean isNull(int columnIndex);
    int getType(int columnIndex);

    boolean move(int offset);
    boolean getWantsAllOnMoveCalls();

    void registerContentObserver(android.database.ContentObserver observer);
    void unregisterContentObserver(android.database.ContentObserver observer);
    void registerDataSetObserver(android.database.DataSetObserver observer);
    void unregisterDataSetObserver(android.database.DataSetObserver observer);

    void setNotificationUri(android.content.ContentResolver cr, android.net.Uri uri);
    android.net.Uri getNotificationUri();

    void setExtras(android.os.Bundle extras);
    android.os.Bundle getExtras();
    android.os.Bundle respond(android.os.Bundle extras);

    void close();
    boolean isClosed();

    // Field type constants
    static final int FIELD_TYPE_NULL    = 0;
    static final int FIELD_TYPE_INTEGER = 1;
    static final int FIELD_TYPE_FLOAT   = 2;
    static final int FIELD_TYPE_STRING  = 3;
    static final int FIELD_TYPE_BLOB    = 4;
}
