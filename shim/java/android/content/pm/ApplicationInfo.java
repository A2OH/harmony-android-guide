package android.content.pm;

public class ApplicationInfo {

    public static final int FLAG_SYSTEM    = 1;
    public static final int FLAG_DEBUGGABLE = 2;

    public String packageName;
    public String name;
    public String appComponentFactory;
    public String sourceDir;
    public String dataDir;
    public int    flags;
    public int    uid;
    public int    targetSdkVersion;

    public boolean hasRtlSupport() { return false; }
}
