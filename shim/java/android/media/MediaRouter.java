package android.media;

/**
 * Android-compatible MediaRouter shim. Stub for media route selection.
 */
public class MediaRouter {
    public static final int ROUTE_TYPE_LIVE_AUDIO      = 1;
    public static final int ROUTE_TYPE_LIVE_VIDEO      = 2;
    public static final int ROUTE_TYPE_REMOTE_DISPLAY  = 4;

    private static MediaRouter sInstance;

    private MediaRouter() {}

    public static MediaRouter getInstance(Object context) {
        if (sInstance == null) {
            sInstance = new MediaRouter();
        }
        return sInstance;
    }

    public RouteInfo getSelectedRoute() {
        return new RouteInfo();
    }

    public void addCallback(int types, Callback cb) {
        // no-op
    }

    public void removeCallback(Callback cb) {
        // no-op
    }

    public void selectRoute(int types, RouteInfo route) {
        // no-op
    }

    // -----------------------------------------------------------------------
    // RouteInfo inner class
    // -----------------------------------------------------------------------

    public static class RouteInfo {
        private String mName = "Default Route";
        private boolean mEnabled = true;

        public String getName() { return mName; }
        public boolean isEnabled() { return mEnabled; }
        public boolean isDefault() { return true; }

        public int getPlaybackType() { return 0; }
        public int getVolumeMax() { return 100; }
        public int getVolume() { return 50; }
        public void requestSetVolume(int volume) {}
        public void requestUpdateVolume(int direction) {}
    }

    // -----------------------------------------------------------------------
    // UserRouteInfo inner class
    // -----------------------------------------------------------------------

    public static class UserRouteInfo extends RouteInfo {
        private String mTag;

        public void setName(String name) {}
        public void setStatus(CharSequence status) {}
        public void setTag(Object tag) { mTag = tag != null ? tag.toString() : null; }
        public Object getTag() { return mTag; }
        public void setVolume(int volume) {}
        public void setVolumeMax(int max) {}
        public void setVolumeCallback(Object cb) {}
        public void setRemotePlaybackInfo(String statusText, int iconRes, Object presentationDisplay) {}
    }

    // -----------------------------------------------------------------------
    // Callback abstract inner class
    // -----------------------------------------------------------------------

    public static abstract class Callback {
        public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {}
        public void onRouteUnselected(MediaRouter router, int type, RouteInfo info) {}
        public void onRouteAdded(MediaRouter router, RouteInfo info) {}
        public void onRouteRemoved(MediaRouter router, RouteInfo info) {}
        public void onRouteChanged(MediaRouter router, RouteInfo info) {}
        public void onRouteGrouped(MediaRouter router, RouteInfo info, RouteInfo group, int index) {}
        public void onRouteUngrouped(MediaRouter router, RouteInfo info, RouteInfo group) {}
        public void onRouteVolumeChanged(MediaRouter router, RouteInfo info) {}
        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {}
    }
}
