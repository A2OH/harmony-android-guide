package android.widget;
import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.RadioButton → ArkUI Radio node concept.
 *
 * AOSP: RadioButton extends CompoundButton (not View directly).
 * CompoundButton extends Button → TextView → View.
 */
public class RadioButton extends CompoundButton {

    // ArkUI attribute for checked state (approximate)
    private static final int ATTR_CHECKED = 1100;

    // Drawing constants
    private static final int OUTER_CIRCLE_COLOR = 0xFF757575;        // gray when unchecked
    private static final int OUTER_CIRCLE_CHECKED_COLOR = 0xFF1976D2; // blue when checked
    private static final int INNER_CIRCLE_COLOR = 0xFF1976D2;        // blue fill
    private static final int TEXT_COLOR = 0xFF212121;
    private static final float OUTER_RADIUS = 10f;
    private static final float INNER_RADIUS = 5f;
    private static final float CIRCLE_STROKE_WIDTH = 2f;
    private static final float CIRCLE_TEXT_GAP = 8f;

    public RadioButton() { super(new android.content.Context());
    }

    public RadioButton(android.content.Context context) {
        this();
    }

    public RadioButton(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public RadioButton(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    // ── Checked state (with OHBridge wiring) ──

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_CHECKED, checked ? 1 : 0);
        }
    }

    // ── Drawing ──

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        boolean checked = isChecked();
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        android.graphics.Paint paint = new android.graphics.Paint();

        // Circle center position
        float cx = getPaddingLeft() + OUTER_RADIUS + CIRCLE_STROKE_WIDTH;
        float cy = h / 2f;

        // Step 1: Draw outer circle (ring)
        paint.setColor(checked ? OUTER_CIRCLE_CHECKED_COLOR : OUTER_CIRCLE_COLOR);
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
        canvas.drawCircle(cx, cy, OUTER_RADIUS, paint);

        // Step 2: If checked, draw filled inner circle
        if (checked) {
            paint.setColor(INNER_CIRCLE_COLOR);
            paint.setStyle(android.graphics.Paint.Style.FILL);
            canvas.drawCircle(cx, cy, INNER_RADIUS, paint);
        }

        // Step 3: Draw label text to the right
        CharSequence label = getText();
        if (label != null && label.length() > 0) {
            float textSize = getTextSize() > 0 ? getTextSize() : 14f;
            paint.setColor(TEXT_COLOR);
            paint.setTextSize(textSize);
            paint.setStyle(android.graphics.Paint.Style.FILL);

            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            float textX = cx + OUTER_RADIUS + CIRCLE_STROKE_WIDTH + CIRCLE_TEXT_GAP;
            float textY = cy + (-fm.ascent - fm.descent) / 2f;
            canvas.drawText(label.toString(), textX, textY, paint);
        }
    }
}
