package android.widget;
import android.view.View;
import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.SeekBar → ARKUI_NODE_SLIDER
 */
public class SeekBar extends View {
    static final int NODE_TYPE_SLIDER = 26;
    static final int ATTR_SLIDER_VALUE = 26000;
    static final int ATTR_SLIDER_MIN = 26001;
    static final int ATTR_SLIDER_MAX = 26002;
    static final int ATTR_SLIDER_STEP = 26003;
    static final int EVENT_SLIDER_ON_CHANGE = 26000;

    // Drawing constants
    private static final int TRACK_COLOR = 0xFFBDBDBD;       // gray track
    private static final int PROGRESS_COLOR = 0xFF1976D2;    // blue progress
    private static final int THUMB_COLOR = 0xFF1976D2;       // blue thumb
    private static final float TRACK_HEIGHT = 4f;
    private static final float THUMB_RADIUS = 10f;

    private int max = 100;
    private int progress = 0;
    private OnSeekBarChangeListener onSeekBarChangeListener;
    private int sliderEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(40000);

    public SeekBar() {
        super(NODE_TYPE_SLIDER);
        registerSliderEvent();
    }

    private void registerSliderEvent() {
        if (nativeHandle != 0) {
            sliderEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_SLIDER_ON_CHANGE, sliderEventId);
        }
    }

    public int getMax() { return max; }

    public void setMax(int max) {
        this.max = max;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_SLIDER_MAX, (float) max, 0, 0, 0, 1);
        }
    }

    public int getProgress() { return progress; }

    public void setProgress(int progress) {
        this.progress = progress;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_SLIDER_VALUE, (float) progress, 0, 0, 0, 1);
        }
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.onSeekBarChangeListener = listener;
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setStyle(android.graphics.Paint.Style.FILL);

        float usableWidth = w - getPaddingLeft() - getPaddingRight() - THUMB_RADIUS * 2;
        float centerY = h / 2f;
        float trackLeft = getPaddingLeft() + THUMB_RADIUS;
        float trackRight = trackLeft + usableWidth;

        // Step 1: Draw track line (horizontal rect, full width)
        paint.setColor(TRACK_COLOR);
        canvas.drawRect(trackLeft, centerY - TRACK_HEIGHT / 2f,
            trackRight, centerY + TRACK_HEIGHT / 2f, paint);

        // Step 2: Draw progress fill (colored rect from left to progress position)
        float progressRatio = max > 0 ? (float) progress / max : 0f;
        float progressX = trackLeft + usableWidth * progressRatio;

        paint.setColor(PROGRESS_COLOR);
        canvas.drawRect(trackLeft, centerY - TRACK_HEIGHT / 2f,
            progressX, centerY + TRACK_HEIGHT / 2f, paint);

        // Step 3: Draw thumb (circle at progress position)
        paint.setColor(THUMB_COLOR);
        canvas.drawCircle(progressX, centerY, THUMB_RADIUS, paint);
    }

    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_SLIDER_ON_CHANGE && onSeekBarChangeListener != null) {
            // stringData or float data contains the new value
            // For now, parse from event data
            onSeekBarChangeListener.onProgressChanged(this, progress, true);
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
        default void onStartTrackingTouch(SeekBar seekBar) {}
        default void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
