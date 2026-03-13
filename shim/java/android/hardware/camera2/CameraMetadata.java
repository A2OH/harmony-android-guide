package android.hardware.camera2;

/**
 * Android-compatible CameraMetadata stub.
 * Base class for camera metadata containers (CaptureRequest, CaptureResult, CameraCharacteristics).
 */
public class CameraMetadata {

    // -----------------------------------------------------------------------
    // CONTROL_MODE constants
    // -----------------------------------------------------------------------
    public static final int CONTROL_MODE_OFF              = 0;
    public static final int CONTROL_MODE_AUTO             = 1;
    public static final int CONTROL_MODE_USE_SCENE_MODE  = 2;
    public static final int CONTROL_MODE_OFF_KEEP_STATE  = 3;

    // -----------------------------------------------------------------------
    // CONTROL_AE_MODE constants
    // -----------------------------------------------------------------------
    public static final int CONTROL_AE_MODE_OFF                     = 0;
    public static final int CONTROL_AE_MODE_ON                      = 1;
    public static final int CONTROL_AE_MODE_ON_AUTO_FLASH           = 2;
    public static final int CONTROL_AE_MODE_ON_ALWAYS_FLASH         = 3;
    public static final int CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE    = 4;
    public static final int CONTROL_AE_MODE_ON_EXTERNAL_FLASH       = 5;

    // -----------------------------------------------------------------------
    // CONTROL_AF_MODE constants
    // -----------------------------------------------------------------------
    public static final int CONTROL_AF_MODE_OFF              = 0;
    public static final int CONTROL_AF_MODE_AUTO             = 1;
    public static final int CONTROL_AF_MODE_MACRO            = 2;
    public static final int CONTROL_AF_MODE_CONTINUOUS_VIDEO = 3;
    public static final int CONTROL_AF_MODE_CONTINUOUS_PICTURE = 4;
    public static final int CONTROL_AF_MODE_EDOF             = 5;

    // -----------------------------------------------------------------------
    // CONTROL_AWB_MODE constants
    // -----------------------------------------------------------------------
    public static final int CONTROL_AWB_MODE_OFF            = 0;
    public static final int CONTROL_AWB_MODE_AUTO           = 1;
    public static final int CONTROL_AWB_MODE_INCANDESCENT   = 2;
    public static final int CONTROL_AWB_MODE_FLUORESCENT    = 3;
    public static final int CONTROL_AWB_MODE_DAYLIGHT       = 5;
    public static final int CONTROL_AWB_MODE_CLOUDY_DAYLIGHT = 6;
    public static final int CONTROL_AWB_MODE_TWILIGHT       = 7;
    public static final int CONTROL_AWB_MODE_SHADE          = 8;

    // -----------------------------------------------------------------------
    // LENS_FACING constants
    // -----------------------------------------------------------------------
    public static final int LENS_FACING_FRONT    = 0;
    public static final int LENS_FACING_BACK     = 1;
    public static final int LENS_FACING_EXTERNAL = 2;

    // -----------------------------------------------------------------------
    // FLASH_MODE constants
    // -----------------------------------------------------------------------
    public static final int FLASH_MODE_OFF    = 0;
    public static final int FLASH_MODE_SINGLE = 1;
    public static final int FLASH_MODE_TORCH  = 2;

    // -----------------------------------------------------------------------
    // JPEG_QUALITY default
    // -----------------------------------------------------------------------
    public static final int JPEG_QUALITY_DEFAULT = 95;

    // -----------------------------------------------------------------------
    // INFO_SUPPORTED_HARDWARE_LEVEL constants
    // -----------------------------------------------------------------------
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY   = 2;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED  = 0;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_FULL     = 1;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_LEVEL_3  = 3;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL = 5;

    /** Prevent direct instantiation outside the camera2 package. */
    CameraMetadata() {}
}
