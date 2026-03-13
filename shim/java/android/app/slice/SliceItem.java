package android.app.slice;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class SliceItem implements Parcelable {
    public static final int FORMAT_ACTION = 0;
    public static final int FORMAT_BUNDLE = 0;
    public static final int FORMAT_IMAGE = 0;
    public static final int FORMAT_INT = 0;
    public static final int FORMAT_LONG = 0;
    public static final int FORMAT_REMOTE_INPUT = 0;
    public static final int FORMAT_SLICE = 0;
    public static final int FORMAT_TEXT = 0;

    public SliceItem() {}

    public int describeContents() { return 0; }
    public PendingIntent getAction() { return null; }
    public Bundle getBundle() { return null; }
    public String getFormat() { return null; }
    public Icon getIcon() { return null; }
    public int getInt() { return 0; }
    public long getLong() { return 0L; }
    public RemoteInput getRemoteInput() { return null; }
    public Slice getSlice() { return null; }
    public String getSubType() { return null; }
    public CharSequence getText() { return null; }
    public boolean hasHint(String p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
