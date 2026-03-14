package android.content;
import android.content.pm.PathPermission;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class ContentProvider implements ComponentCallbacks2 {

    private Context mContext;
    private String mAuthority;
    private String mReadPermission;
    private String mWritePermission;
    private PathPermission[] mPathPermissions;
    private boolean mCreated;
    private Object mCallingIdentity;

    public ContentProvider() {}

    /** Inner class for provider metadata. */
    public static class ProviderInfo {
        public String authority;
        public ProviderInfo() {}
        public ProviderInfo(String authority) { this.authority = authority; }
    }

    public void attachInfo(Context context, Object info) {
        mContext = context;
        if (info instanceof ProviderInfo) {
            mAuthority = ((ProviderInfo) info).authority;
        }
        if (!mCreated) {
            mCreated = onCreate();
        }
    }

    public Context getContext() { return mContext; }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        int count = 0;
        if (values != null) {
            for (ContentValues v : values) {
                if (insert(uri, v) != null) count++;
            }
        }
        return count;
    }

    /**
     * Subclasses must override to handle queries. Default returns null (no data).
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }

    /**
     * Subclasses must override to handle inserts. Default returns null (not implemented).
     */
    public Uri insert(Uri uri, ContentValues values) { return null; }

    /**
     * Subclasses should override to return the MIME type for the given URI.
     * Default returns null, meaning no type.
     */
    public String getType(Uri uri) { return null; }

    /**
     * Call a provider-defined method. Default returns null (method not recognized).
     * Subclasses override to handle specific methods.
     */
    public Bundle call(String method, String arg, Bundle extras) { return null; }

    /**
     * Subclasses must override to handle deletes. Default returns 0 (no rows deleted).
     */
    public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

    /**
     * Delete with Bundle-based selection. Default delegates to the String-based overload
     * if possible, otherwise returns 0.
     */
    public int delete(Uri uri, Bundle extras) {
        // Subclasses should override; default returns 0
        return 0;
    }

    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        if (writer != null) {
            writer.println("ContentProvider: " + getClass().getName());
            if (mAuthority != null) {
                writer.println("  authority=" + mAuthority);
            }
            writer.println("  created=" + mCreated);
        }
    }

    public boolean isTemporary() { return false; }

    public void onCallingPackageChanged() {
        // Hook for subclasses; default is no-op
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // Hook for subclasses; default is no-op
    }

    /**
     * Called when the provider is first created. Subclasses must override.
     * Default returns true indicating successful initialization.
     */
    public boolean onCreate() { return true; }

    public void onLowMemory() {
        // Hook for subclasses; default is no-op
    }

    public void onTrimMemory(int level) {
        // Hook for subclasses; default is no-op
    }

    /**
     * Refresh content identified by the given URI. Default returns false
     * (refresh not supported). Subclasses override to support refresh.
     */
    public boolean refresh(Uri uri, Bundle extras, CancellationSignal cancellationSignal) {
        return false;
    }

    public void restoreCallingIdentity(Object identity) {
        mCallingIdentity = identity;
    }

    public void setPathPermissions(PathPermission[] permissions) {
        mPathPermissions = permissions;
    }

    public PathPermission[] getPathPermissions() {
        return mPathPermissions;
    }

    public void setReadPermission(String permission) {
        mReadPermission = permission;
    }

    public String getReadPermission() {
        return mReadPermission;
    }

    public void setWritePermission(String permission) {
        mWritePermission = permission;
    }

    public String getWritePermission() {
        return mWritePermission;
    }

    public void shutdown() {
        // Hook for subclasses; default is no-op
    }

    /**
     * Subclasses must override to handle updates. Default returns 0 (no rows updated).
     */
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }

    /**
     * Update with Bundle-based selection. Default returns 0.
     */
    public int update(Uri uri, ContentValues values, Bundle extras) {
        return 0;
    }
}
