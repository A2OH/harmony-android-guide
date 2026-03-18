package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class AnalogClock extends View {
    private String timeZone = null;

    public AnalogClock(Context context) { super(context); }
    public AnalogClock(Context context, AttributeSet attrs) { super(context, attrs); }
    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    public String getTimeZone() { return timeZone; }
}
