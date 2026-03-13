package android.location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Android-compatible Geocoder shim. Stub — returns empty results.
 * Real implementation would use OH location service.
 */
public class Geocoder {
    private final java.util.Locale mLocale;

    public Geocoder(Object context) {
        mLocale = java.util.Locale.getDefault();
    }

    public Geocoder(Object context, java.util.Locale locale) {
        mLocale = locale;
    }

    public static boolean isPresent() {
        return false; // no geocoder service in mock
    }

    public List<Address> getFromLocation(double latitude, double longitude, int maxResults)
            throws IOException {
        return new ArrayList<>();
    }

    public List<Address> getFromLocationName(String locationName, int maxResults)
            throws IOException {
        return new ArrayList<>();
    }

    public List<Address> getFromLocationName(String locationName, int maxResults,
            double lowerLeftLatitude, double lowerLeftLongitude,
            double upperRightLatitude, double upperRightLongitude) throws IOException {
        return new ArrayList<>();
    }
}
