package android.widget;

/**
 * Android-compatible TimePicker shim. Stub — time selection is no-op.
 */
public class TimePicker extends FrameLayout {
    private int     mHour   = 0;
    private int     mMinute = 0;
    private boolean m24Hour = false;

    public TimePicker() {}
    public TimePicker(Object context) { super(context); }

    public int  getHour()   { return mHour; }
    public void setHour(int hour) { mHour = hour; }

    public int  getMinute()      { return mMinute; }
    public void setMinute(int minute) { mMinute = minute; }

    public boolean is24HourView()           { return m24Hour; }
    public void    setIs24HourView(boolean is24HourView) { m24Hour = is24HourView; }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {}

    public interface OnTimeChangedListener {
        void onTimeChanged(TimePicker view, int hourOfDay, int minute);
    }
}
