package android.view.contentcapture;

import java.util.Collection;
import java.util.Collections;

/**
 * Android-compatible ContentCaptureManager shim.
 * Provides access to the content capture service.
 */
public final class ContentCaptureManager {

    public boolean isContentCaptureEnabled() {
        return false;
    }

    /**
     * Returns the set of conditions under which content capture runs.
     * Returns an empty collection on OpenHarmony (service not present).
     */
    public Collection<Object> getContentCaptureConditions() {
        return Collections.emptyList();
    }

    /**
     * Removes data maintained by the content capture service.
     *
     * @param request an Object representing the data removal request (DataRemovalRequest on Android)
     */
    public void removeData(Object request) {
        // stub — content capture not supported on OpenHarmony
    }

    public ContentCaptureSession getMainContentCaptureSession() { return null; }
}
