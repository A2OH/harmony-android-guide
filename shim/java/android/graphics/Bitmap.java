package android.graphics;

/**
 * Shim: android.graphics.Bitmap
 * OH mapping: image.PixelMap
 *
 * This is a minimal stub sufficient to satisfy API signatures that pass
 * Bitmap as a parameter (e.g., WebViewClient.onPageStarted favicon).
 * Actual pixel-level operations require the OH bridge.
 */
public class Bitmap {

    public enum Config {
        ALPHA_8,
        RGB_565,
        ARGB_4444,
        ARGB_8888,
        RGBA_F16,
        HARDWARE
    }

    private final int width;
    private final int height;
    private final Config config;
    private boolean recycled = false;

    private Bitmap(int width, int height, Config config) {
        this.width  = width;
        this.height = height;
        this.config = config;
    }

    // ── Factory ──

    public static Bitmap createBitmap(int width, int height, Config config) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Bitmap dimensions must be positive");
        }
        return new Bitmap(width, height, config);
    }

    public static Bitmap createBitmap(Bitmap src) {
        if (src == null) throw new NullPointerException("src must not be null");
        return new Bitmap(src.width, src.height, src.config);
    }

    // ── Properties ──

    public int getWidth()  { return width; }
    public int getHeight() { return height; }
    public Config getConfig() { return config; }
    public boolean isRecycled() { return recycled; }

    /** Release pixel memory. Subsequent operations will throw. */
    public void recycle() { recycled = true; }

    public int getByteCount() {
        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:   bytesPerPixel = 1; break;
            case RGB_565:
            case ARGB_4444: bytesPerPixel = 2; break;
            case RGBA_F16:  bytesPerPixel = 8; break;
            default:        bytesPerPixel = 4; break; // ARGB_8888 / HARDWARE
        }
        return width * height * bytesPerPixel;
    }

    @Override
    public String toString() {
        return "Bitmap(" + width + "x" + height + ", " + config + ")";
    }
}
