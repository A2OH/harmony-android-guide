package android.app.slice;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import java.util.Set;

public class SliceProvider extends ContentProvider {
    public static final int SLICE_TYPE = 0;

    public SliceProvider(String... p0) {}
    public SliceProvider() {}

    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public String getType(Uri p0) { return null; }
    public Uri insert(Uri p0, ContentValues p1) { return null; }
    public Slice onBindSlice(Uri p0, java.util.Set<Object> p1) { return null; }
    public void onSlicePinned(Uri p0) {}
    public void onSliceUnpinned(Uri p0) {}
    public Cursor query(Uri p0, String[] p1, String p2, String[] p3, String p4) { return null; }
    public Cursor query(Uri p0, String[] p1, String p2, String[] p3, String p4, CancellationSignal p5) { return null; }
    public Cursor query(Uri p0, String[] p1, Bundle p2, CancellationSignal p3) { return null; }
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
}
