package android.widget;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Shim: android.widget.ToggleButton
 */
public class ToggleButton extends CompoundButton {
    protected long nativeHandle;

    private CharSequence textOn = "ON";
    private CharSequence textOff = "OFF";

    public ToggleButton(Context context) { super(context); }
    public ToggleButton(Context context, AttributeSet attrs) { super(context, attrs); }
    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public CharSequence getTextOn() { return textOn; }
    public void setTextOn(CharSequence textOn) { this.textOn = textOn != null ? textOn : "ON"; }
    public CharSequence getTextOff() { return textOff; }
    public void setTextOff(CharSequence textOff) { this.textOff = textOff != null ? textOff : "OFF"; }
}
