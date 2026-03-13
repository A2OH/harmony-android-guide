package android.graphics;

/**
 * Android-compatible ImageFormat shim. Provides image format constants and
 * bits-per-pixel lookup; no actual image processing.
 */
public class ImageFormat {
    /** Unknown image format. */
    public static final int UNKNOWN          = 0;
    /** JPEG compressed format. */
    public static final int JPEG             = 0x100;
    /** YCbCr 4:2:0 planar, NV21 packing (Y plane + interleaved VU). */
    public static final int NV21             = 0x11;
    /** YCbCr 4:2:2 semi-planar, NV16 packing. */
    public static final int NV16             = 0x10;
    /** YCbCr 4:2:2 packed (YUYV). */
    public static final int YUY2             = 0x14;
    /** YV12: 4:2:0 planar, Y-V-U order. */
    public static final int YV12             = 0x32315659;
    /** 16-bit RGB 5:6:5. */
    public static final int RGB_565          = 0x4;
    /** Flexible RGB 8:8:8 (no alpha). */
    public static final int FLEX_RGB_888     = 0x29;
    /** Flexible RGBA 8:8:8:8. */
    public static final int FLEX_RGBA_8888   = 0x2A;
    /** Raw Bayer sensor data, 16 bits per pixel. */
    public static final int RAW_SENSOR       = 0x20;
    /** Opaque raw camera private format. */
    public static final int RAW_PRIVATE      = 0x24;
    /** 10-bit raw Bayer, packed 4 pixels in 5 bytes. */
    public static final int RAW10            = 0x25;
    /** 12-bit raw Bayer, packed 2 pixels in 3 bytes. */
    public static final int RAW12            = 0x26;
    /** Depth image: 16 bits per pixel. */
    public static final int DEPTH16          = 0x44363159;
    /** Depth point cloud. */
    public static final int DEPTH_POINT_CLOUD = 0x101;
    /** Opaque/implementation-defined private format. */
    public static final int PRIVATE          = 0x22;
    /** Multi-plane YCbCr 4:2:0 (YUV_420_888). */
    public static final int YUV_420_888      = 0x23;
    /** Multi-plane YCbCr 4:2:2 (YUV_422_888). */
    public static final int YUV_422_888      = 0x27;
    /** Multi-plane YCbCr 4:4:4 (YUV_444_888). */
    public static final int YUV_444_888      = 0x28;

    private ImageFormat() {}

    /**
     * Returns the bits per pixel for the given format, or -1 if unknown.
     */
    public static int getBitsPerPixel(int format) {
        switch (format) {
            case NV21:
            case NV16:
            case YUY2:
            case YUV_420_888:
                return 12;
            case YV12:
                return 12;
            case YUV_422_888:
                return 16;
            case YUV_444_888:
                return 24;
            case RGB_565:
            case DEPTH16:
                return 16;
            case FLEX_RGB_888:
                return 24;
            case FLEX_RGBA_8888:
                return 32;
            case RAW_SENSOR:
                return 16;
            case RAW10:
                return 10;
            case RAW12:
                return 12;
            case JPEG:
            case RAW_PRIVATE:
            case PRIVATE:
            case DEPTH_POINT_CLOUD:
            case UNKNOWN:
            default:
                return -1;
        }
    }
}
