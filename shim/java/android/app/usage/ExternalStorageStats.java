package android.app.usage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class ExternalStorageStats implements Parcelable {
    public ExternalStorageStats() {}

    public int describeContents() { return 0; }
    public long getAppBytes() { return 0L; }
    public long getAudioBytes() { return 0L; }
    public long getImageBytes() { return 0L; }
    public long getTotalBytes() { return 0L; }
    public long getVideoBytes() { return 0L; }
    public void writeToParcel(Parcel p0, int p1) {}
}
