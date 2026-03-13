package android.view.inputmethod;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public final class InlineSuggestion implements Parcelable {
    public InlineSuggestion() {}

    public int describeContents() { return 0; }
    public void inflate(Context p0, Size p1, Executor p2, Object p3) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
