package android.content.pm;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Printer;

public class PackageItemInfo {
    public int banner = 0;
    public int icon = 0;
    public int labelRes = 0;
    public int logo = 0;
    public android.os.Bundle metaData;
    public String name;
    public int nonLocalizedLabel = 0;
    public String packageName;

    public PackageItemInfo() {}
    public PackageItemInfo(PackageItemInfo p0) {}
    public PackageItemInfo(Parcel p0) {}

    public void dumpBack(Printer p0, String p1) {}
    public void dumpFront(Printer p0, String p1) {}
    public Drawable loadBanner(PackageManager p0) { return null; }
    public Drawable loadIcon(PackageManager p0) { return null; }
    public Drawable loadLogo(PackageManager p0) { return null; }
    public Drawable loadUnbadgedIcon(PackageManager p0) { return null; }
    public XmlResourceParser loadXmlMetaData(PackageManager p0, String p1) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
