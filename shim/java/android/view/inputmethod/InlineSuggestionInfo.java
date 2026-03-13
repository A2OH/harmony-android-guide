package android.view.inputmethod;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class InlineSuggestionInfo implements Parcelable {
    public static final int SOURCE_AUTOFILL = 0;
    public static final int SOURCE_PLATFORM = 0;
    public static final int TYPE_ACTION = 0;
    public static final int TYPE_SUGGESTION = 0;

    public InlineSuggestionInfo() {}

    public int describeContents() { return 0; }
    public boolean isPinned() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
