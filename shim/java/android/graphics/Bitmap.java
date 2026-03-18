package android.graphics;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.graphics.Bitmap
 * OH mapping: image.PixelMap / drawing.OH_Drawing_Bitmap
 *
 * Wraps a native OH_Drawing_Bitmap handle for pixel-level operations.
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
    private long nativeHandle;

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
        Bitmap bmp = new Bitmap(width, height, config);
        bmp.nativeHandle = OHBridge.bitmapCreate(width, height, configToFormat(config));
        return bmp;
    }

    public static Bitmap createBitmap(android.util.DisplayMetrics display, int width, int height, Config config) {
        return createBitmap(width, height, config);
    }

    public static Bitmap createBitmap(Bitmap src) {
        if (src == null) throw new NullPointerException("src must not be null");
        Bitmap bmp = new Bitmap(src.width, src.height, src.config);
        bmp.nativeHandle = OHBridge.bitmapCreate(src.width, src.height, configToFormat(src.config));
        return bmp;
    }

    private static int configToFormat(Config config) {
        if (config == null) return 0;
        switch (config) {
            case ALPHA_8:   return 1;
            case RGB_565:   return 2;
            case ARGB_4444: return 3;
            default:        return 0; // ARGB_8888
        }
    }

    // ── Native handle ──

    public long getNativeHandle() { return nativeHandle; }

    // ── Properties ──

    public int getWidth()  { return width; }
    public int getHeight() { return height; }
    public Config getConfig() { return config; }
    public boolean isRecycled() { return recycled; }

    public void recycle() {
        if (nativeHandle != 0) { OHBridge.bitmapDestroy(nativeHandle); nativeHandle = 0; }
        recycled = true;
    }

    public int getByteCount() {
        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:   bytesPerPixel = 1; break;
            case RGB_565:
            case ARGB_4444: bytesPerPixel = 2; break;
            case RGBA_F16:  bytesPerPixel = 8; break;
            default:        bytesPerPixel = 4; break;
        }
        return width * height * bytesPerPixel;
    }

    // ── Pixel access ──

    public void setPixel(int x, int y, int color) {
        if (nativeHandle != 0) OHBridge.bitmapSetPixel(nativeHandle, x, y, color);
    }

    public int getPixel(int x, int y) {
        if (nativeHandle != 0) return OHBridge.bitmapGetPixel(nativeHandle, x, y);
        return 0;
    }

    // Methods needed for View.java compilation
    public void eraseColor(int color) { /* no-op */ }
    public void setDensity(int density) { /* no-op */ }
    public void setHasAlpha(boolean hasAlpha) { /* no-op */ }
    public boolean hasAlpha() { return true; }
    public int getDensity() { return 160; }

    @Override
    public String toString() {
        return "Bitmap(" + width + "x" + height + ", " + config + ")";
    }
}
