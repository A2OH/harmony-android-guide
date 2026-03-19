package android.content.pm;
import android.os.Parcel;

public class ComponentInfo extends PackageItemInfo {
    public int applicationInfo = 0;
    public int descriptionRes = 0;
    public int directBootAware = 0;
    public int enabled = 0;
    public boolean exported = false;
    public int processName = 0;
    public int splitName = 0;

    public ComponentInfo() {}
    public ComponentInfo(ComponentInfo p0) {}
    public ComponentInfo(Parcel p0) {}

    public int getBannerResource() { return 0; }
    public int getIconResource() { return 0; }
    public int getLogoResource() { return 0; }
    public boolean isEnabled() { return false; }
}
