package android.widget;

/**
 * Android-compatible Chronometer shim. Stub — timer is no-op.
 */
public class Chronometer extends TextView {
    private long   mBase   = 0L;
    private String mFormat = null;

    public Chronometer() {}
    public Chronometer(Object context) { super(); }

    public void setBase(long base) { mBase = base; }
    public long getBase()          { return mBase; }

    public void start() {}
    public void stop() {}

    public void setFormat(String format) { mFormat = format; }
    public String getFormat()            { return mFormat; }

    public void setOnChronometerTickListener(OnChronometerTickListener listener) {}

    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
    }
}
