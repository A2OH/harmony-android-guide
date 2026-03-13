package android.app.job;

import android.os.Bundle;

/**
 * Android-compatible JobParameters shim.
 * Carries the parameters that were supplied when a job was scheduled.
 */
public class JobParameters {
    private final int mJobId;
    private final Bundle mExtras;
    private final boolean mOverrideDeadlineExpired;

    /** Package-private constructor — created by the JobScheduler shim. */
    JobParameters(int jobId, Bundle extras, boolean overrideDeadlineExpired) {
        mJobId = jobId;
        mExtras = extras != null ? extras : new Bundle();
        mOverrideDeadlineExpired = overrideDeadlineExpired;
    }

    /** Returns the unique ID of the job being executed. */
    public int getJobId() {
        return mJobId;
    }

    /**
     * Returns the extras Bundle that was set on the JobInfo via
     * {@link JobInfo.Builder#setExtras(Bundle)}, or an empty Bundle if none was set.
     */
    public Bundle getExtras() {
        return mExtras;
    }

    /**
     * Returns true if the system triggered this job because the override deadline expired,
     * rather than because the normal constraints were satisfied.
     */
    public boolean isOverrideDeadlineExpired() {
        return mOverrideDeadlineExpired;
    }
}
