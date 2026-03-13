package android.content.pm;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class SharedLibraryInfo implements Parcelable {
    public static final int TYPE_BUILTIN = 0;
    public static final int TYPE_DYNAMIC = 0;
    public static final int TYPE_STATIC = 0;
    public static final int VERSION_UNDEFINED = 0;

    public SharedLibraryInfo() {}

    public int describeContents() { return 0; }
    public String getName() { return null; }
    public int getType() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
