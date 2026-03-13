package android.telephony.gsm;
import android.os.Bundle;
import android.telephony.CellLocation;

public class GsmCellLocation extends CellLocation {
    public GsmCellLocation() {}
    public GsmCellLocation(Bundle p0) {}

    public void fillInNotifierBundle(Bundle p0) {}
    public int getCid() { return 0; }
    public int getLac() { return 0; }
    public int getPsc() { return 0; }
    public void setLacAndCid(int p0, int p1) {}
    public void setStateInvalid() {}
}
