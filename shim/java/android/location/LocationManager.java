package android.location;

import com.ohos.shim.bridge.OHBridge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible LocationManager shim.
 * Maps to @ohos.geoLocationManager on OpenHarmony.
 *
 * Location data is retrieved via OHBridge native methods:
 *   OHBridge.locationGetLast()   -- [lat, lon, alt] or null
 *   OHBridge.locationIsEnabled() -- boolean
 *
 * Supports GPS, Network, and Passive providers.
 */
public class LocationManager {

    public static final String GPS_PROVIDER     = "gps";
    public static final String NETWORK_PROVIDER = "network";
    public static final String PASSIVE_PROVIDER = "passive";

    // -- Listener registry -------------------------------------------------
    // Maps listener -> provider name so removeUpdates() can clean up per-listener.
    private final Map<LocationListener, String> mListeners =
            new LinkedHashMap<>();

    // -- Core API ----------------------------------------------------------

    /**
     * Register a listener for periodic location updates.
     *
     * @param provider    GPS_PROVIDER / NETWORK_PROVIDER / PASSIVE_PROVIDER
     * @param minTime     minimum interval in milliseconds (hint only)
     * @param minDistance minimum displacement in metres (hint only)
     * @param listener    callback to receive Location objects
     */
    public void requestLocationUpdates(String provider,
                                       long minTime,
                                       float minDistance,
                                       LocationListener listener) {
        if (listener == null || provider == null) return;
        mListeners.put(listener, provider);
    }

    /**
     * Remove all pending updates for the given listener.
     */
    public void removeUpdates(LocationListener listener) {
        if (listener == null) return;
        mListeners.remove(listener);
    }

    /**
     * Returns the last known location for the given provider, or null.
     * Queries OHBridge.locationGetLast() which wraps @ohos.geoLocationManager.
     */
    public Location getLastKnownLocation(String provider) {
        double[] coords = OHBridge.locationGetLast();
        if (coords == null || coords.length < 2) return null;
        Location loc = new Location(provider != null ? provider : GPS_PROVIDER);
        loc.setLatitude(coords[0]);
        loc.setLongitude(coords[1]);
        if (coords.length >= 3) {
            loc.setAltitude(coords[2]);
        }
        loc.setTime(System.currentTimeMillis());
        return loc;
    }

    /**
     * Returns true if the given provider is currently enabled.
     * Delegates to OHBridge.locationIsEnabled().
     */
    public boolean isProviderEnabled(String provider) {
        return OHBridge.locationIsEnabled();
    }

    /**
     * Returns all known providers. If enabledOnly is true, only returns those
     * that are currently enabled.
     */
    public List<String> getProviders(boolean enabledOnly) {
        List<String> all = Arrays.asList(GPS_PROVIDER, NETWORK_PROVIDER, PASSIVE_PROVIDER);
        if (!enabledOnly) return new ArrayList<>(all);
        if (OHBridge.locationIsEnabled()) return new ArrayList<>(all);
        return new ArrayList<>();
    }

    /**
     * Returns the name of the best provider that meets the given Criteria.
     * Falls back to GPS_PROVIDER when the criteria are null or no match.
     */
    public String getBestProvider(Criteria criteria, boolean enabledOnly) {
        if (enabledOnly && !OHBridge.locationIsEnabled()) return null;
        if (criteria != null && criteria.getAccuracy() == Criteria.ACCURACY_COARSE) {
            return NETWORK_PROVIDER;
        }
        return GPS_PROVIDER;
    }

    // -- GPS status (stub) ------------------------------------------------

    /** No-op stub -- GPS status events are not supported in this shim. */
    public void addGpsStatusListener(Object listener) {
        // no-op: @ohos.geoLocationManager does not expose a direct GpsStatus API
    }

    /** No-op stub. */
    public void removeGpsStatusListener(Object listener) {
        // no-op
    }

    // -- Helpers ----------------------------------------------------------

    /** Returns the number of currently registered listeners (for testing). */
    public int getListenerCount() {
        return mListeners.size();
    }

    /** Returns true if the given listener is currently registered. */
    public boolean hasListener(LocationListener listener) {
        return mListeners.containsKey(listener);
    }
}
