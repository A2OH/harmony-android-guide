package android.widget;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Shim: android.widget.Switch
 */
public class Switch extends CompoundButton {
    protected long nativeHandle;

    private CharSequence textOn = "ON";
    private CharSequence textOff = "OFF";

    public Switch(Context context) { super(context); }
    public Switch(Context context, AttributeSet attrs) { super(context, attrs); }
    public Switch(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public Switch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public void setTextOn(CharSequence text) { textOn = text; }
    public CharSequence getTextOn() { return textOn; }
    public void setTextOff(CharSequence text) { textOff = text; }
    public CharSequence getTextOff() { return textOff; }
}
