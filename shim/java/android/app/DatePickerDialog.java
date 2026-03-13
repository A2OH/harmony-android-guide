package android.app;

import android.widget.DatePicker;

/**
 * Android-compatible DatePickerDialog shim. Stub — shows no UI.
 */
public class DatePickerDialog extends AlertDialog {
    private final DatePicker mDatePicker = new DatePicker();

    public DatePickerDialog(Object context, OnDateSetListener listener,
                            int year, int month, int dayOfMonth) {
        super(context);
        mDatePicker.init(year, month, dayOfMonth, null);
    }

    public DatePickerDialog(Object context, int themeResId, OnDateSetListener listener,
                            int year, int month, int dayOfMonth) {
        super(context, themeResId);
        mDatePicker.init(year, month, dayOfMonth, null);
    }

    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    public void updateDate(int year, int month, int dayOfMonth) {
        mDatePicker.updateDate(year, month, dayOfMonth);
    }

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }
}
