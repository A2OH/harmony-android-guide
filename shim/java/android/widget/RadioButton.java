package android.widget;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Shim: android.widget.RadioButton
 */
public class RadioButton extends CompoundButton {
    protected long nativeHandle;

    public RadioButton(Context context) { super(context); }
    public RadioButton(Context context, AttributeSet attrs) { super(context, attrs); }
    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }
}
