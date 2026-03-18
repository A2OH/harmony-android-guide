package android.widget;
import android.content.Context;
import android.util.AttributeSet;

public class CheckedTextView extends TextView implements Checkable {
    private boolean checked;
    private int checkMarkResource;

    public CheckedTextView(Context context) { super(context); }
    public CheckedTextView(Context context, AttributeSet attrs) { super(context, attrs); }
    public CheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override public void setChecked(boolean checked) { this.checked = checked; }
    @Override public boolean isChecked() { return checked; }
    @Override public void toggle() { setChecked(!checked); }
    public void setCheckMarkDrawable(int resId) { this.checkMarkResource = resId; }
    public void setCheckMarkDrawable(Object drawable) {}
    public int getCheckMarkDrawableResId() { return checkMarkResource; }
}
