package android.content.pm;

/**
 * Shim: android.content.pm.FeatureInfo
 *
 * Describes a feature that an application requires or that a device provides.
 * Fields are public to match the real Android API, which exposes them directly
 * rather than via accessor methods.
 */
public class FeatureInfo {

    // ── Flag constants ───────────────────────────────────────────────────────

    /**
     * Set in {@link #flags} when this is a required (not optional) feature.
     */
    public static final int FLAG_REQUIRED = 0x0001;

    /**
     * Sentinel value for {@link #reqGlEsVersion}: indicates that no OpenGL ES
     * version requirement is specified (i.e. this is a non-GL feature entry).
     */
    public static final int GL_ES_VERSION_UNDEFINED = 0;

    // ── Public fields (match the real PackageManager API layout) ─────────────

    /**
     * The name of this feature, e.g. {@code "android.hardware.camera"}.
     * Null when this entry encodes a minimum OpenGL ES version requirement
     * rather than a named hardware feature.
     */
    public String name;

    /**
     * The minimum OpenGL ES version required, encoded as
     * {@code (major << 16) | minor}, or {@link #GL_ES_VERSION_UNDEFINED} (0)
     * when there is no GL requirement.
     */
    public int reqGlEsVersion = GL_ES_VERSION_UNDEFINED;

    /**
     * Additional flags for this feature entry (see {@link #FLAG_REQUIRED}).
     */
    public int flags;

    // ── Constructors ─────────────────────────────────────────────────────────

    /** Create an empty FeatureInfo. */
    public FeatureInfo() {}

    /** Copy constructor. */
    public FeatureInfo(FeatureInfo other) {
        if (other != null) {
            this.name           = other.name;
            this.reqGlEsVersion = other.reqGlEsVersion;
            this.flags          = other.flags;
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Return a human-readable string describing the required OpenGL ES version.
     *
     * @return e.g. {@code "0x00020000"} for OpenGL ES 2.0
     */
    public String getGlEsVersion() {
        int major = (reqGlEsVersion >> 16) & 0xffff;
        int minor =  reqGlEsVersion        & 0xffff;
        return major + "." + minor;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        if (name != null) {
            return "FeatureInfo{name=" + name
                    + ", flags=0x" + Integer.toHexString(flags) + "}";
        } else {
            return "FeatureInfo{reqGlEsVersion=" + getGlEsVersion()
                    + ", flags=0x" + Integer.toHexString(flags) + "}";
        }
    }
}
