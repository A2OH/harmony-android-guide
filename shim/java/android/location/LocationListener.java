package android.location;

public interface LocationListener {
    void onLocationChanged(Location p0);
    void onProviderDisabled(String p0);
    void onProviderEnabled(String p0);
}
