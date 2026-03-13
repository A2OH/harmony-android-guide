package android.media.browse;
import android.media.MediaDescription;
import android.media.MediaDescription;
import java.util.ArrayList;

import java.util.List;

/**
 * Android-compatible MediaBrowser shim. Stub for media browser service connection.
 */
public class MediaBrowser {
    public static final int FLAG_BROWSABLE = 1;
    public static final int FLAG_PLAYABLE  = 2;

    private final Object mContext;
    private final Object mServiceComponent;
    private final ConnectionCallback mCallback;
    private final Object mRootHints;
    private boolean mConnected;

    public MediaBrowser(Object context, Object serviceComponent,
                        ConnectionCallback callback, Object rootHints) {
        mContext = context;
        mServiceComponent = serviceComponent;
        mCallback = callback;
        mRootHints = rootHints;
    }

    public void connect() {
        mConnected = true;
        if (mCallback != null) mCallback.onConnected();
    }

    public void disconnect() {
        mConnected = false;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public String getRoot() {
        return "/";
    }

    public Object getSessionToken() {
        return null;
    }

    public void subscribe(String parentId, SubscriptionCallback callback) {
        if (callback != null) callback.onChildrenLoaded(parentId, new java.util.ArrayList<MediaItem>());
    }

    public void unsubscribe(String parentId) {
        // no-op
    }

    // -----------------------------------------------------------------------
    // Abstract inner classes
    // -----------------------------------------------------------------------

    public static abstract class ConnectionCallback {
        public void onConnected() {}
        public void onConnectionSuspended() {}
        public void onConnectionFailed() {}
    }

    public static abstract class SubscriptionCallback {
        public void onChildrenLoaded(String parentId, List<MediaItem> children) {}
        public void onError(String parentId) {}
    }

    // -----------------------------------------------------------------------
    // MediaItem inner class
    // -----------------------------------------------------------------------

    public static class MediaItem {
        private final android.media.MediaDescription mDescription;
        private final int mFlags;

        public MediaItem(android.media.MediaDescription description, int flags) {
            mDescription = description;
            mFlags = flags;
        }

        public String getMediaId() {
            return mDescription != null ? mDescription.getMediaId() : null;
        }

        public android.media.MediaDescription getDescription() {
            return mDescription;
        }

        public boolean isBrowsable() {
            return (mFlags & FLAG_BROWSABLE) != 0;
        }

        public boolean isPlayable() {
            return (mFlags & FLAG_PLAYABLE) != 0;
        }
    }
}
