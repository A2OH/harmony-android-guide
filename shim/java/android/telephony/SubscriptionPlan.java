package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Iterator;

public final class SubscriptionPlan implements Parcelable {
    public static final int BYTES_UNKNOWN = 0;
    public static final int BYTES_UNLIMITED = 0;
    public static final int LIMIT_BEHAVIOR_BILLED = 0;
    public static final int LIMIT_BEHAVIOR_DISABLED = 0;
    public static final int LIMIT_BEHAVIOR_THROTTLED = 0;
    public static final int LIMIT_BEHAVIOR_UNKNOWN = 0;
    public static final int TIME_UNKNOWN = 0;

    public SubscriptionPlan() {}

    public Iterator<Object> cycleIterator() { return null; }
    public int describeContents() { return 0; }
    public int getDataLimitBehavior() { return 0; }
    public long getDataLimitBytes() { return 0L; }
    public long getDataUsageBytes() { return 0L; }
    public long getDataUsageTime() { return 0L; }
    public void writeToParcel(Parcel p0, int p1) {}
}
