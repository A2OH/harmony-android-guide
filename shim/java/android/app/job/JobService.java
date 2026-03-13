package android.app.job;

import android.app.Service;
import android.os.IBinder;

/**
 * Android-compatible JobService shim.
 * Abstract base class that app code subclasses to perform scheduled work.
 *
 * In the OH migration shim there is no real JobScheduler daemon, so this class
 * provides the standard lifecycle contract without actual scheduling infrastructure.
 */
public abstract class JobService extends Service {

    /**
     * Override this method with the logic to perform when the job is triggered.
     *
     * @param params Parameters supplied by the JobScheduler at the time this
     *               job was run.
     * @return true if the job needs to run on a separate thread; false if the
     *         work is already done by the time this method returns.
     */
    public abstract boolean onStartJob(JobParameters params);

    /**
     * Called when the scheduling engine has decided to interrupt the execution
     * of a running job, most typically because the runtime constraints
     * associated with the job are no longer satisfied.
     *
     * @param params The parameters identifying this job, as supplied to the job
     *               in {@link #onStartJob}.
     * @return whether the job should be retried.
     */
    public abstract boolean onStopJob(JobParameters params);

    /**
     * Call this to inform the JobScheduler that the job has finished executing.
     *
     * @param params     The parameters identifying this job.
     * @param needsReschedule true if the job should be rescheduled based on the
     *                        retry criteria provided by the JobInfo; false if the
     *                        job is complete.
     */
    public final void jobFinished(JobParameters params, boolean needsReschedule) {
        // In the shim there is no real scheduler to notify; log the outcome.
        System.out.println("[JobService] jobFinished: id=" + params.getJobId()
                + " reschedule=" + needsReschedule);
    }

    /**
     * JobService does not support binding via the standard Service mechanism.
     * Returns null — clients interact through JobScheduler instead.
     */
    @Override
    public IBinder onBind(android.content.Intent intent) {
        return null;
    }
}
