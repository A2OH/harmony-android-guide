package android.drm;
import android.content.ContentValues;
import android.content.ContentValues;

/**
 * Android-compatible DrmManagerClient shim. Stub — no-op implementation.
 */
public class DrmManagerClient {
    private final Object mContext;

    public DrmManagerClient(Object context) {
        mContext = context;
    }

    public int acquireRights(Object request) {
        return 0; // stub
    }

    public android.content.ContentValues getConstraints(String path, int action) {
        return new android.content.ContentValues(); // stub
    }

    public boolean canHandle(String path, String mimeType) {
        return false; // stub
    }

    public String[] getAvailableDrmEngines() {
        return new String[0]; // stub
    }

    public void close() {
        // stub
    }

}
