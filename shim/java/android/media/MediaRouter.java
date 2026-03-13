package android.media;

public class MediaRouter {
    public MediaRouter() {}

    public static final int CALLBACK_FLAG_PERFORM_ACTIVE_SCAN = 0;
    public static final int CALLBACK_FLAG_UNFILTERED_EVENTS = 0;
    public static final int ROUTE_TYPE_LIVE_AUDIO = 0;
    public static final int ROUTE_TYPE_LIVE_VIDEO = 0;
    public static final int ROUTE_TYPE_USER = 0;
    public void addCallback(Object p0, Object p1) {}
    public void addCallback(Object p0, Object p1, Object p2) {}
    public void addUserRoute(Object p0) {}
    public void clearUserRoutes() {}
    public Object createRouteCategory(Object p0, Object p1) { return null; }
    public Object createUserRoute(Object p0) { return null; }
    public Object getCategoryAt(Object p0) { return null; }
    public int getCategoryCount() { return 0; }
    public Object getDefaultRoute() { return null; }
    public Object getRouteAt(Object p0) { return null; }
    public int getRouteCount() { return 0; }
    public Object getSelectedRoute(Object p0) { return null; }
    public void removeCallback(Object p0) {}
    public void removeUserRoute(Object p0) {}
    public void selectRoute(Object p0, Object p1) {}
    public void onRouteAdded(Object p0, Object p1) {}
    public void onRouteChanged(Object p0, Object p1) {}
    public void onRouteGrouped(Object p0, Object p1, Object p2, Object p3) {}
    public void onRoutePresentationDisplayChanged(Object p0, Object p1) {}
    public void onRouteRemoved(Object p0, Object p1) {}
    public void onRouteSelected(Object p0, Object p1, Object p2) {}
    public void onRouteUngrouped(Object p0, Object p1, Object p2) {}
    public void onRouteUnselected(Object p0, Object p1, Object p2) {}
    public void onRouteVolumeChanged(Object p0, Object p1) {}
}
