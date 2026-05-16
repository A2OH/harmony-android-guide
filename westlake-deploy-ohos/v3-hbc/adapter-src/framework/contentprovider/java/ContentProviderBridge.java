/*
 * ContentProviderBridge.java
 *
 * Implements Android IContentProvider and routes data operations to
 * OpenHarmony DataShareExtensionAbility (Stage model) via JNI.
 *
 * Supports two scenarios:
 *   1. Android App -> OH system DataShare (contacts, media, etc.)
 *      URI converted from content://authority to datashare:///bundleName.
 *      Data operations forwarded to OH DataShareHelper via JNI/IPC.
 *
 *   2. Android App -> Another Android App's ContentProvider
 *      Managed by ContentProviderRegistry (in-process registry).
 *      No OH system service involvement.
 *
 * Extends ContentProviderNative (Binder stub) so it can be returned
 * directly from ActivityManagerAdapter.getContentProvider() as a valid
 * IContentProvider Binder reference.
 *
 * Reference:
 *   Android: core/java/android/content/ContentProviderNative.java
 *   OH:      DataShare::DataShareHelper (Stage model client)
 */
package adapter.contentprovider;

import android.content.AttributionSource;
import android.content.ContentProviderNative;
import android.content.ContentValues;
import android.content.IContentProvider;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallback;
import android.util.Log;

import adapter.contentprovider.ContentProviderUriConverter;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Bridge that implements IContentProvider and forwards operations to
 * OH DataShareHelper (Stage model) or a local Android ContentProvider.
 *
 * One instance per authority (content provider).
 */
public class ContentProviderBridge extends ContentProviderNative {

    private static final String TAG = "OH_CPBridge";

    private final String mAuthority;
    private final String mOhBundleName;

    // Native OH DataShareHelper connection handle, obtained via JNI.
    // 0 means not connected.
    private long mNativeHandle;

    public ContentProviderBridge(String authority) {
        mAuthority = authority;
        mOhBundleName = ContentProviderUriConverter.authorityToBundleName(authority);
        mNativeHandle = nativeConnect(mOhBundleName);
        Log.d(TAG, "Created bridge: authority=" + authority
                + " -> ohBundle=" + mOhBundleName
                + ", handle=" + mNativeHandle);
    }

    /**
     * Release native resources. Called when this bridge is no longer needed.
     */
    public void release() {
        if (mNativeHandle != 0) {
            nativeDisconnect(mNativeHandle);
            mNativeHandle = 0;
            Log.d(TAG, "Released bridge: authority=" + mAuthority);
        }
    }

    public String getAuthority() {
        return mAuthority;
    }

    // ========================================================================
    // IContentProvider implementation: CRUD operations
    // ========================================================================

    // [BRIDGED] query -> OH DataShareHelper.Query()
    @Override
    public Cursor query(AttributionSource attributionSource, Uri uri,
            String[] projection, Bundle queryArgs,
            ICancellationSignal cancellationSignal) {
        if (mNativeHandle == 0) {
            Log.e(TAG, "query: not connected, authority=" + mAuthority);
            return null;
        }

        String ohUri = ContentProviderUriConverter.toOhUri(uri);

        // Extract SQL selection/selectionArgs from Bundle (AOSP 14 style)
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        if (queryArgs != null) {
            selection = queryArgs.getString("android:query-arg-sql-selection");
            selectionArgs = queryArgs.getStringArray("android:query-arg-sql-selection-args");
            sortOrder = queryArgs.getString("android:query-arg-sql-sort-order");
        }

        Log.d(TAG, "[BRIDGED] query: " + uri + " -> " + ohUri);
        return nativeQuery(mNativeHandle, ohUri, projection, selection, selectionArgs, sortOrder);
    }

    // [BRIDGED] insert -> OH DataShareHelper.Insert()
    @Override
    public Uri insert(AttributionSource attributionSource, Uri uri,
            ContentValues values, Bundle extras) {
        if (mNativeHandle == 0) {
            Log.e(TAG, "insert: not connected, authority=" + mAuthority);
            return null;
        }

        String ohUri = ContentProviderUriConverter.toOhUri(uri);
        Log.d(TAG, "[BRIDGED] insert: " + uri + " -> " + ohUri);

        int rowId = nativeInsert(mNativeHandle, ohUri,
                contentValuesToKeys(values), contentValuesToValues(values),
                contentValuesToTypes(values));
        if (rowId >= 0) {
            return Uri.withAppendedPath(uri, String.valueOf(rowId));
        }
        return null;
    }

    // [BRIDGED] update -> OH DataShareHelper.Update()
    @Override
    public int update(AttributionSource attributionSource, Uri uri,
            ContentValues values, Bundle extras) {
        if (mNativeHandle == 0) {
            Log.e(TAG, "update: not connected, authority=" + mAuthority);
            return 0;
        }

        String ohUri = ContentProviderUriConverter.toOhUri(uri);

        String selection = null;
        String[] selectionArgs = null;
        if (extras != null) {
            selection = extras.getString("android:query-arg-sql-selection");
            selectionArgs = extras.getStringArray("android:query-arg-sql-selection-args");
        }

        Log.d(TAG, "[BRIDGED] update: " + uri + " -> " + ohUri);
        return nativeUpdate(mNativeHandle, ohUri,
                contentValuesToKeys(values), contentValuesToValues(values),
                contentValuesToTypes(values), selection, selectionArgs);
    }

    // [BRIDGED] delete -> OH DataShareHelper.Delete()
    @Override
    public int delete(AttributionSource attributionSource, Uri uri,
            Bundle extras) {
        if (mNativeHandle == 0) {
            Log.e(TAG, "delete: not connected, authority=" + mAuthority);
            return 0;
        }

        String ohUri = ContentProviderUriConverter.toOhUri(uri);

        String selection = null;
        String[] selectionArgs = null;
        if (extras != null) {
            selection = extras.getString("android:query-arg-sql-selection");
            selectionArgs = extras.getStringArray("android:query-arg-sql-selection-args");
        }

        Log.d(TAG, "[BRIDGED] delete: " + uri + " -> " + ohUri);
        return nativeDelete(mNativeHandle, ohUri, selection, selectionArgs);
    }

    // ========================================================================
    // IContentProvider implementation: Bulk operations
    // ========================================================================

    // [BRIDGED] bulkInsert -> OH DataShareHelper.BatchInsert()
    @Override
    public int bulkInsert(AttributionSource attributionSource, Uri uri,
            ContentValues[] values) {
        if (mNativeHandle == 0) {
            Log.e(TAG, "bulkInsert: not connected, authority=" + mAuthority);
            return 0;
        }

        String ohUri = ContentProviderUriConverter.toOhUri(uri);
        Log.d(TAG, "[BRIDGED] bulkInsert: " + uri + " -> " + ohUri + ", count=" + values.length);

        // Batch insert: serialize all ContentValues into parallel arrays
        int count = 0;
        for (ContentValues cv : values) {
            int result = nativeInsert(mNativeHandle, ohUri,
                    contentValuesToKeys(cv), contentValuesToValues(cv),
                    contentValuesToTypes(cv));
            if (result >= 0) count++;
        }
        return count;
    }

    // ========================================================================
    // IContentProvider implementation: Type & File operations
    // ========================================================================

    // [BRIDGED] getType -> OH DataShareHelper.GetType()
    @Override
    public String getType(AttributionSource attributionSource, Uri uri) {
        if (mNativeHandle == 0) return null;
        String ohUri = ContentProviderUriConverter.toOhUri(uri);
        Log.d(TAG, "[BRIDGED] getType: " + uri);
        return nativeGetType(mNativeHandle, ohUri);
    }

    // [BRIDGED] openFile -> OH DataShareHelper.OpenFile()
    @Override
    public ParcelFileDescriptor openFile(AttributionSource attributionSource,
            Uri uri, String mode, ICancellationSignal signal) throws FileNotFoundException {
        if (mNativeHandle == 0) {
            throw new FileNotFoundException("Not connected: " + uri);
        }
        String ohUri = ContentProviderUriConverter.toOhUri(uri);
        Log.d(TAG, "[BRIDGED] openFile: " + uri + ", mode=" + mode);

        int fd = nativeOpenFile(mNativeHandle, ohUri, mode);
        if (fd < 0) {
            throw new FileNotFoundException("OH DataShare openFile failed: " + uri);
        }
        return ParcelFileDescriptor.adoptFd(fd);
    }

    // [BRIDGED] call -> OH DataShareHelper.Call() (custom extension method)
    @Override
    public Bundle call(AttributionSource attributionSource, String authority,
            String method, String arg, Bundle extras) {
        if (mNativeHandle == 0) return null;
        Log.d(TAG, "[BRIDGED] call: authority=" + authority
                + ", method=" + method + ", arg=" + arg);
        return nativeCall(mNativeHandle, authority, method, arg, extras);
    }

    // ========================================================================
    // IContentProvider implementation: Stub methods (no OH equivalent)
    // ========================================================================

    // [STUB] openAssetFile - OH does not have a direct equivalent
    @Override
    public AssetFileDescriptor openAssetFile(AttributionSource attributionSource,
            Uri uri, String mode, ICancellationSignal signal) throws FileNotFoundException {
        Log.d(TAG, "[STUB] openAssetFile: " + uri);
        // Fall back to openFile and wrap as AssetFileDescriptor
        ParcelFileDescriptor pfd = openFile(attributionSource, uri, mode, signal);
        if (pfd != null) {
            return new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
        }
        return null;
    }

    // [STUB] openTypedAssetFile
    @Override
    public AssetFileDescriptor openTypedAssetFile(AttributionSource attributionSource,
            Uri uri, String mimeTypeFilter, Bundle opts,
            ICancellationSignal signal) throws FileNotFoundException {
        Log.d(TAG, "[STUB] openTypedAssetFile: " + uri);
        return openAssetFile(attributionSource, uri, "r", signal);
    }

    // [STUB] applyBatch
    @Override
    public android.content.ContentProviderResult[] applyBatch(
            AttributionSource attributionSource, String authority,
            ArrayList<android.content.ContentProviderOperation> operations) {
        Log.d(TAG, "[STUB] applyBatch: authority=" + authority
                + ", ops=" + (operations != null ? operations.size() : 0));
        // Return empty results for now
        return new android.content.ContentProviderResult[0];
    }

    // [STUB] getStreamTypes
    @Override
    public String[] getStreamTypes(AttributionSource attributionSource,
            Uri uri, String mimeTypeFilter) {
        Log.d(TAG, "[STUB] getStreamTypes: " + uri);
        return null;
    }

    // [STUB] canonicalize -> OH DataShareHelper.NormalizeUri()
    @Override
    public Uri canonicalize(AttributionSource attributionSource, Uri uri) {
        Log.d(TAG, "[STUB] canonicalize: " + uri);
        return uri;
    }

    // [STUB] uncanonicalize -> OH DataShareHelper.DenormalizeUri()
    @Override
    public Uri uncanonicalize(AttributionSource attributionSource, Uri uri) {
        Log.d(TAG, "[STUB] uncanonicalize: " + uri);
        return uri;
    }

    // [STUB] refresh
    @Override
    public boolean refresh(AttributionSource attributionSource, Uri uri,
            Bundle extras, ICancellationSignal signal) {
        Log.d(TAG, "[STUB] refresh: " + uri);
        return false;
    }

    // [STUB] checkUriPermission - OH uses different permission model
    @Override
    public int checkUriPermission(AttributionSource attributionSource,
            Uri uri, int uid, int modeFlags) {
        Log.d(TAG, "[STUB] checkUriPermission: " + uri);
        // Grant by default within adapter
        return android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    // [STUB] createCancellationSignal
    @Override
    public ICancellationSignal createCancellationSignal() {
        Log.d(TAG, "[STUB] createCancellationSignal");
        return null;
    }

    // [STUB] Async variants
    @Override
    public void getTypeAsync(AttributionSource attributionSource,
            Uri uri, RemoteCallback callback) {
        Log.d(TAG, "[STUB] getTypeAsync: " + uri);
    }

    @Override
    public void canonicalizeAsync(AttributionSource attributionSource,
            Uri uri, RemoteCallback callback) {
        Log.d(TAG, "[STUB] canonicalizeAsync: " + uri);
    }

    @Override
    public void uncanonicalizeAsync(AttributionSource attributionSource,
            Uri uri, RemoteCallback callback) {
        Log.d(TAG, "[STUB] uncanonicalizeAsync: " + uri);
    }

    @Override
    public void getTypeAnonymousAsync(Uri uri, RemoteCallback callback) {
        Log.d(TAG, "[STUB] getTypeAnonymousAsync: " + uri);
    }

    // ========================================================================
    // ContentValues serialization helpers
    // ========================================================================

    /**
     * Extract keys from ContentValues as a String array for JNI transfer.
     */
    private static String[] contentValuesToKeys(ContentValues values) {
        if (values == null || values.size() == 0) return new String[0];
        return values.keySet().toArray(new String[0]);
    }

    /**
     * Extract values from ContentValues as a String array for JNI transfer.
     * Each value is converted to its string representation.
     * null values are represented as the string "\0NULL\0".
     */
    private static String[] contentValuesToValues(ContentValues values) {
        if (values == null || values.size() == 0) return new String[0];
        String[] keys = contentValuesToKeys(values);
        String[] result = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            Object val = values.get(keys[i]);
            result[i] = (val != null) ? val.toString() : "\0NULL\0";
        }
        return result;
    }

    /**
     * Extract value type codes from ContentValues for JNI type dispatch.
     * Type codes: 0=null, 1=string, 2=int, 3=long, 4=float, 5=double, 6=blob
     */
    private static int[] contentValuesToTypes(ContentValues values) {
        if (values == null || values.size() == 0) return new int[0];
        String[] keys = contentValuesToKeys(values);
        int[] types = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            Object val = values.get(keys[i]);
            if (val == null) {
                types[i] = 0;
            } else if (val instanceof String) {
                types[i] = 1;
            } else if (val instanceof Integer) {
                types[i] = 2;
            } else if (val instanceof Long) {
                types[i] = 3;
            } else if (val instanceof Float) {
                types[i] = 4;
            } else if (val instanceof Double) {
                types[i] = 5;
            } else if (val instanceof byte[]) {
                types[i] = 6;
            } else {
                types[i] = 1; // Default to string
            }
        }
        return types;
    }

    // ========================================================================
    // Native methods (implemented in oh_datashare_client.cpp)
    // ========================================================================

    /**
     * Connect to OH DataShareHelper for the given bundleName.
     * @return native handle (>0 on success, 0 on failure)
     */
    private static native long nativeConnect(String bundleName);

    /**
     * Disconnect and release OH DataShareHelper.
     */
    private static native void nativeDisconnect(long handle);

    /**
     * Execute query via OH DataShareHelper.Query().
     * Returns a MatrixCursor filled with results from OH AbsSharedResultSet.
     */
    private static native Cursor nativeQuery(long handle, String ohUri,
            String[] projection, String selection, String[] selectionArgs,
            String sortOrder);

    /**
     * Execute insert via OH DataShareHelper.Insert().
     * ContentValues are passed as parallel arrays: keys[], values[], types[].
     * @return row ID of inserted record, or -1 on error
     */
    private static native int nativeInsert(long handle, String ohUri,
            String[] keys, String[] values, int[] types);

    /**
     * Execute update via OH DataShareHelper.Update().
     * @return number of rows updated
     */
    private static native int nativeUpdate(long handle, String ohUri,
            String[] keys, String[] values, int[] types,
            String selection, String[] selectionArgs);

    /**
     * Execute delete via OH DataShareHelper.Delete().
     * @return number of rows deleted
     */
    private static native int nativeDelete(long handle, String ohUri,
            String selection, String[] selectionArgs);

    /**
     * Get MIME type for the given URI.
     */
    private static native String nativeGetType(long handle, String ohUri);

    /**
     * Open file via OH DataShareHelper.OpenFile().
     * @return file descriptor (>= 0 on success, -1 on error)
     */
    private static native int nativeOpenFile(long handle, String ohUri, String mode);

    /**
     * Call custom method via OH DataShareHelper.
     */
    private static native Bundle nativeCall(long handle, String authority,
            String method, String arg, Bundle extras);

    @Override
    public String getProviderName() {
        return mAuthority;
    }
}