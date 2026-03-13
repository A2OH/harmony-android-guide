package android.content.pm;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;

public class ResolveInfo implements Parcelable {
    public int activityInfo = 0;
    public int filter = 0;
    public int icon = 0;
    public int isDefault = 0;
    public int isInstantAppAvailable = 0;
    public int labelRes = 0;
    public int match = 0;
    public int nonLocalizedLabel = 0;
    public int preferredOrder = 0;
    public int priority = 0;
    public int providerInfo = 0;
    public int resolvePackageName = 0;
    public int serviceInfo = 0;
    public int specificIndex = 0;

    public ResolveInfo() {}
    public ResolveInfo(ResolveInfo p0) {}

    public int describeContents() { return 0; }
    public void dump(Printer p0, String p1) {}
    public int getIconResource() { return 0; }
    public boolean isCrossProfileIntentForwarderActivity() { return false; }
    public Drawable loadIcon(PackageManager p0) { return null; }
    public CharSequence loadLabel(PackageManager p0) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
