package android.media.projection;

/**
 * Android-compatible MediaProjectionManager shim. Stub for screen capture/projection.
 * android.content.Intent is not yet shimmed; methods use Object where Intent would appear.
 */
public class MediaProjectionManager {

    /**
     * Returns a stub Intent-like Object that can be passed to startActivityForResult.
     * Callers should treat the return value as an opaque token.
     */
    public Object createScreenCaptureIntent() {
        return new Object();
    }

    /**
     * Returns a MediaProjection from a successful screen-capture result.
     *
     * @param resultCode Activity.RESULT_OK
     * @param resultData  Intent (as Object) returned by the system dialog
     */
    public MediaProjection getMediaProjection(int resultCode, Object resultData) {
        return new MediaProjection();
    }
}
