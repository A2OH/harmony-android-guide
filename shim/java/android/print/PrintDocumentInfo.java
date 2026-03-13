package android.print;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class PrintDocumentInfo implements Parcelable {
    public static final int CONTENT_TYPE_DOCUMENT = 0;
    public static final int CONTENT_TYPE_PHOTO = 0;
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final int PAGE_COUNT_UNKNOWN = 0;

    public PrintDocumentInfo() {}

    public int describeContents() { return 0; }
    public int getContentType() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
