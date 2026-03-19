package android.view;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class DragAndDropPermissions implements Parcelable {
    public DragAndDropPermissions() {}

    public int describeContents() { return 0; }
    public void release() {}
    public void writeToParcel(Parcel p0, int p1) {}

    public static DragAndDropPermissions obtain(DragEvent event) { return new DragAndDropPermissions(); }
    public void takeTransient() {}
}
