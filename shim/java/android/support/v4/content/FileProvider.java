package android.support.v4.content;

/**
 * Android-compatible FileProvider shim for android.support.v4.content.
 *
 * android.content.ContentProvider does not exist in this shim tree, so this
 * class is a standalone stub.  Replace the base class with
 * android.content.ContentProvider once that shim is available.
 *
 * Satisfies compile-time references in apps that use
 * FileProvider.getUriForFile() and the ContentProvider callback surface.
 */
public class FileProvider {

    /**
     * Returns a content URI string for the given file using the specified
     * authority.  Shim returns a simple synthetic URI; real file-path
     * resolution requires a manifest {@code <provider>} entry together with
     * a res/xml/file_paths.xml resource on real Android.
     *
     * @param context   application Context (typed Object — Context shim absent)
     * @param authority the authority declared in AndroidManifest.xml
     * @param file      the file whose URI is needed
     * @return a synthetic content URI string
     */
    public static String getUriForFile(Object context, String authority, java.io.File file) {
        return "content://" + authority + "/" + file.getName();
    }

    // -----------------------------------------------------------------------
    // ContentProvider lifecycle / callback stubs
    // -----------------------------------------------------------------------

    public boolean onCreate() {
        return true;
    }

    /**
     * @param uri           the URI to query
     * @param projection    list of columns to put into the cursor
     * @param selection     a selection criteria to apply when filtering rows
     * @param selectionArgs values for {@code ?} placeholders in selection
     * @param sortOrder     how to order the rows
     * @return null (stub)
     */
    public Object query(Object uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    public String getType(Object uri) {
        return null;
    }

    public Object insert(Object uri, Object values) {
        return null;
    }

    public int update(Object uri, Object values,
            String selection, String[] selectionArgs) {
        return 0;
    }

    public int delete(Object uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
