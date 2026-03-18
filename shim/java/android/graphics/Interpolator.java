package android.graphics;

public class Interpolator {
    public enum Result {
        NORMAL,
        FREEZE_START,
        FREEZE_END
    }

    public Interpolator() {}
    public Interpolator(int valueCount) {}
    public Interpolator(int valueCount, int frameCount) {}

    public int getKeyFrameCount() { return 0; }
    public int getValueCount() { return 0; }
    public void reset(Object p0) {}
    public void reset(Object p0, Object p1) {}
    public void setKeyFrame(Object p0, Object p1, Object p2) {}
    public void setKeyFrame(Object p0, Object p1, Object p2, Object p3) {}
    public void setRepeatMirror(Object p0, Object p1) {}
    public Object timeToValues(Object p0) { return null; }
    public Object timeToValues(Object p0, Object p1) { return null; }
}
