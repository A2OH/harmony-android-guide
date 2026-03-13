package android.telecom;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class StatusHints implements Parcelable {
    public StatusHints(CharSequence p0, Icon p1, Bundle p2) {}

    public int describeContents() { return 0; }
    public Bundle getExtras() { return null; }
    public Icon getIcon() { return null; }
    public CharSequence getLabel() { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
