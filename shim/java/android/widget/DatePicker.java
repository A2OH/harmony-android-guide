package android.widget;

/**
 * Android-compatible DatePicker shim. Stub — date selection is no-op.
 */
public class DatePicker extends FrameLayout {
    private int mYear  = 1970;
    private int mMonth = 0;
    private int mDay   = 1;
    private long mMinDate = Long.MIN_VALUE;
    private long mMaxDate = Long.MAX_VALUE;

    public DatePicker() {}
    public DatePicker(Object context) { super(context); }

    public void init(int year, int monthOfYear, int dayOfMonth,
                     OnDateChangedListener onDateChangedListener) {
        mYear  = year;
        mMonth = monthOfYear;
        mDay   = dayOfMonth;
    }

    public int getYear()       { return mYear; }
    public int getMonth()      { return mMonth; }
    public int getDayOfMonth() { return mDay; }

    public void updateDate(int year, int month, int dayOfMonth) {
        mYear  = year;
        mMonth = month;
        mDay   = dayOfMonth;
    }

    public void setMinDate(long minDate) { mMinDate = minDate; }
    public void setMaxDate(long maxDate) { mMaxDate = maxDate; }
    public long getMinDate() { return mMinDate; }
    public long getMaxDate() { return mMaxDate; }

    public interface OnDateChangedListener {
        void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }
}
