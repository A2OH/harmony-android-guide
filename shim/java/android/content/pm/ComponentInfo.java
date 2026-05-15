package android.content.pm;
import android.os.Parcel;

public class ComponentInfo extends PackageItemInfo {
    public ApplicationInfo applicationInfo;
    public int descriptionRes = 0;
    public boolean directBootAware = false;
    public boolean enabled = true;
    public boolean exported = false;
    public String processName;
    public String splitName;

    public ComponentInfo() {}
    public ComponentInfo(ComponentInfo p0) {}
    public ComponentInfo(Parcel p0) {}

    public int getBannerResource() { return 0; }
    public int getIconResource() { return 0; }
    public int getLogoResource() { return 0; }
    public boolean isEnabled() { return false; }
}
