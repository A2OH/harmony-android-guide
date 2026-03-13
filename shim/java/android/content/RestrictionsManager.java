package android.content;

/**
 * Android-compatible RestrictionsManager shim. Stub — no-op implementation for A2OH migration.
 */
public class RestrictionsManager {

    /**
     * Broadcast intent delivered when a managed app requests a permission.
     */
    public static final String ACTION_REQUEST_PERMISSION =
            "android.content.action.REQUEST_PERMISSION";

    public RestrictionsManager() {}

    /**
     * Returns any available set of application-specific restrictions applicable
     * to this application. Always returns null in this stub.
     */
    public Object getApplicationRestrictions() {
        return null; // stub — real impl returns a Bundle
    }

    /**
     * Called by an application to check if there is an active restrictions provider.
     * @return false (stub)
     */
    public boolean hasRestrictionsProvider() {
        return false; // stub
    }

    /**
     * Called by an application to request permission for an operation.
     * No-op stub.
     *
     * @param requestType  The type of restriction being requested.
     * @param requestId    A unique identifier for this request.
     * @param request      A Bundle containing the request details (typed as Object).
     */
    public void requestPermission(String requestType, String requestId, Object request) {
        // stub — no-op
    }
}
