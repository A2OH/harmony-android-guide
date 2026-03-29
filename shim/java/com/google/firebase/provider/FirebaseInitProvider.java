package com.google.firebase.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.google.firebase.FirebaseApp;

/**
 * Stub FirebaseInitProvider — called before Application.onCreate() to init Firebase.
 */
public class FirebaseInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        if (getContext() != null) {
            FirebaseApp.initializeApp(getContext());
        }
        return false;
    }

    @Override public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String sel, String[] selArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String sel, String[] selArgs) { return 0; }
}
