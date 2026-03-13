package android.view;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;

public final class PixelCopy {
    public static final int ERROR_DESTINATION_INVALID = 0;
    public static final int ERROR_SOURCE_INVALID = 0;
    public static final int ERROR_SOURCE_NO_DATA = 0;
    public static final int ERROR_TIMEOUT = 0;
    public static final int ERROR_UNKNOWN = 0;
    public static final int SUCCESS = 0;

    public PixelCopy() {}

    public static void request(SurfaceView p0, Bitmap p1, Object p2, Handler p3) {}
    public static void request(SurfaceView p0, Rect p1, Bitmap p2, Object p3, Handler p4) {}
}
