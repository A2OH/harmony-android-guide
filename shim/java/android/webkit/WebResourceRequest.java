package android.webkit;
import android.net.Uri;
import android.net.Uri;

import android.net.Uri;
import java.util.Collections;
import java.util.Map;

/**
 * Android-compatible WebResourceRequest stub.
 */
public interface WebResourceRequest {
    default Uri getUrl() { return null; }
    default boolean isForMainFrame() { return true; }
    default boolean isRedirect() { return false; }
    default boolean hasGesture() { return false; }
    default String getMethod() { return "GET"; }
    default Map<String, String> getRequestHeaders() { return Collections.emptyMap(); }
}
