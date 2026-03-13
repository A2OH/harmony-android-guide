package android.content.pm;

/**
 * Android-compatible PackageItemInfo shim. Base class for components in a package.
 * Stub — no-op implementation for A2OH migration.
 */
public class PackageItemInfo {
    public String name;
    public String packageName;
    public int labelRes;
    public int icon;
    /** Bundle represented as Object to avoid dependency on android.os.Bundle. */
    public Object metaData;
    public CharSequence nonLocalizedLabel;

    public PackageItemInfo() {}

    public PackageItemInfo(PackageItemInfo orig) {
        name = orig.name;
        packageName = orig.packageName;
        labelRes = orig.labelRes;
        icon = orig.icon;
        metaData = orig.metaData;
        nonLocalizedLabel = orig.nonLocalizedLabel;
    }

    public CharSequence loadLabel(Object pm) {
        if (nonLocalizedLabel != null) return nonLocalizedLabel;
        return name != null ? name : "";
    }
}
