package android.content.pm;

/**
 * Android-compatible InstrumentationInfo shim.
 * Extends PackageItemInfo; describes a declared Instrumentation component.
 * Stub — no-op implementation for A2OH migration.
 */
public class InstrumentationInfo extends PackageItemInfo {

    /** The package that is being instrumented. */
    public String targetPackage;

    /** Full path to the base APK / source directory of this instrumentation. */
    public String sourceDir;

    /** Path to the data directory for this instrumentation. */
    public String dataDir;

    /**
     * Specifies whether or not this instrumentation will handle profiling.
     * When true the framework will not perform profiling itself.
     */
    public boolean handleProfiling;

    /**
     * Specifies whether or not this instrumentation is a functional test.
     */
    public boolean functionalTest;

    public InstrumentationInfo() {}

    public InstrumentationInfo(InstrumentationInfo orig) {
        super(orig);
        targetPackage   = orig.targetPackage;
        sourceDir       = orig.sourceDir;
        dataDir         = orig.dataDir;
        handleProfiling = orig.handleProfiling;
        functionalTest  = orig.functionalTest;
    }

    @Override
    public String toString() {
        return "InstrumentationInfo{"
                + "targetPackage=" + targetPackage
                + ", name=" + name
                + '}';
    }
}
