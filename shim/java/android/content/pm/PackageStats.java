package android.content.pm;
import android.util.Size;
import android.util.Size;

/**
 * Shim for android.content.pm.PackageStats — deprecated package size info, stub fields only.
 *
 * @deprecated This class was deprecated in Android API level 26.
 */
@Deprecated
public class PackageStats {

    /** Name of the package to which this stats applies. */
    public String packageName;

    /** Size of the code (dex + so) in bytes. */
    public long codeSize;

    /** Size of the data directory in bytes. */
    public long dataSize;

    /** Size of the cache directory in bytes. */
    public long cacheSize;

    /** Size of external code in bytes. */
    public long externalCodeSize;

    /** Size of external data in bytes. */
    public long externalDataSize;

    /** Size of external cache in bytes. */
    public long externalCacheSize;

    public PackageStats(String packageName) {
        this.packageName = packageName;
    }

    public PackageStats(PackageStats orig) {
        if (orig != null) {
            this.packageName      = orig.packageName;
            this.codeSize         = orig.codeSize;
            this.dataSize         = orig.dataSize;
            this.cacheSize        = orig.cacheSize;
            this.externalCodeSize = orig.externalCodeSize;
            this.externalDataSize = orig.externalDataSize;
            this.externalCacheSize = orig.externalCacheSize;
        }
    }
}
