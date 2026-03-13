package android.content;

import android.database.Cursor;
import android.net.Uri;

import java.io.File;

/**
 * Android-compatible FileProvider shim. Extends ContentProvider.
 * Provides getUriForFile() static method for file sharing URIs.
 */
public class FileProvider extends ContentProvider {

    /**
     * Return a content URI for a given File. Stub -- returns a content:// URI
     * constructed from the authority and file path.
     */
    public static Uri getUriForFile(Context context, String authority, File file) {
        return Uri.parse("content://" + authority + "/" + file.getName());
    }

    @Override
    public boolean onCreate() {
        return true; // stub
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null; // stub
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null; // stub
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0; // stub
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0; // stub
    }

    @Override
    public String getType(Uri uri) {
        return null; // stub
    }
}
