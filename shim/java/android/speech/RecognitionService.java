package android.speech;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecognitionService extends Service {
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public RecognitionService() {}

    public IBinder onBind(Intent p0) { return null; }
    public void onCancel(Object p0) {}
    public void onStartListening(Intent p0, Object p1) {}
    public void onStopListening(Object p0) {}
}
