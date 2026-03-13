package android.app.usage;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;

public final class ConfigurationStats implements Parcelable {
    public ConfigurationStats(ConfigurationStats p0) {}

    public int describeContents() { return 0; }
    public int getActivationCount() { return 0; }
    public Configuration getConfiguration() { return null; }
    public long getFirstTimeStamp() { return 0L; }
    public long getLastTimeActive() { return 0L; }
    public long getLastTimeStamp() { return 0L; }
    public long getTotalTimeActive() { return 0L; }
    public void writeToParcel(Parcel p0, int p1) {}
}
