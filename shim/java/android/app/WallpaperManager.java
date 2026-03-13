package android.app;

/**
 * Android-compatible WallpaperManager shim. Stub — always reports unsupported.
 *
 * OH mapping: No direct equivalent. Custom home-screen wallpaper is not a
 * developer-accessible feature in OpenHarmony; background imagery is handled
 * by the system launcher. This shim satisfies compile-time dependencies only.
 */
public class WallpaperManager {

    // ── Intent action constants ────────────────────────────────────────────────

    /** Intent action to open the live wallpaper picker. */
    public static final String ACTION_CHANGE_LIVE_WALLPAPER =
            "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";

    /** Intent action to open the wallpaper picker. */
    public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";

    // ── Singleton ──────────────────────────────────────────────────────────────

    private static WallpaperManager sInstance;

    private WallpaperManager() {}

    /**
     * Returns the WallpaperManager singleton for this context.
     *
     * @param context application context (typed as Object for shim compatibility)
     */
    public static WallpaperManager getInstance(Object context) {
        if (sInstance == null) {
            sInstance = new WallpaperManager();
        }
        return sInstance;
    }

    // ── Wallpaper retrieval ────────────────────────────────────────────────────

    /**
     * Returns a Drawable (typed as Object) showing the current wallpaper,
     * or null if unsupported / not set.
     */
    public Object getDrawable() {
        return null; // stub
    }

    /**
     * Like getDrawable(), but returns null immediately rather than blocking
     * if the wallpaper is not yet available.
     */
    public Object peekDrawable() {
        return null; // stub
    }

    // ── Wallpaper setting ──────────────────────────────────────────────────────

    /**
     * Set the current system wallpaper to a bitmap.
     * Stub — no-op on OH; always throws to signal unsupported.
     *
     * @param bitmap the Bitmap to use (typed as Object)
     */
    public void setBitmap(Object bitmap) throws java.io.IOException {
        if (!isSetWallpaperAllowed()) {
            throw new java.io.IOException("SET_WALLPAPER permission not granted (stub)");
        }
        // no-op stub
    }

    /**
     * Set the current system wallpaper to the resource with the given ID.
     * Stub — no-op on OH.
     *
     * @param resid the drawable resource ID
     */
    public void setResource(int resid) throws java.io.IOException {
        if (!isSetWallpaperAllowed()) {
            throw new java.io.IOException("SET_WALLPAPER permission not granted (stub)");
        }
        // no-op stub
    }

    /**
     * Remove any currently set wallpaper, reverting to the system default.
     * Stub — no-op on OH.
     */
    public void clear() throws java.io.IOException {
        // no-op stub
    }

    // ── Dimensions ────────────────────────────────────────────────────────────

    /**
     * Returns the desired minimum width for wallpaper images, in pixels.
     * Returns 0 on OH (unsupported).
     */
    public int getDesiredMinimumWidth() {
        return 0;
    }

    /**
     * Returns the desired minimum height for wallpaper images, in pixels.
     * Returns 0 on OH (unsupported).
     */
    public int getDesiredMinimumHeight() {
        return 0;
    }

    // ── Capability checks ──────────────────────────────────────────────────────

    /**
     * Returns true if the wallpaper feature is supported on this device.
     * Always false on OH — wallpaper management is not exposed to third-party apps.
     */
    public boolean isWallpaperSupported() {
        return false;
    }

    /**
     * Returns true if the caller is allowed to set the wallpaper.
     * Always false on OH — the SET_WALLPAPER permission is not grantable.
     */
    public boolean isSetWallpaperAllowed() {
        return false;
    }
}
