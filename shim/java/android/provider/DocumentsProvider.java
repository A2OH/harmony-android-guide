package android.provider;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.IntentSender;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;

public class DocumentsProvider extends ContentProvider {
    public DocumentsProvider() {}

    public String copyDocument(String p0, String p1) { return null; }
    public String createDocument(String p0, String p1, String p2) { return null; }
    public IntentSender createWebLinkIntent(String p0, Bundle p1) { return null; }
    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public void deleteDocument(String p0) {}
    public void ejectRoot(String p0) {}
    public Path findDocumentPath(String p0, String p1) { return null; }
    public String[] getDocumentStreamTypes(String p0, String p1) { return null; }
    public String getDocumentType(String p0) { return null; }
    public String getType(Uri p0) { return null; }
    public Uri insert(Uri p0, ContentValues p1) { return null; }
    public boolean isChildDocument(String p0, String p1) { return false; }
    public String moveDocument(String p0, String p1, String p2) { return null; }
    public AssetFileDescriptor openAssetFile(Uri p0, String p1) { return null; }
    public AssetFileDescriptor openAssetFile(Uri p0, String p1, CancellationSignal p2) { return null; }
    public ParcelFileDescriptor openDocument(String p0, String p1, CancellationSignal p2) { return null; }
    public AssetFileDescriptor openDocumentThumbnail(String p0, Point p1, CancellationSignal p2) { return null; }
    public ParcelFileDescriptor openFile(Uri p0, String p1) { return null; }
    public ParcelFileDescriptor openFile(Uri p0, String p1, CancellationSignal p2) { return null; }
    public AssetFileDescriptor openTypedAssetFile(Uri p0, String p1, Bundle p2) { return null; }
    public AssetFileDescriptor openTypedAssetFile(Uri p0, String p1, Bundle p2, CancellationSignal p3) { return null; }
    public AssetFileDescriptor openTypedDocument(String p0, String p1, Bundle p2, CancellationSignal p3) { return null; }
    public Cursor query(Uri p0, String[] p1, String p2, String[] p3, String p4) { return null; }
    public Cursor query(Uri p0, String[] p1, Bundle p2, CancellationSignal p3) { return null; }
    public Cursor queryChildDocuments(String p0, String[] p1, String p2) { return null; }
    public Cursor queryDocument(String p0, String[] p1) { return null; }
    public Cursor queryRecentDocuments(String p0, String[] p1) { return null; }
    public Cursor queryRoots(String[] p0) { return null; }
    public Cursor querySearchDocuments(String p0, String p1, String[] p2) { return null; }
    public void removeDocument(String p0, String p1) {}
    public String renameDocument(String p0, String p1) { return null; }
    public void revokeDocumentPermission(String p0) {}
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
}
