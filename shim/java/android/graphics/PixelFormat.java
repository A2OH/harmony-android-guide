package android.graphics;

/**
 * Android-compatible PixelFormat shim. Provides pixel format constants
 * and format info queries.
 */
public class PixelFormat {
    public static final int UNKNOWN = 0;
    public static final int TRANSLUCENT = -3;
    public static final int TRANSPARENT = -2;
    public static final int OPAQUE = -1;
    public static final int RGBA_8888 = 1;
    public static final int RGBX_8888 = 2;
    public static final int RGB_888 = 3;
    public static final int RGB_565 = 4;
    public static final int RGBA_5551 = 6;
    public static final int RGBA_4444 = 7;
    public static final int A_8 = 8;
    public static final int L_8 = 9;
    public static final int LA_88 = 10;
    public static final int RGB_332 = 11;

    public int bytesPerPixel;
    public int bitsPerPixel;

    public static void getPixelFormatInfo(int format, PixelFormat info) {
        switch (format) {
            case RGBA_8888:
            case RGBX_8888:
                info.bytesPerPixel = 4;
                info.bitsPerPixel = 32;
                break;
            case RGB_888:
                info.bytesPerPixel = 3;
                info.bitsPerPixel = 24;
                break;
            case RGB_565:
            case RGBA_5551:
            case RGBA_4444:
            case LA_88:
                info.bytesPerPixel = 2;
                info.bitsPerPixel = 16;
                break;
            case A_8:
            case L_8:
            case RGB_332:
                info.bytesPerPixel = 1;
                info.bitsPerPixel = 8;
                break;
            default:
                info.bytesPerPixel = 0;
                info.bitsPerPixel = 0;
                break;
        }
    }

    public static boolean formatHasAlpha(int format) {
        switch (format) {
            case RGBA_8888:
            case RGBA_5551:
            case RGBA_4444:
            case A_8:
            case LA_88:
            case TRANSLUCENT:
            case TRANSPARENT:
                return true;
            default:
                return false;
        }
    }
}
