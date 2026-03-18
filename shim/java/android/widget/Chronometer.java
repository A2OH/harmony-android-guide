package android.widget;
import android.content.Context;
import android.util.AttributeSet;

public class Chronometer extends TextView {
    private long mBase = 0L;
    private String mFormat = null;

    public Chronometer(Context context) { super(context); }
    public Chronometer(Context context, AttributeSet attrs) { super(context, attrs); }
    public Chronometer(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public void setBase(long base) { mBase = base; }
    public long getBase() { return mBase; }
    public void start() {}
    public void stop() {}
    public void setFormat(String format) { mFormat = format; }
    public String getFormat() { return mFormat; }
    public void setOnChronometerTickListener(OnChronometerTickListener listener) {}

    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
    }
}
