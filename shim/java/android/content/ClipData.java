package android.content;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ClipData implements Parcelable {
    public ClipData(CharSequence p0, String[] p1, Object p2) {}
    public ClipData(ClipDescription p0, Object p1) {}
    public ClipData(ClipData p0) {}

    public void addItem(Object p0) {}
    public void addItem(ContentResolver p0, Object p1) {}
    public int describeContents() { return 0; }
    public ClipDescription getDescription() { return null; }
    public Object getItemAt(int p0) { return null; }
    public int getItemCount() { return 0; }
    public static ClipData newHtmlText(CharSequence p0, CharSequence p1, String p2) { return null; }
    public static ClipData newIntent(CharSequence p0, Intent p1) { return null; }
    public static ClipData newPlainText(CharSequence p0, CharSequence p1) { return null; }
    public static ClipData newRawUri(CharSequence p0, Uri p1) { return null; }
    public static ClipData newUri(ContentResolver p0, CharSequence p1, Uri p2) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
