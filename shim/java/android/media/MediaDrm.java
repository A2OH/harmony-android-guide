package android.media;

import java.util.UUID;

/**
 * Android-compatible MediaDrm shim. Stub for DRM key management.
 */
public class MediaDrm {

    public static final int EVENT_KEY_REQUIRED = 1;
    public static final int EVENT_KEY_EXPIRED = 2;
    public static final int EVENT_VENDOR_DEFINED = 3;
    public static final int EVENT_SESSION_RECLAIMED = 4;

    public static final int KEY_TYPE_STREAMING = 1;
    public static final int KEY_TYPE_OFFLINE = 2;
    public static final int KEY_TYPE_RELEASE = 3;

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    public static final class KeyRequest {
        private final byte[] mData;
        private final String mDefaultUrl;
        private final int mRequestType;

        public KeyRequest(byte[] data, String defaultUrl, int requestType) {
            mData = data != null ? data : new byte[0];
            mDefaultUrl = defaultUrl != null ? defaultUrl : "";
            mRequestType = requestType;
        }

        public byte[] getData() {
            return mData;
        }

        public String getDefaultUrl() {
            return mDefaultUrl;
        }

        public int getRequestType() {
            return mRequestType;
        }
    }

    public static final class ProvisionRequest {
        private final byte[] mData;
        private final String mDefaultUrl;

        public ProvisionRequest(byte[] data, String defaultUrl) {
            mData = data != null ? data : new byte[0];
            mDefaultUrl = defaultUrl != null ? defaultUrl : "";
        }

        public byte[] getData() {
            return mData;
        }

        public String getDefaultUrl() {
            return mDefaultUrl;
        }
    }

    public interface OnEventListener {
        void onEvent(MediaDrm md, byte[] sessionId, int event, int extra, byte[] data);
    }

    public interface OnKeyStatusChangeListener {
        void onKeyStatusChange(MediaDrm md, byte[] sessionId, Object keyInformation, boolean hasNewUsableKey);
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private final UUID mSchemeUUID;
    private OnEventListener mEventListener;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public MediaDrm(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid must not be null");
        }
        mSchemeUUID = uuid;
    }

    // -------------------------------------------------------------------------
    // Static helpers
    // -------------------------------------------------------------------------

    public static boolean isCryptoSchemeSupported(UUID uuid) {
        return false;
    }

    public static boolean isCryptoSchemeSupported(UUID uuid, String mimeType) {
        return false;
    }

    // -------------------------------------------------------------------------
    // Session management
    // -------------------------------------------------------------------------

    public byte[] openSession() {
        return new byte[16];
    }

    public void closeSession(byte[] sessionId) {}

    // -------------------------------------------------------------------------
    // Key management
    // -------------------------------------------------------------------------

    public KeyRequest getKeyRequest(byte[] scope, byte[] init, String mimeType,
            int keyType, java.util.HashMap<String, String> optionalParameters) {
        return new KeyRequest(new byte[0], "", keyType);
    }

    public byte[] provideKeyResponse(byte[] scope, byte[] response) {
        return new byte[0];
    }

    // -------------------------------------------------------------------------
    // Provisioning
    // -------------------------------------------------------------------------

    public ProvisionRequest getProvisionRequest() {
        return new ProvisionRequest(new byte[0], "");
    }

    public void provideProvisionResponse(byte[] response) {}

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    public String getPropertyString(String propertyName) {
        return "";
    }

    public void setPropertyString(String propertyName, String value) {}

    public byte[] getPropertyByteArray(String propertyName) {
        return new byte[0];
    }

    public void setPropertyByteArray(String propertyName, byte[] value) {}

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    public void setOnEventListener(OnEventListener listener) {
        mEventListener = listener;
    }

    public void setOnKeyStatusChangeListener(OnKeyStatusChangeListener listener) {}

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    public void release() {}
}
