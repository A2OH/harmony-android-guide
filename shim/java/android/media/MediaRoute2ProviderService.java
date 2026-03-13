package android.media;
import android.app.Service;
import android.os.Bundle;
import java.util.Collection;

public class MediaRoute2ProviderService extends Service {
    public static final int REASON_INVALID_COMMAND = 0;
    public static final int REASON_NETWORK_ERROR = 0;
    public static final int REASON_REJECTED = 0;
    public static final int REASON_ROUTE_NOT_AVAILABLE = 0;
    public static final int REASON_UNKNOWN_ERROR = 0;
    public static final int REQUEST_ID_NONE = 0;
    public static final int SERVICE_INTERFACE = 0;

    public MediaRoute2ProviderService() {}

    public void notifyRequestFailed(long p0, int p1) {}
    public void notifyRoutes(Object p0) {}
    public void notifySessionCreated(long p0, RoutingSessionInfo p1) {}
    public void notifySessionReleased(String p0) {}
    public void notifySessionUpdated(RoutingSessionInfo p0) {}
    public void onCreateSession(long p0, String p1, String p2, Bundle p3) {}
    public void onDeselectRoute(long p0, String p1, String p2) {}
    public void onDiscoveryPreferenceChanged(RouteDiscoveryPreference p0) {}
    public void onReleaseSession(long p0, String p1) {}
    public void onSelectRoute(long p0, String p1, String p2) {}
    public void onSetRouteVolume(long p0, String p1, int p2) {}
    public void onSetSessionVolume(long p0, String p1, int p2) {}
    public void onTransferToRoute(long p0, String p1, String p2) {}
}
