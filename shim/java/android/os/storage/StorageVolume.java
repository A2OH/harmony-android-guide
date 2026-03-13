package android.os.storage;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public final class StorageVolume implements Parcelable {
    public static final int EXTRA_STORAGE_VOLUME = 0;

    public StorageVolume() {}

    public int describeContents() { return 0; }
    public String getDescription(Context p0) { return null; }
    public String getState() { return null; }
    public boolean isEmulated() { return false; }
    public boolean isPrimary() { return false; }
    public boolean isRemovable() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
