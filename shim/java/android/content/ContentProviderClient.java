package android.content;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;

public class ContentProviderClient implements AutoCloseable {
    public ContentProviderClient() {}

    public int bulkInsert(Uri p0, ContentValues[] p1) { return 0; }
    public void close() {}
    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public int delete(Uri p0, Bundle p1) { return 0; }
    public boolean refresh(Uri p0, Bundle p1, CancellationSignal p2) { return false; }
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
    public int update(Uri p0, ContentValues p1, Bundle p2) { return 0; }
}
