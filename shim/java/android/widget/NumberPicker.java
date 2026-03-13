package android.widget;
import android.text.format.Formatter;
import android.text.format.Formatter;

/**
 * Android-compatible NumberPicker shim.
 * A widget that enables the user to select a number from a predefined range.
 * Extends LinearLayout (vertical by default).
 */
public class NumberPicker extends LinearLayout {
    private int minValue = 0;
    private int maxValue = 0;
    private int value    = 0;
    private String[] displayedValues;
    private boolean wrapSelectorWheel = true;
    private OnValueChangeListener onValueChangeListener;

    public NumberPicker() {
        super();
    }

    /**
     * Interface to listen for changes of the current value.
     */
    public interface OnValueChangeListener {
        /**
         * Called upon a change of the current value.
         *
         * @param picker The NumberPicker associated with this listener.
         * @param oldVal The previous value.
         * @param newVal The new value.
         */
        void onValueChange(NumberPicker picker, int oldVal, int newVal);
    }

    /**
     * Interface for formatting numbers displayed in the number picker.
     */
    public interface Formatter {
        /**
         * Formats a number as a string, e.g. "02" for 2.
         */
        String format(int value);
    }

    /** Sets the minimum value of the picker. */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
        if (this.value < minValue) this.value = minValue;
    }

    /** Returns the minimum value of the picker. */
    public int getMinValue() {
        return minValue;
    }

    /** Sets the maximum value of the picker. */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (this.value > maxValue) this.value = maxValue;
    }

    /** Returns the maximum value of the picker. */
    public int getMaxValue() {
        return maxValue;
    }

    /** Returns the current value of the picker. */
    public int getValue() {
        return value;
    }

    /** Sets the current value of the picker. */
    public void setValue(int value) {
        if (value < minValue) value = minValue;
        if (value > maxValue) value = maxValue;
        this.value = value;
    }

    /** Sets the listener to be notified on change of the current value. */
    public void setOnValueChangedListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    /**
     * Sets the values to be displayed instead of plain numbers in the selector.
     */
    public void setDisplayedValues(String[] displayedValues) {
        this.displayedValues = displayedValues;
    }

    /** Returns the values to be displayed instead of plain numbers. */
    public String[] getDisplayedValues() {
        return displayedValues;
    }

    /**
     * Sets whether the selector wheel wraps when reaching the min/max value.
     */
    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        this.wrapSelectorWheel = wrapSelectorWheel;
    }

    /** Returns whether the selector wheel wraps. */
    public boolean getWrapSelectorWheel() {
        return wrapSelectorWheel;
    }

    /**
     * Sets the speed at which the numbers be incremented and decremented when
     * the user holds the increment/decrement buttons. Stub — no-op.
     */
    public void setOnLongPressUpdateInterval(long intervalMillis) {}

    /**
     * Sets the formatter to be used for formatting the current value. Stub.
     */
    public void setFormatter(Formatter formatter) {}
}
