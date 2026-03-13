package android.content.pm;

// Shim: android.content.pm.VersionedPackage
// Android-to-OpenHarmony migration compatibility stub.

public class VersionedPackage {

    private final String packageName;
    private final long versionCode;

    /**
     * Create a versioned package with a 32-bit version code.
     *
     * @param packageName the package name
     * @param versionCode the version code as an int
     */
    public VersionedPackage(String packageName, int versionCode) {
        this.packageName = packageName;
        this.versionCode = versionCode;
    }

    /**
     * Create a versioned package with a 64-bit version code.
     *
     * @param packageName the package name
     * @param versionCode the version code as a long
     */
    public VersionedPackage(String packageName, long versionCode) {
        this.packageName = packageName;
        this.versionCode = versionCode;
    }

    /** Returns the package name. */
    public String getPackageName() {
        return packageName;
    }

    /** Returns the version code as a long. */
    public long getLongVersionCode() {
        return versionCode;
    }
}
