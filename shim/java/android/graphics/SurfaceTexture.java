package android.graphics;
import android.os.Handler;

public class SurfaceTexture {
    public SurfaceTexture(int p0) {}
    public SurfaceTexture(int p0, boolean p1) {}
    public SurfaceTexture(boolean p0) {}

    public void attachToGLContext(int p0) {}
    public void detachFromGLContext() {}
    public long getTimestamp() { return 0L; }
    public void getTransformMatrix(float[] p0) {}
    public boolean isReleased() { return false; }
    public void release() {}
    public void releaseTexImage() {}
    public void setDefaultBufferSize(int p0, int p1) {}
    public void setOnFrameAvailableListener(Object p0) {}
    public void setOnFrameAvailableListener(Object p0, Handler p1) {}
    public void updateTexImage() {}
}
