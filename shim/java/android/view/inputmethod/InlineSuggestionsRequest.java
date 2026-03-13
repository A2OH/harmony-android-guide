package android.view.inputmethod;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class InlineSuggestionsRequest implements Parcelable {
    public static final int SUGGESTION_COUNT_UNLIMITED = 0;

    public InlineSuggestionsRequest() {}

    public int describeContents() { return 0; }
    public int getMaxSuggestionCount() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
