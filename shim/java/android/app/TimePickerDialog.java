package android.app;

import android.widget.TimePicker;

/**
 * Android-compatible TimePickerDialog shim. Stub — shows no UI.
 */
public class TimePickerDialog extends AlertDialog {
    private final TimePicker mTimePicker = new TimePicker();

    public TimePickerDialog(Object context, OnTimeSetListener listener,
                            int hourOfDay, int minute, boolean is24HourView) {
        super(context);
        mTimePicker.setHour(hourOfDay);
        mTimePicker.setMinute(minute);
        mTimePicker.setIs24HourView(is24HourView);
    }

    public TimePickerDialog(Object context, int themeResId, OnTimeSetListener listener,
                            int hourOfDay, int minute, boolean is24HourView) {
        super(context, themeResId);
        mTimePicker.setHour(hourOfDay);
        mTimePicker.setMinute(minute);
        mTimePicker.setIs24HourView(is24HourView);
    }

    public TimePicker getTimePicker() {
        return mTimePicker;
    }

    public interface OnTimeSetListener {
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }
}
