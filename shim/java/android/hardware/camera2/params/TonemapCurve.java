package android.hardware.camera2.params;
import android.graphics.PointF;
import android.graphics.PointF;

public final class TonemapCurve {
    public static final int CHANNEL_BLUE = 0;
    public static final int CHANNEL_GREEN = 0;
    public static final int CHANNEL_RED = 0;
    public static final int LEVEL_BLACK = 0;
    public static final int LEVEL_WHITE = 0;
    public static final int POINT_SIZE = 0;

    public TonemapCurve(float[] p0, float[] p1, float[] p2) {}

    public void copyColorCurve(int p0, float[] p1, int p2) {}
    public PointF getPoint(int p0, int p1) { return null; }
    public int getPointCount(int p0) { return 0; }
}
