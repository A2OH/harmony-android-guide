package android.content;
import android.os.Parcel;
import android.os.Parcelable;

public class ContentProviderOperation implements Parcelable {
    public ContentProviderOperation() {}

    public int describeContents() { return 0; }
    public boolean isAssertQuery() { return false; }
    public boolean isCall() { return false; }
    public boolean isDelete() { return false; }
    public boolean isExceptionAllowed() { return false; }
    public boolean isInsert() { return false; }
    public boolean isReadOperation() { return false; }
    public boolean isUpdate() { return false; }
    public boolean isWriteOperation() { return false; }
    public boolean isYieldAllowed() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
