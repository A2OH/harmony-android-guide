package android.hardware.camera2;
import android.util.AndroidException;
import android.util.AndroidException;

public class CameraAccessException extends AndroidException {
    public static final int CAMERA_DISABLED = 0;
    public static final int CAMERA_DISCONNECTED = 0;
    public static final int CAMERA_ERROR = 0;
    public static final int CAMERA_IN_USE = 0;
    public static final int MAX_CAMERAS_IN_USE = 0;

    public CameraAccessException(int p0) {}
    public CameraAccessException(int p0, String p1) {}
    public CameraAccessException(int p0, String p1, Throwable p2) {}
    public CameraAccessException(int p0, Throwable p1) {}

    public int getReason() { return 0; }
}
