package android.service.media;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CameraPrewarmService extends Service {
    public CameraPrewarmService() {}

    public IBinder onBind(Intent p0) { return null; }
    public void onCooldown(boolean p0) {}
    public void onPrewarm() {}
}
