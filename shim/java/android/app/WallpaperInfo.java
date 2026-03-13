package android.app;

/**
 * Android-compatible WallpaperInfo shim.
 * Provides information about a wallpaper service component.
 * Stub implementation — returns sensible defaults.
 */
public class WallpaperInfo {

    private final String mPackageName;
    private final String mServiceName;

    public WallpaperInfo() {
        mPackageName = "";
        mServiceName = "";
    }

    /** Returns the package name of the wallpaper service. */
    public String getPackageName() {
        return mPackageName;
    }

    /** Returns the class name of the wallpaper service. */
    public String getServiceName() {
        return mServiceName;
    }

    /**
     * Load a user-visible label for this wallpaper.
     * @param pm ignored in shim (Object to avoid dependency chain)
     * @return empty string in shim
     */
    public CharSequence loadLabel(Object pm) {
        return "";
    }

    /**
     * Load a user-visible icon for this wallpaper.
     * @param pm ignored in shim (Object to avoid dependency chain)
     * @return null in shim — no Drawable support
     */
    public Object loadIcon(Object pm) {
        return null;
    }

    /**
     * Load a user-visible thumbnail for this wallpaper.
     * @param pm ignored in shim (Object to avoid dependency chain)
     * @return null in shim — no Drawable support
     */
    public Object loadThumbnail(Object pm) {
        return null;
    }

    /**
     * Returns the description of this wallpaper.
     * @param pm ignored in shim (Object to avoid dependency chain)
     * @return empty string in shim
     */
    public CharSequence loadDescription(Object pm) {
        return "";
    }

    @Override
    public String toString() {
        return "WallpaperInfo{package=" + mPackageName + ", service=" + mServiceName + "}";
    }
}
