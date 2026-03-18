package android.widget;
import android.view.View;
import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.CheckBox → ARKUI_NODE_CHECKBOX
 *
 * AOSP: CheckBox extends CompoundButton (not Button directly).
 * CompoundButton extends Button → TextView → View.
 */
public class CheckBox extends CompoundButton {
    static final int NODE_TYPE_CHECKBOX = 15;
    static final int ATTR_CHECKBOX_SELECT = 15000;
    static final int ATTR_CHECKBOX_SELECT_COLOR = 15001;
    static final int EVENT_CHECKBOX_ON_CHANGE = 15000;

    private int checkEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(20000);

    public CheckBox() { super(new android.content.Context());
    }

    public CheckBox(android.content.Context context) {
        this();
    }

    public CheckBox(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public CheckBox(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    @Override
    public void setChecked(boolean checked) {
        boolean wasChecked = isChecked();
        super.setChecked(checked);
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_CHECKBOX_SELECT, checked ? 1 : 0);
        }
    }

    @Override
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(listener);
        // Register with ArkUI event bridge if native handle is live
        if (nativeHandle != 0 && listener != null && checkEventId == 0) {
            checkEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_CHECKBOX_ON_CHANGE, checkEventId);
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        boolean checked = isChecked();
        int boxSize = Math.min(getWidth(), getHeight());
        if (boxSize <= 0) boxSize = 20;
        int margin = 4;

        // Draw checkbox box
        android.graphics.Paint boxPaint = new android.graphics.Paint();
        boxPaint.setColor(checked ? 0xFF2196F3 : 0xFF757575);
        boxPaint.setStyle(checked ? android.graphics.Paint.Style.FILL : android.graphics.Paint.Style.STROKE);
        boxPaint.setStrokeWidth(2);
        canvas.drawRect(margin, margin, margin + boxSize - 2 * margin, margin + boxSize - 2 * margin, boxPaint);

        // Draw checkmark when checked
        if (checked) {
            android.graphics.Paint checkPaint = new android.graphics.Paint();
            checkPaint.setColor(0xFFFFFFFF);
            checkPaint.setStyle(android.graphics.Paint.Style.STROKE);
            checkPaint.setStrokeWidth(3);
            float cx = boxSize / 2f;
            float cy = boxSize / 2f;
            float s = boxSize / 4f;
            canvas.drawLine(cx - s, cy, cx - s / 3, cy + s * 0.7f, checkPaint);
            canvas.drawLine(cx - s / 3, cy + s * 0.7f, cx + s, cy - s * 0.6f, checkPaint);
        }

        // Draw label text to the right of the box
        CharSequence text = getText();
        if (text != null && text.length() > 0) {
            android.graphics.Paint textPaint = new android.graphics.Paint();
            textPaint.setColor(getCurrentTextColor() != 0 ? getCurrentTextColor() : 0xFF000000);
            float ts = getTextSize() > 0 ? getTextSize() : 14;
            textPaint.setTextSize(ts);
            textPaint.setStyle(android.graphics.Paint.Style.FILL);
            canvas.drawText(text.toString(), boxSize + margin * 2, (boxSize + ts) / 2, textPaint);
        }
    }

    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_CHECKBOX_ON_CHANGE) {
            toggle();
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }
}
