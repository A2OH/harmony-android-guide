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
