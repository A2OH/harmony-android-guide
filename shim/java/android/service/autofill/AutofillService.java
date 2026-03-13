package android.service.autofill;
import android.app.Service;
import android.content.Intent;
import android.os.CancellationSignal;
import android.os.IBinder;

public class AutofillService extends Service {
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public AutofillService() {}

    public IBinder onBind(Intent p0) { return null; }
    public void onConnected() {}
    public void onDisconnected() {}
    public void onFillRequest(FillRequest p0, CancellationSignal p1, FillCallback p2) {}
    public void onSaveRequest(SaveRequest p0, SaveCallback p1) {}
}
