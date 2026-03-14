package android.database.sqlite;
import android.content.Context;
import android.database.DatabaseErrorHandler;

public class SQLiteOpenHelper implements AutoCloseable {
    private final Context mContext;
    private final String mName;
    private final int mNewVersion;
    private final DatabaseErrorHandler mErrorHandler;
    private SQLiteDatabase mDatabase;
    private boolean mIsInitializing;

    public SQLiteOpenHelper(Context p0, String p1, Object p2, int p3) {
        this(p0, p1, p2, p3, null);
    }

    public SQLiteOpenHelper(Context p0, String p1, Object p2, int p3, DatabaseErrorHandler p4) {
        mContext = p0;
        mName = p1;
        mNewVersion = p3;
        mErrorHandler = p4;
    }

    public SQLiteOpenHelper(Context p0, String p1, int p2, Object p3) {
        mContext = p0;
        mName = p1;
        mNewVersion = p2;
        mErrorHandler = null;
    }

    public String getDatabaseName() { return mName; }

    public SQLiteDatabase getWritableDatabase() {
        return getDatabaseLocked();
    }

    public SQLiteDatabase getReadableDatabase() {
        return getDatabaseLocked();
    }

    private SQLiteDatabase getDatabaseLocked() {
        if (mDatabase != null && mDatabase.isOpen()) {
            return mDatabase;
        }
        if (mIsInitializing) {
            throw new IllegalStateException("getDatabase called recursively");
        }
        SQLiteDatabase db = mDatabase;
        try {
            mIsInitializing = true;
            if (db == null) {
                db = new SQLiteDatabase();
                if (mName != null) {
                    db.path = mName;
                }
            }
            onConfigure(db);
            int version = db.getVersion();
            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else if (version > mNewVersion) {
                        onDowngrade(db, version, mNewVersion);
                    } else {
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
            onOpen(db);
            mDatabase = db;
            return db;
        } finally {
            mIsInitializing = false;
        }
    }

    public void close() {
        if (mIsInitializing) {
            throw new IllegalStateException("Closed during initialization");
        }
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public void onConfigure(SQLiteDatabase p0) {}
    public void onCreate(SQLiteDatabase p0) {}

    public void onDowngrade(SQLiteDatabase p0, int p1, int p2) {
        throw new android.database.sqlite.SQLiteException(
            "Can't downgrade database from version " + p1 + " to " + p2);
    }

    public void onOpen(SQLiteDatabase p0) {}
    public void onUpgrade(SQLiteDatabase p0, int p1, int p2) {}
    public void setLookasideConfig(int p0, int p1) {}
    public void setOpenParams(Object p0) {}
    public void setWriteAheadLoggingEnabled(boolean p0) {}
}
