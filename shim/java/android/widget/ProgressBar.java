package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Shim: android.widget.ProgressBar
 */
public class ProgressBar extends View {
    protected long nativeHandle;

    private int max = 100;
    private int progress = 0;
    private boolean indeterminate = false;

    public ProgressBar(Context context) { super(context); }
    public ProgressBar(Context context, AttributeSet attrs) { super(context, attrs); }
    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setIndeterminate(boolean indeterminate) { this.indeterminate = indeterminate; }
    public boolean isIndeterminate() { return indeterminate; }
    public void incrementProgressBy(int diff) { setProgress(progress + diff); }
}
