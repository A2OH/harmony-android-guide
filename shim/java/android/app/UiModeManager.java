package android.app;

/**
 * Android-compatible UiModeManager shim. Stub — always reports normal/auto on OH.
 *
 * OH mapping: No direct equivalent. Night mode is controlled by
 * @ohos.display (system brightness/dark-mode API), which is not accessible
 * via a public Java API in the migration shim layer. This shim satisfies
 * compile-time dependencies only.
 */
public class UiModeManager {

    // ── Night mode constants ───────────────────────────────────────────────────

    /** Night mode is determined automatically (e.g. based on time or ambient light). */
    public static final int MODE_NIGHT_AUTO = 0;

    /** Night mode is always disabled — display stays in day/light mode. */
    public static final int MODE_NIGHT_NO = 1;

    /** Night mode is always enabled — display uses dark/night palette. */
    public static final int MODE_NIGHT_YES = 2;

    // ── UI mode type constants ─────────────────────────────────────────────────

    /** The device is in normal (phone/tablet) UI mode. */
    public static final int UI_MODE_TYPE_NORMAL = 1;

    /** The device is in desk dock UI mode. */
    public static final int UI_MODE_TYPE_DESK = 2;

    /** The device is in car dock UI mode. */
    public static final int UI_MODE_TYPE_CAR = 3;

    /** The device is connected to a television. */
    public static final int UI_MODE_TYPE_TELEVISION = 4;

    // ── Singleton ──────────────────────────────────────────────────────────────

    private static UiModeManager sInstance;

    private int mCurrentModeType = UI_MODE_TYPE_NORMAL;
    private int mNightMode = MODE_NIGHT_AUTO;
    private boolean mCarModeEnabled = false;

    private UiModeManager() {}

    /**
     * Returns the UiModeManager singleton.
     *
     * @param context application context (typed as Object for shim compatibility)
     */
    public static UiModeManager getInstance(Object context) {
        if (sInstance == null) {
            sInstance = new UiModeManager();
        }
        return sInstance;
    }

    // ── Mode queries ───────────────────────────────────────────────────────────

    /**
     * Returns the current UI mode type (e.g. {@link #UI_MODE_TYPE_NORMAL}).
     * On OH always returns {@link #UI_MODE_TYPE_NORMAL}.
     */
    public int getCurrentModeType() {
        return mCurrentModeType;
    }

    /**
     * Returns the current night mode setting.
     * On OH always returns {@link #MODE_NIGHT_AUTO}.
     */
    public int getNightMode() {
        return mNightMode;
    }

    // ── Mode changes ───────────────────────────────────────────────────────────

    /**
     * Sets the night mode. Stub — records the value locally but has no effect
     * on the OH display system.
     *
     * @param mode one of {@link #MODE_NIGHT_AUTO}, {@link #MODE_NIGHT_NO},
     *             or {@link #MODE_NIGHT_YES}
     */
    public void setNightMode(int mode) {
        mNightMode = mode;
    }

    /**
     * Object to enter car mode. Stub — records state locally only.
     *
     * @param flags optional flags (ignored in shim)
     */
    public void enableCarMode(int flags) {
        mCarModeEnabled = true;
        mCurrentModeType = UI_MODE_TYPE_CAR;
    }

    /**
     * Object to exit car mode. Stub — records state locally only.
     *
     * @param flags optional flags (ignored in shim)
     */
    public void disableCarMode(int flags) {
        mCarModeEnabled = false;
        mCurrentModeType = UI_MODE_TYPE_NORMAL;
    }

    /** Returns true if the device is currently in car mode. */
    public boolean isCarModeEnabled() {
        return mCarModeEnabled;
    }
}
