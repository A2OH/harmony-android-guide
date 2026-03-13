package android.content;
import android.net.Uri;
import android.net.Uri;
import java.net.URI;

import android.net.Uri;

/**
 * Android-compatible ContentProviderResult shim.
 * Contains either a URI (for inserts) or a count (for updates/deletes).
 */
public class ContentProviderResult {

    public final Uri uri;
    public final Integer count;

    public ContentProviderResult(Uri uri) {
        this.uri = uri;
        this.count = null;
    }

    public ContentProviderResult(int count) {
        this.uri = null;
        this.count = count;
    }

    @Override
    public String toString() {
        if (uri != null) {
            return "ContentProviderResult(uri=" + uri + ")";
        }
        return "ContentProviderResult(count=" + count + ")";
    }
}
