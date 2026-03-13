package android.view.textservice;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;

public final class SpellCheckerSubtype implements Parcelable {
    public SpellCheckerSubtype() {}

    public boolean containsExtraValueKey(String p0) { return false; }
    public int describeContents() { return 0; }
    public CharSequence getDisplayName(Context p0, String p1, ApplicationInfo p2) { return null; }
    public String getExtraValue() { return null; }
    public String getExtraValueOf(String p0) { return null; }
    public int getNameResId() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
