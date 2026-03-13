package android.content;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class SyncStats implements Parcelable {
    public int numAuthExceptions = 0;
    public int numConflictDetectedExceptions = 0;
    public int numDeletes = 0;
    public int numEntries = 0;
    public int numInserts = 0;
    public int numIoExceptions = 0;
    public int numParseExceptions = 0;
    public int numSkippedEntries = 0;
    public int numUpdates = 0;

    public SyncStats() {}
    public SyncStats(Parcel p0) {}

    public void clear() {}
    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
