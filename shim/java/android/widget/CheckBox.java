package android.widget;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Shim: android.widget.CheckBox
 */
public class CheckBox extends CompoundButton {
    protected long nativeHandle;

    public CheckBox(Context context) { super(context); }
    public CheckBox(Context context, AttributeSet attrs) { super(context, attrs); }
    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }
}
