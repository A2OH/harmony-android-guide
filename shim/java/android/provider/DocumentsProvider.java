package android.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import java.io.FileNotFoundException;

/**
 * Android-compatible DocumentsProvider shim.
 *
 * OH mapping: FileAccessExtensionAbility / DataShareExtensionAbility
 * Subclass and implement the four abstract document-access methods.
 * The optional mutation methods (createDocument, deleteDocument, etc.)
 * throw UnsupportedOperationException by default.
 */
public abstract class DocumentsProvider extends ContentProvider {

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    @Override
    public boolean onCreate() {
        return true;
    }

    // ── Abstract document methods ──────────────────────────────────────────────

    /**
     * Return all roots provided by this documents provider.
     * Each row must include at least {@code Root.COLUMN_ROOT_ID} and
     * {@code Root.COLUMN_FLAGS}.
     */
    public abstract Cursor queryRoots(String[] projection) throws FileNotFoundException;

    /**
     * Return metadata for a single document, identified by {@code documentId}.
     */
    public abstract Cursor queryDocument(String documentId, String[] projection)
            throws FileNotFoundException;

    /**
     * Return the children of the given directory document.
     */
    public abstract Cursor queryChildDocuments(String parentDocumentId, String[] projection,
            String sortOrder) throws FileNotFoundException;

    /**
     * Open a document for reading or writing.
     * Return an {@code InputStream} or {@code OutputStream} wrapped as an Object
     * (caller casts as needed).
     */
    public abstract Object openDocument(String documentId, String mode,
            Object cancellationSignal) throws FileNotFoundException;

    // ── Optional document mutation methods ────────────────────────────────────

    /**
     * Create a new document in the given directory.
     * @return the documentId of the newly created document
     */
    public String createDocument(String parentDocumentId, String mimeType,
            String displayName) throws FileNotFoundException {
        throw new UnsupportedOperationException("createDocument not supported");
    }

    /**
     * Delete the given document. The default implementation throws
     * UnsupportedOperationException.
     */
    public void deleteDocument(String documentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("deleteDocument not supported");
    }

    /**
     * Rename the given document.
     * @return the new documentId (may differ from original if the provider changed it)
     */
    public String renameDocument(String documentId, String displayName)
            throws FileNotFoundException {
        throw new UnsupportedOperationException("renameDocument not supported");
    }

    /**
     * Copy the given document under the given destination.
     * @return the documentId of the newly created copy
     */
    public String copyDocument(String sourceDocumentId, String targetParentDocumentId)
            throws FileNotFoundException {
        throw new UnsupportedOperationException("copyDocument not supported");
    }

    /**
     * Move the given document under the new destination.
     * @return the documentId of the document at its new location
     */
    public String moveDocument(String sourceDocumentId, String sourceParentDocumentId,
            String targetParentDocumentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("moveDocument not supported");
    }

    // ── ContentProvider delegation (overridden from ContentProvider) ───────────

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // DocumentsProvider routes through queryDocument / queryChildDocuments
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("insert not supported by DocumentsProvider");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("update not supported by DocumentsProvider");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("delete not supported by DocumentsProvider");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
