package android.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible MediaRouter2 shim (API 30+).
 * Stub for media routing with route discovery and transfer support.
 */
public class MediaRouter2 {

    private MediaRouter2() {}

    /**
     * Returns a MediaRouter2 instance for the given context.
     */
    public static MediaRouter2 getInstance(Object context) {
        return new MediaRouter2();
    }

    /**
     * Returns the currently known routes. Stub returns empty list.
     */
    public List<Object> getRoutes() {
        return new ArrayList<>();
    }

    /**
     * Returns the current controllers. Stub returns empty list.
     */
    public List<Object> getControllers() {
        return new ArrayList<>();
    }

    /**
     * Sets the volume of the given route. No-op stub.
     */
    public void setRouteVolume(Object route, int volume) {
        // no-op
    }

    /**
     * Transfers playback to the given route. No-op stub.
     */
    public void transferTo(Object route) {
        // no-op
    }

    /**
     * Stops routing. No-op stub.
     */
    public void stop() {
        // no-op
    }

    // -----------------------------------------------------------------------
    // Object registration (all no-ops)
    // -----------------------------------------------------------------------

    public void registerRouteCallback(Object executor, RouteCallback callback) {
        // no-op
    }

    public void unregisterRouteCallback(RouteCallback callback) {
        // no-op
    }

    public void registerTransferCallback(Object executor, TransferCallback callback) {
        // no-op
    }

    public void unregisterTransferCallback(TransferCallback callback) {
        // no-op
    }

    // -----------------------------------------------------------------------
    // Inner interfaces
    // -----------------------------------------------------------------------

    /** Object for route availability changes. */
    public interface RouteCallback {
    }

    /** Object for route transfer events. */
    public interface TransferCallback {
    }
}
