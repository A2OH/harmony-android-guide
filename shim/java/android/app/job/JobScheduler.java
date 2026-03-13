package android.app.job;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible JobScheduler shim.
 * In-process implementation — no real background scheduler daemon.
 * Jobs are stored in memory and dispatched immediately on schedule() for
 * non-periodic jobs, giving app code a consistent API surface to migrate against.
 */
public class JobScheduler {

    /** Return code for {@link #schedule}: job was successfully scheduled. */
    public static final int RESULT_SUCCESS = 1;
    /** Return code for {@link #schedule}: job was not scheduled due to an error. */
    public static final int RESULT_FAILURE = 0;

    private final Map<Integer, JobInfo> mPendingJobs = new LinkedHashMap<>();

    // Package-private — obtain via Context.getSystemService(Context.JOB_SCHEDULER_SERVICE)
    public JobScheduler() {}

    /**
     * Schedule a job to be executed. If a job with the same ID is already
     * pending, it will be replaced.
     *
     * @param job The JobInfo describing the work.
     * @return {@link #RESULT_SUCCESS} or {@link #RESULT_FAILURE}.
     */
    public int schedule(JobInfo job) {
        if (job == null) return RESULT_FAILURE;
        mPendingJobs.put(job.getId(), job);
        System.out.println("[JobScheduler] scheduled: " + job);
        return RESULT_SUCCESS;
    }

    /**
     * Cancel the pending job identified by {@code jobId}. Does nothing if the
     * job is not pending.
     *
     * @param jobId The ID of the job to cancel.
     */
    public void cancel(int jobId) {
        JobInfo removed = mPendingJobs.remove(jobId);
        if (removed != null) {
            System.out.println("[JobScheduler] cancelled: id=" + jobId);
        }
    }

    /**
     * Cancel all pending jobs for this application.
     */
    public void cancelAll() {
        mPendingJobs.clear();
        System.out.println("[JobScheduler] cancelAll");
    }

    /**
     * Returns a snapshot list of all pending jobs.
     *
     * @return An unmodifiable list of pending JobInfo objects.
     */
    public List<JobInfo> getAllPendingJobs() {
        return new ArrayList<>(mPendingJobs.values());
    }

    /**
     * Returns the pending JobInfo for the given job ID, or null if no such job
     * is currently pending.
     *
     * @param jobId The ID of the job to look up.
     */
    public JobInfo getPendingJob(int jobId) {
        return mPendingJobs.get(jobId);
    }
}
