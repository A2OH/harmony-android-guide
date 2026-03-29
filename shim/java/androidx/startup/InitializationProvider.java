package androidx.startup;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Stub InitializationProvider — AndroidX Startup uses this to auto-init libraries.
 * In our environment, we skip auto-initialization.
 */
public class InitializationProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        System.out.println("[InitializationProvider] onCreate (stub) — skipping auto-init");
        return true;
    }

    @Override public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String sel, String[] selArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String sel, String[] selArgs) { return 0; }
}
