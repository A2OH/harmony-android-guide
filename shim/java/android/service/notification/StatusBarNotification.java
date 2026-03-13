package android.service.notification;
import android.app.Notification;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

public class StatusBarNotification implements Parcelable {
    public StatusBarNotification(Parcel p0) {}

    public StatusBarNotification clone() { return null; }
    public int describeContents() { return 0; }
    public String getGroupKey() { return null; }
    public int getId() { return 0; }
    public String getKey() { return null; }
    public Notification getNotification() { return null; }
    public String getOverrideGroupKey() { return null; }
    public String getPackageName() { return null; }
    public long getPostTime() { return 0L; }
    public String getTag() { return null; }
    public int getUid() { return 0; }
    public UserHandle getUser() { return null; }
    public boolean isAppGroup() { return false; }
    public boolean isClearable() { return false; }
    public boolean isGroup() { return false; }
    public boolean isOngoing() { return false; }
    public void setOverrideGroupKey(String p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
