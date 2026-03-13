package android.media;

/**
 * Android-compatible MediaSession2 shim. Stub for media session management (API 29+).
 */
public class MediaSession2 {

    private final String mId;

    private MediaSession2(String id) {
        mId = id;
    }

    /**
     * Returns the session ID provided at construction time.
     */
    public String getId() { return mId; }

    /**
     * Returns an opaque token that can be used to create a controller for this session.
     */
    public Object getToken() { return null; }

    /**
     * Releases this session and its resources.
     */
    public void close() {
        // no-op
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    /**
     * Builder for MediaSession2.
     */
    public static class Builder {
        private String mId = "";
        private Object mCallback;

        public Builder() {}

        /**
         * Sets the session ID that will be returned by {@link MediaSession2#getId()}.
         */
        public Builder setId(String id) {
            mId = id != null ? id : "";
            return this;
        }

        /**
         * Sets the session callback that handles controller commands.
         */
        public Builder setSessionCallback(Object callback) {
            mCallback = callback;
            return this;
        }

        /**
         * Builds and returns a new {@link MediaSession2}.
         */
        public MediaSession2 build() {
            return new MediaSession2(mId);
        }
    }

    /**
     * Abstract callback class for handling commands sent by a connected controller.
     */
    public abstract static class Object {

        /**
         * Called when a controller connects to the session.
         *
         * @param session    the session receiving the connection
         * @param controller the controller that is connecting
         * @return a non-null result to allow the connection, or null to reject it
         */
        public Object onConnect(MediaSession2 session, ControllerInfo controller) {
            return null;
        }

        /**
         * Called when a controller disconnects from the session.
         */
        public void onDisconnected(MediaSession2 session, ControllerInfo controller) {
            // no-op
        }
    }

    /**
     * Describes a controller that is connected to (or attempting to connect to) the session.
     */
    public static class ControllerInfo {
        private final String mPackageName;
        private final int mUid;

        public ControllerInfo() {
            mPackageName = "";
            mUid = -1;
        }

        /**
         * Returns the package name of the connecting application.
         */
        public String getPackageName() { return mPackageName; }

        /**
         * Returns the UID of the connecting application.
         */
        public int getUid() { return mUid; }
    }
}
