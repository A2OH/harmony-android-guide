package android.location;
import android.app.PendingIntent;
import android.os.Bundle;

public class LocationManager {
    public static final int EXTRA_LOCATION_ENABLED = 0;
    public static final int EXTRA_PROVIDER_ENABLED = 0;
    public static final int EXTRA_PROVIDER_NAME = 0;
    public static final int GPS_PROVIDER = 0;
    public static final int KEY_LOCATION_CHANGED = 0;
    public static final int KEY_PROVIDER_ENABLED = 0;
    public static final int KEY_PROXIMITY_ENTERING = 0;
    public static final int MODE_CHANGED_ACTION = 0;
    public static final int NETWORK_PROVIDER = 0;
    public static final int PASSIVE_PROVIDER = 0;
    public static final int PROVIDERS_CHANGED_ACTION = 0;

    public LocationManager() {}

    public void addTestProvider(String p0, boolean p1, boolean p2, boolean p3, boolean p4, boolean p5, boolean p6, boolean p7, int p8, int p9) {}
    public int getGnssYearOfHardware() { return 0; }
    public boolean isLocationEnabled() { return false; }
    public boolean isProviderEnabled(String p0) { return false; }
    public void removeNmeaListener(OnNmeaMessageListener p0) {}
    public void removeTestProvider(String p0) {}
    public void removeUpdates(PendingIntent p0) {}
    public boolean sendExtraCommand(String p0, String p1, Bundle p2) { return false; }
    public void setTestProviderEnabled(String p0, boolean p1) {}
    public void setTestProviderLocation(String p0, Location p1) {}
    public void unregisterAntennaInfoListener(Object p0) {}
    public void unregisterGnssMeasurementsCallback(Object p0) {}
    public void unregisterGnssNavigationMessageCallback(Object p0) {}
    public void unregisterGnssStatusCallback(Object p0) {}
}
