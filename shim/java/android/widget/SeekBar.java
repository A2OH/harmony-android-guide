package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Shim: android.widget.SeekBar
 */
public class SeekBar extends ProgressBar {

    private OnSeekBarChangeListener onSeekBarChangeListener;

    public SeekBar(Context context) { super(context); }
    public SeekBar(Context context, AttributeSet attrs) { super(context, attrs); }
    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.onSeekBarChangeListener = listener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
        default void onStartTrackingTouch(SeekBar seekBar) {}
        default void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
