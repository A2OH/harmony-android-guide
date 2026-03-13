package android.content;
import android.os.Bundle;
import android.os.PersistableBundle;
import java.util.List;

public class RestrictionsManager {
    public static final int ACTION_PERMISSION_RESPONSE_RECEIVED = 0;
    public static final int ACTION_REQUEST_LOCAL_APPROVAL = 0;
    public static final int ACTION_REQUEST_PERMISSION = 0;
    public static final int EXTRA_PACKAGE_NAME = 0;
    public static final int EXTRA_REQUEST_BUNDLE = 0;
    public static final int EXTRA_REQUEST_ID = 0;
    public static final int EXTRA_REQUEST_TYPE = 0;
    public static final int EXTRA_RESPONSE_BUNDLE = 0;
    public static final int META_DATA_APP_RESTRICTIONS = 0;
    public static final int REQUEST_KEY_APPROVE_LABEL = 0;
    public static final int REQUEST_KEY_DATA = 0;
    public static final int REQUEST_KEY_DENY_LABEL = 0;
    public static final int REQUEST_KEY_ICON = 0;
    public static final int REQUEST_KEY_ID = 0;
    public static final int REQUEST_KEY_MESSAGE = 0;
    public static final int REQUEST_KEY_NEW_REQUEST = 0;
    public static final int REQUEST_KEY_TITLE = 0;
    public static final int REQUEST_TYPE_APPROVAL = 0;
    public static final int RESPONSE_KEY_ERROR_CODE = 0;
    public static final int RESPONSE_KEY_MESSAGE = 0;
    public static final int RESPONSE_KEY_RESPONSE_TIMESTAMP = 0;
    public static final int RESPONSE_KEY_RESULT = 0;
    public static final int RESULT_APPROVED = 0;
    public static final int RESULT_DENIED = 0;
    public static final int RESULT_ERROR = 0;
    public static final int RESULT_ERROR_BAD_REQUEST = 0;
    public static final int RESULT_ERROR_INTERNAL = 0;
    public static final int RESULT_ERROR_NETWORK = 0;
    public static final int RESULT_NO_RESPONSE = 0;
    public static final int RESULT_UNKNOWN_REQUEST = 0;

    public RestrictionsManager() {}

    public static Bundle convertRestrictionsToBundle(java.util.List<Object> p0) { return null; }
    public Intent createLocalApprovalIntent() { return null; }
    public Bundle getApplicationRestrictions() { return null; }
    public List<?> getManifestRestrictions(String p0) { return null; }
    public boolean hasRestrictionsProvider() { return false; }
    public void notifyPermissionResponse(String p0, PersistableBundle p1) {}
    public void requestPermission(String p0, String p1, PersistableBundle p2) {}
}
