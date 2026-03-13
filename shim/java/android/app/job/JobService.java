package android.app.job;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class JobService extends Service {
    public static final int PERMISSION_BIND = 0;

    public JobService() {}

    public void jobFinished(JobParameters p0, boolean p1) {}
    public IBinder onBind(Intent p0) { return null; }
    public boolean onStartJob(JobParameters p0) { return false; }
    public boolean onStopJob(JobParameters p0) { return false; }
}
