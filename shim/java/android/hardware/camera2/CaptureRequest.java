package android.hardware.camera2;

/**
 * Stub for android.hardware.camera2.CaptureRequest.
 * Maps to OpenHarmony camera framework capture settings.
 */
public class CaptureRequest {

    public static final Object CONTROL_MODE = "android.control.mode";
    public static final Object CONTROL_AE_MODE = "android.control.aeMode";
    public static final Object CONTROL_AF_MODE = "android.control.afMode";
    public static final Object CONTROL_AWB_MODE = "android.control.awbMode";
    public static final Object FLASH_MODE = "android.flash.mode";
    public static final Object JPEG_ORIENTATION = "android.jpeg.orientation";
    public static final Object JPEG_QUALITY = "android.jpeg.quality";
    public static final Object SCALER_CROP_REGION = "android.scaler.cropRegion";

    public CaptureRequest() {
    }

    public static class Builder {

        public Builder() {
        }

        public void addTarget(Object surface) {
            // no-op stub
        }

        public void set(Object key, Object value) {
            // no-op stub
        }

        public CaptureRequest build() {
            return new CaptureRequest();
        }
    }
}
