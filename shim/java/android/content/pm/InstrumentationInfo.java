package android.content.pm;
import android.os.Parcelable;

public class InstrumentationInfo extends PackageItemInfo implements Parcelable {
    public int dataDir = 0;
    public int functionalTest = 0;
    public int handleProfiling = 0;
    public int publicSourceDir = 0;
    public int sourceDir = 0;
    public int splitNames = 0;
    public int splitPublicSourceDirs = 0;
    public int splitSourceDirs = 0;
    public int targetPackage = 0;
    public int targetProcesses = 0;

    public InstrumentationInfo() {}
    public InstrumentationInfo(InstrumentationInfo p0) {}

    public int describeContents() { return 0; }
}
