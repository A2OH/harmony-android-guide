package android.widget;

/**
 * Android-compatible CalendarView shim. Stub — calendar display is no-op.
 */
public class CalendarView extends FrameLayout {
    private long mDate    = 0L;
    private long mMinDate = Long.MIN_VALUE;
    private long mMaxDate = Long.MAX_VALUE;

    public CalendarView() { super(new android.content.Context()); }
    public CalendarView(android.content.Context context) { super(context); }

    public long getDate() { return mDate; }

    public void setDate(long date) {
        mDate = date;
    }

    public void setDate(long date, boolean animate, boolean center) {
        mDate = date;
    }

    public void setMinDate(long minDate) { mMinDate = minDate; }
    public void setMaxDate(long maxDate) { mMaxDate = maxDate; }
    public long getMinDate() { return mMinDate; }
    public long getMaxDate() { return mMaxDate; }

    public void setOnDateChangeListener(OnDateChangeListener listener) {}

    public interface OnDateChangeListener {
        void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth);
    }
}
