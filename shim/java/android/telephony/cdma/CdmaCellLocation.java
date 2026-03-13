package android.telephony.cdma;
import android.os.Bundle;
import android.telephony.CellLocation;

public class CdmaCellLocation extends CellLocation {
    public CdmaCellLocation() {}
    public CdmaCellLocation(Bundle p0) {}

    public static double convertQuartSecToDecDegrees(int p0) { return 0.0; }
    public void fillInNotifierBundle(Bundle p0) {}
    public int getBaseStationId() { return 0; }
    public int getBaseStationLatitude() { return 0; }
    public int getBaseStationLongitude() { return 0; }
    public int getNetworkId() { return 0; }
    public int getSystemId() { return 0; }
    public void setCellLocationData(int p0, int p1, int p2) {}
    public void setCellLocationData(int p0, int p1, int p2, int p3, int p4) {}
    public void setStateInvalid() {}
}
