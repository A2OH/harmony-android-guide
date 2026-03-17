package android.provider;
import android.net.Uri;
import android.os.Build;
import android.net.Uri;
import android.os.Build;
import java.net.URI;
import android.net.Uri;

/**
 * Android-compatible DocumentsContract shim.
 * Provides static helper methods for constructing/deconstructing document URIs
 * and column name constants for document metadata.
 */
public class DocumentsContract {

    public static final String EXTRA_INITIAL_URI         = "android.provider.extra.INITIAL_URI";
    public static final String EXTRA_LOADING             = "loading";
    public static final String EXTRA_INFO                = "info";
    public static final String EXTRA_ERROR               = "error";
    public static final String EXTRA_PROMPT              = "android.provider.extra.PROMPT";
    public static final String EXTRA_EXCLUDE_SELF        = "android.provider.extra.EXCLUDE_SELF";

    public static final String ACTION_MANAGE_DOCUMENT    = "android.provider.action.MANAGE_DOCUMENT";
    public static final String ACTION_BROWSE_DOCUMENT_TREE = "android.provider.action.BROWSE_DOCUMENT_TREE";

    public DocumentsContract() {}

    // -------------------------------------------------------------------------
    // Document column constants
    // -------------------------------------------------------------------------

    public static class Document {
        public static final String COLUMN_DOCUMENT_ID   = "document_id";
        public static final String COLUMN_MIME_TYPE     = "mime_type";
        public static final String COLUMN_DISPLAY_NAME  = "_display_name";
        public static final String COLUMN_SUMMARY       = "summary";
        public static final String COLUMN_LAST_MODIFIED = "last_modified";
        public static final String COLUMN_ICON          = "icon";
        public static final String COLUMN_FLAGS         = "flags";
        public static final String COLUMN_SIZE          = "_size";

        // Mime types
        public static final String MIME_TYPE_DIR = "vnd.android.document/directory";

        // Flag constants
        public static final int FLAG_SUPPORTS_WRITE         = 1;
        public static final int FLAG_SUPPORTS_DELETE        = 1 << 1;
        public static final int FLAG_SUPPORTS_THUMBNAIL     = 1 << 2;
        public static final int FLAG_DIR_PREFERS_GRID       = 1 << 3;
        public static final int FLAG_DIR_PREFERS_LAST_MODIFIED = 1 << 4;
        public static final int FLAG_SUPPORTS_RENAMED       = 1 << 5;
        public static final int FLAG_SUPPORTS_COPY          = 1 << 7;
        public static final int FLAG_SUPPORTS_MOVE          = 1 << 8;
        public static final int FLAG_SUPPORTS_REMOVE        = 1 << 9;
        public static final int FLAG_VIRTUAL_DOCUMENT       = 1 << 9;
        public static final int FLAG_SUPPORTS_SETTINGS      = 1 << 6;
        public static final int FLAG_WEB_LINKABLE           = 1 << 10;
        public static final int FLAG_PARTIAL                = 1 << 11;
        public static final int FLAG_DIR_BLOCKS_OPEN_DOCUMENT_TREE = 1 << 12;

        public Document() {}
    }

    // -------------------------------------------------------------------------
    // Root column constants
    // -------------------------------------------------------------------------

    public static class Root {
        public static final String COLUMN_ROOT_ID        = "root_id";
        public static final String COLUMN_FLAGS          = "flags";
        public static final String COLUMN_ICON           = "icon";
        public static final String COLUMN_TITLE          = "title";
        public static final String COLUMN_SUMMARY        = "summary";
        public static final String COLUMN_DOCUMENT_ID    = "document_id";
        public static final String COLUMN_AVAILABLE_BYTES = "available_bytes";
        public static final String COLUMN_CAPACITY_BYTES  = "capacity_bytes";
        public static final String COLUMN_MIME_TYPES      = "mime_types";
        public static final String COLUMN_QUERY_ARGS      = "query_args";
        public static final String COLUMN_ATTRIBUTION_URI = "attribution_uri";

        // Flag constants
        public static final int FLAG_LOCAL_ONLY         = 1 << 1;
        public static final int FLAG_SUPPORTS_CREATE    = 1 << 2;
        public static final int FLAG_SUPPORTS_RECENTS   = 1 << 3;
        public static final int FLAG_SUPPORTS_SEARCH    = 1 << 4;
        public static final int FLAG_ADVANCED           = 1 << 6;
        public static final int FLAG_SUPPORTS_IS_CHILD  = 1 << 7;
        public static final int FLAG_EMPTY              = 1 << 8;
        public static final int FLAG_SUPPORTS_EJECT     = 1 << 9;
        public static final int FLAG_REMOVABLE_SD       = 1 << 10;
        public static final int FLAG_REMOVABLE_USB      = 1 << 11;

        public Root() {}
    }

    // -------------------------------------------------------------------------
    // URI helper methods
    // -------------------------------------------------------------------------

    /**
     * Extract the {@link Document#COLUMN_DOCUMENT_ID} from a document URI.
     */
    public static String getDocumentId(Uri documentUri) {
        if (documentUri == null) return null;
        String path = documentUri.getPath();
        if (path == null) return null;
        // Pattern: /document/<id> or /tree/<treeId>/document/<id>
        String[] parts = splitByChar(path, '/');
        for (int i = 0; i < parts.length - 1; i++) {
            if ("document".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return null;
    }

    /**
     * Extract the tree document ID from a tree URI.
     */
    public static String getTreeDocumentId(Uri treeUri) {
        if (treeUri == null) return null;
        String path = treeUri.getPath();
        if (path == null) return null;
        String[] parts = splitByChar(path, '/');
        for (int i = 0; i < parts.length - 1; i++) {
            if ("tree".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return null;
    }

    /**
     * Build a URI representing the target {@link Document#COLUMN_DOCUMENT_ID}
     * in a document provider.
     */
    public static Uri buildDocumentUri(String authority, String documentId) {
        return Uri.parse("content://" + authority + "/document/" + documentId);
    }

    /**
     * Build a tree URI with the given document ID as the tree root.
     */
    public static Uri buildTreeDocumentUri(String authority, String documentId) {
        return Uri.parse("content://" + authority + "/tree/" + documentId);
    }

    /**
     * Build a document URI under a given tree URI.
     */
    public static Uri buildDocumentUriUsingTree(Uri treeUri, String documentId) {
        String treeId = getTreeDocumentId(treeUri);
        if (treeUri == null) return null;
        String authority = treeUri.getAuthority();
        return Uri.parse("content://" + authority + "/tree/" + treeId + "/document/" + documentId);
    }

    /**
     * Build a children URI for querying children of a document.
     */
    public static Uri buildChildDocumentsUri(String authority, String parentDocumentId) {
        return Uri.parse("content://" + authority + "/document/" + parentDocumentId + "/children");
    }

    /**
     * Build a children URI using a tree URI.
     */
    public static Uri buildChildDocumentsUriUsingTree(Uri treeUri, String parentDocumentId) {
        String authority = treeUri != null ? treeUri.getAuthority() : null;
        return Uri.parse("content://" + authority + "/tree/"
            + getTreeDocumentId(treeUri) + "/document/" + parentDocumentId + "/children");
    }

    /**
     * Return true if the given URI represents a document backed by
     * {@link android.provider.DocumentsProvider}.
     */
    public static boolean isDocumentUri(Object context, Uri uri) {
        if (uri == null) return false;
        String path = uri.getPath();
        return path != null && path.contains("/document/");
    }

    /**
     * Return true if the given URI is a tree URI (contains /tree/ segment).
     */
    public static boolean isTreeUri(Uri uri) {
        if (uri == null) return false;
        String path = uri.getPath();
        return path != null && path.startsWith("/tree/");
    }

    /**
     * Delete a document via the given URI. Stub — always returns false.
     */
    public static boolean deleteDocument(Object contentResolver, Uri documentUri) {
        return false;
    }

    /**
     * Rename a document. Stub — returns null.
     */
    public static Uri renameDocument(Object contentResolver, Uri documentUri, String displayName) {
        return null;
    }

    /**
     * Copy a document. Stub — returns null.
     */
    public static Uri copyDocument(Object contentResolver, Uri sourceDocumentUri, Uri targetParentDocumentUri) {
        return null;
    }

    /**
     * Move a document. Stub — returns null.
     */
    public static Uri moveDocument(Object contentResolver, Uri sourceDocumentUri,
            Uri sourceParentDocumentUri, Uri targetParentDocumentUri) {
        return null;
    }

    /**
     * Remove a document from a parent. Stub — always returns false.
     */
    private static String[] splitByChar(String s, char delim) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        int start = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == delim) {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        return parts.toArray(new String[0]);
    }

    public static boolean removeDocument(Object contentResolver, Uri documentUri, Uri parentDocumentUri) {
        return false;
    }

    /**
     * Create a new document with a given MIME type and display name. Stub — returns null.
     */
    public static Uri createDocument(Object contentResolver, Uri parentDocumentUri,
            String mimeType, String displayName) {
        return null;
    }
}
