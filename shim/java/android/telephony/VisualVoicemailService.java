package android.telephony;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VisualVoicemailService extends Service {
    public static final int SERVICE_INTERFACE = 0;

    public VisualVoicemailService() {}

    public IBinder onBind(Intent p0) { return null; }
}
