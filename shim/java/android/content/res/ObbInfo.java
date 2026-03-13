package android.content.res;

/**
 * Shim: android.content.res.ObbInfo
 * Holds metadata about an OBB (Opaque Binary Blob) expansion file.
 */
public class ObbInfo {

    /** Flag indicating that this OBB is an overlay patch for a previously installed OBB. */
    public static final int OBB_OVERLAY = 1 << 0;

    /** Absolute path to the OBB file on the device storage. */
    public String filename;

    /** The package name that owns this OBB. */
    public String packageName;

    /** The version code of the OBB as declared in its manifest. */
    public int version;

    /** Bit-field of flags describing this OBB (e.g. {@link #OBB_OVERLAY}). */
    public int flags;

    /** Construct an empty ObbInfo. */
    public ObbInfo() {
    }

    @Override
    public String toString() {
        return "ObbInfo{filename='" + filename + "', packageName='" + packageName
                + "', version=" + version + ", flags=" + flags + "}";
    }
}
