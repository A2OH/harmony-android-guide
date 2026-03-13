package android.net.wifi.rtt;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public final class ResponderLocation implements Parcelable {
    public static final int ALTITUDE_FLOORS = 0;
    public static final int ALTITUDE_METERS = 0;
    public static final int ALTITUDE_UNDEFINED = 0;
    public static final int DATUM_NAD83_MLLW = 0;
    public static final int DATUM_NAD83_NAV88 = 0;
    public static final int DATUM_UNDEFINED = 0;
    public static final int DATUM_WGS84 = 0;
    public static final int LCI_VERSION_1 = 0;
    public static final int LOCATION_FIXED = 0;
    public static final int LOCATION_MOVEMENT_UNKNOWN = 0;
    public static final int LOCATION_RESERVED = 0;
    public static final int LOCATION_VARIABLE = 0;

    public ResponderLocation() {}

    public int describeContents() { return 0; }
    public double getAltitude() { return 0.0; }
    public int getAltitudeType() { return 0; }
    public double getAltitudeUncertainty() { return 0.0; }
    public List<?> getColocatedBssids() { return null; }
    public int getDatum() { return 0; }
    public int getExpectedToMove() { return 0; }
    public double getFloorNumber() { return 0.0; }
    public double getHeightAboveFloorMeters() { return 0.0; }
    public double getHeightAboveFloorUncertaintyMeters() { return 0.0; }
    public double getLatitude() { return 0.0; }
    public double getLatitudeUncertainty() { return 0.0; }
    public int getLciVersion() { return 0; }
    public double getLongitude() { return 0.0; }
    public double getLongitudeUncertainty() { return 0.0; }
    public boolean getRegisteredLocationAgreementIndication() { return false; }
    public boolean isLciSubelementValid() { return false; }
    public boolean isZaxisSubelementValid() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
