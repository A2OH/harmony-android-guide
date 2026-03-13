package android.app.job;
import android.app.Service;
import android.os.IBinder;

public class JobServiceEngine {
    public JobServiceEngine(Service p0) {}

    public IBinder getBinder() { return null; }
    public void jobFinished(JobParameters p0, boolean p1) {}
    public boolean onStartJob(JobParameters p0) { return false; }
    public boolean onStopJob(JobParameters p0) { return false; }
}
