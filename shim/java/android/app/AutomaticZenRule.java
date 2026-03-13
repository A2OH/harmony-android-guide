package android.app;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.ZenPolicy;

public final class AutomaticZenRule implements Parcelable {
    public AutomaticZenRule(String p0, ComponentName p1, ComponentName p2, Uri p3, ZenPolicy p4, int p5, boolean p6) {}
    public AutomaticZenRule(Parcel p0) {}

    public int describeContents() { return 0; }
    public Uri getConditionId() { return null; }
    public long getCreationTime() { return 0L; }
    public int getInterruptionFilter() { return 0; }
    public String getName() { return null; }
    public ComponentName getOwner() { return null; }
    public ZenPolicy getZenPolicy() { return null; }
    public boolean isEnabled() { return false; }
    public void setConditionId(Uri p0) {}
    public void setConfigurationActivity(ComponentName p0) {}
    public void setEnabled(boolean p0) {}
    public void setInterruptionFilter(int p0) {}
    public void setName(String p0) {}
    public void setZenPolicy(ZenPolicy p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
