package android.app.slice;
import android.net.Uri;
import android.net.Uri;
import java.util.Set;

public class SliceManager {
    public static final int CATEGORY_SLICE = 0;
    public static final int SLICE_METADATA_KEY = 0;

    public SliceManager() {}

    public int checkSlicePermission(Uri p0, int p1, int p2) { return 0; }
    public void grantSlicePermission(String p0, Uri p1) {}
    public void pinSlice(Uri p0, java.util.Set<Object> p1) {}
    public void revokeSlicePermission(String p0, Uri p1) {}
    public void unpinSlice(Uri p0) {}
}
