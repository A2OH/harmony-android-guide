package android.content.pm;

/**
 * Shim for android.content.pm.ConfigurationInfo — device configuration requirements.
 * Stub fields and no-op methods.
 */
public class ConfigurationInfo {

    /** Value for {@link #reqTouchScreen} if the application doesn't require a touch screen. */
    public static final int INPUT_FEATURE_HARD_KEYBOARD = 0x00000001;

    /** Value for {@link #reqInputFeatures} if the application requires a five-way navigation device. */
    public static final int INPUT_FEATURE_FIVE_WAY_NAV  = 0x00000002;

    /** Minimum OpenGL ES version required, encoded as (major << 16) | minor. */
    public static final int GL_ES_VERSION_UNDEFINED = 0;

    /** Touch screen type required by the application. */
    public int reqTouchScreen;

    /** Keyboard type required by the application. */
    public int reqKeyboardType;

    /** Navigation method required by the application. */
    public int reqNavigation;

    /** Flags for input device features required by the application. */
    public int reqInputFeatures;

    /**
     * The OpenGL ES version required by the application. The upper 16 bits represent the
     * major version and the lower 16 bits the minor version.  For example, version 2.0 is
     * represented as 0x00020000.  If set to 0, only OpenGL ES 1.0 is required.
     */
    public int reqGlEsVersion;

    public ConfigurationInfo() {}

    /**
     * Returns the OpenGL ES version required by this configuration as a human-readable string,
     * e.g. "2.0".
     */
    public String getGlEsVersion() {
        if (reqGlEsVersion == GL_ES_VERSION_UNDEFINED) {
            return "1.0";
        }
        int major = (reqGlEsVersion >> 16) & 0xffff;
        int minor = reqGlEsVersion & 0xffff;
        return major + "." + minor;
    }

    @Override
    public String toString() {
        return "ConfigurationInfo{"
                + "reqTouchScreen=" + reqTouchScreen
                + ", reqKeyboardType=" + reqKeyboardType
                + ", reqNavigation=" + reqNavigation
                + ", reqInputFeatures=" + reqInputFeatures
                + ", reqGlEsVersion=" + getGlEsVersion()
                + "}";
    }
}
