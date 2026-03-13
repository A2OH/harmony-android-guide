package android.app.job;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public final class JobWorkItem implements Parcelable {
    public JobWorkItem(Intent p0) {}
    public JobWorkItem(Intent p0, long p1, long p2) {}

    public int describeContents() { return 0; }
    public int getDeliveryCount() { return 0; }
    public long getEstimatedNetworkDownloadBytes() { return 0L; }
    public long getEstimatedNetworkUploadBytes() { return 0L; }
    public Intent getIntent() { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
