package android.service.carrier;
import android.app.Service;
import android.os.PersistableBundle;

public class CarrierService extends Service {
    public static final int CARRIER_SERVICE_INTERFACE = 0;

    public CarrierService() {}

    public void notifyCarrierNetworkChange(boolean p0) {}
    public PersistableBundle onLoadConfig(CarrierIdentifier p0) { return null; }
}
