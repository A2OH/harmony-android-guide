package android.view;
import android.telecom.Call;
import android.util.DisplayMetrics;
import android.telecom.Call;
import android.util.DisplayMetrics;
import java.util.Set;

/**
 * Shim: android.view.Display — represents a physical display screen.
 *
 * On OpenHarmony the display dimensions are obtained via the ArkUI
 * displaySync or display module. This stub returns sensible defaults
 * (Full-HD portrait) so that code using DisplayMetrics to compute
 * layout dimensions can compile and run without crashes.
 *
 * For accurate display metrics at runtime, use OHBridge.getDisplayWidth()
 * / OHBridge.getDisplayHeight() when those are wired up.
 */
public class Display {

    /** Default display ID (matches Android's Display.DEFAULT_DISPLAY). */
    public static final int DEFAULT_DISPLAY = 0;

    // ── Mock dimensions (overridable at runtime) ──
    private static int sMockWidth  = 1080;
    private static int sMockHeight = 1920;
    private static float sMockDensity = 3.0f; // xxhdpi

    private final int mDisplayId;

    public Display() {
        this(DEFAULT_DISPLAY);
    }

    public Display(int displayId) {
        mDisplayId = displayId;
    }

    // ── Runtime override ──

    /**
     * Set the dimensions reported by this mock Display.
     * Call this from your ArkUI bridge initialisation code once the real
     * screen size is known.
     */
    public static void setMockDimensions(int widthPx, int heightPx, float density) {
        sMockWidth   = widthPx;
        sMockHeight  = heightPx;
        sMockDensity = density;
    }

    // ── API ──

    public int getDisplayId() { return mDisplayId; }

    /** Screen width in pixels. */
    public int getWidth() { return sMockWidth; }

    /** Screen height in pixels. */
    public int getHeight() { return sMockHeight; }

    /**
     * Populate a {@link DisplayMetrics} object with this display's metrics.
     *
     * @param outMetrics The metrics object to fill.
     */
    public void getMetrics(DisplayMetrics outMetrics) {
        if (outMetrics == null) return;
        outMetrics.widthPixels  = sMockWidth;
        outMetrics.heightPixels = sMockHeight;
        outMetrics.density      = sMockDensity;
        outMetrics.densityDpi   = (int) (sMockDensity * DisplayMetrics.DENSITY_DEFAULT);
        outMetrics.xdpi         = sMockDensity * DisplayMetrics.DENSITY_DEFAULT;
        outMetrics.ydpi         = sMockDensity * DisplayMetrics.DENSITY_DEFAULT;
        outMetrics.scaledDensity = sMockDensity;
    }

    /**
     * Populate a {@link DisplayMetrics} object with real display metrics.
     * Alias for {@link #getMetrics} — exists because Android has both methods.
     */
    public void getRealMetrics(DisplayMetrics outMetrics) {
        getMetrics(outMetrics);
    }

    /** Screen rotation (0 = portrait, 1 = landscape, 2 = reverse-portrait, 3 = reverse-landscape). */
    public int getRotation() { return 0; }

    @Override
    public String toString() {
        return "Display{id=" + mDisplayId + " " + sMockWidth + "x" + sMockHeight + "}";
    }

    // ── DisplayMetrics ──

    /**
     * Shim: android.util.DisplayMetrics — screen density and dimension info.
     *
     * Nested here for convenience; the real class is in android.util but this
     * stub avoids a cross-package dependency that would complicate the build.
     */
    public static class DisplayMetrics {
        public static final int DENSITY_LOW    = 120;
        public static final int DENSITY_MEDIUM = 160;
        public static final int DENSITY_DEFAULT = 160;
        public static final int DENSITY_HIGH   = 240;
        public static final int DENSITY_XHIGH  = 320;
        public static final int DENSITY_XXHIGH = 480;
        public static final int DENSITY_XXXHIGH = 640;

        public int   widthPixels;
        public int   heightPixels;
        public float density;
        public int   densityDpi;
        public float xdpi;
        public float ydpi;
        public float scaledDensity;
    }
}
