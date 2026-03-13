package android.database.sqlite;

import android.content.Context;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.database.sqlite.SQLiteOpenHelper → @ohos.data.relationalStore.getRdbStore
 * Tier 1 — near-direct mapping.
 *
 * Android: subclass SQLiteOpenHelper, override onCreate/onUpgrade, call getWritableDatabase().
 * OH: call relationalStore.getRdbStore() with config, then executeSql for table creation.
 *
 * This shim preserves the SQLiteOpenHelper pattern — apps subclass it unchanged.
 */
public abstract class SQLiteOpenHelper {
    private final String dbName;
    private final int version;
    private SQLiteDatabase database;

    public SQLiteOpenHelper(Context context, String name, Object factory, int version) {
        this.dbName = name;
        this.version = version;
    }

    public abstract void onCreate(SQLiteDatabase db);

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new android.database.SQLException(
            "Can't downgrade database from version " + oldVersion + " to " + newVersion);
    }

    public void onOpen(SQLiteDatabase db) {}

    public synchronized SQLiteDatabase getWritableDatabase() {
        if (database != null && database.isOpen()) {
            return database;
        }
        long handle = OHBridge.rdbStoreOpen(dbName, version);
        database = new SQLiteDatabase(handle);

        // TODO: version tracking — check stored version, call onCreate or onUpgrade
        // For now, always call onCreate (idempotent if using CREATE TABLE IF NOT EXISTS)
        onCreate(database);

        onOpen(database);
        return database;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        return getWritableDatabase(); // OH RdbStore is always read-write
    }

    public synchronized void close() {
        if (database != null) {
            database.close();
            database = null;
        }
    }

    public String getDatabaseName() { return dbName; }
}
