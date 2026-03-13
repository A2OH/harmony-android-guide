package android.app.job;
import android.os.Parcel;
import android.os.Parcelable;

public class JobParameters implements Parcelable {
    public JobParameters() {}

    public void completeWork(JobWorkItem p0) {}
    public int describeContents() { return 0; }
    public int getClipGrantFlags() { return 0; }
    public int getJobId() { return 0; }
    public boolean isOverrideDeadlineExpired() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
