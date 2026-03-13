package android.content;

import android.database.Cursor;
import android.net.Uri;

/**
 * Android-compatible CursorLoader shim. Pure Java stub.
 * A Loader that queries a ContentProvider and returns a Cursor.
 * loadInBackground() always returns null in this shim.
 */
public class CursorLoader extends AsyncTaskLoader<Cursor> {

    private Uri      mUri;
    private String[] mProjection;
    private String   mSelection;
    private String[] mSelectionArgs;
    private String   mSortOrder;

    // ──────────────────────────────────────────────────────────
    // Constructors
    // ──────────────────────────────────────────────────────────

    public CursorLoader(Object context) {
        super(context);
    }

    public CursorLoader(Object context, Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        super(context);
        mUri           = uri;
        mProjection    = projection;
        mSelection     = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder     = sortOrder;
    }

    // ──────────────────────────────────────────────────────────
    // Setters / getters
    // ──────────────────────────────────────────────────────────

    public void setUri(Uri uri)                          { mUri           = uri; }
    public void setProjection(String[] projection)       { mProjection    = projection; }
    public void setSelection(String selection)           { mSelection     = selection; }
    public void setSelectionArgs(String[] selectionArgs) { mSelectionArgs = selectionArgs; }
    public void setSortOrder(String sortOrder)           { mSortOrder     = sortOrder; }

    public Uri      getUri()             { return mUri; }
    public String[] getProjection()      { return mProjection; }
    public String   getSelection()       { return mSelection; }
    public String[] getSelectionArgs()   { return mSelectionArgs; }
    public String   getSortOrder()       { return mSortOrder; }

    // ──────────────────────────────────────────────────────────
    // AsyncTaskLoader implementation
    // ──────────────────────────────────────────────────────────

    /**
     * Performs the query in the background. Stub — always returns null.
     */
    @Override
    public Cursor loadInBackground() {
        return null; // stub — no real ContentProvider in shim layer
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
        // stub — would re-trigger the query
    }

    @Override
    public boolean cancelLoad() {
        return super.cancelLoad();
    }

    @Override
    protected void onStartLoading() {
        // stub
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        // stub — would close outstanding cursor
    }
}
