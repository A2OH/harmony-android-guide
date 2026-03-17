package android.widget;
import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.RadioButton → ArkUI Radio node concept.
 *
 * ArkUI provides a Radio component; we map checked state through a generic
 * toggle attribute on a STACK node (the bridge can specialise the node type).
 */
public class RadioButton extends View {

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

    private boolean checked = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    private CharSequence mLabel = "";

    public RadioButton() {
        super(0); // Bridge maps to Radio component
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

    // ── Checked state ──

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (nativeHandle != 0) {
                OHBridge.nodeSetAttrInt(nativeHandle, ATTR_CHECKED, checked ? 1 : 0);
            }
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
    }

    public void toggle() {
        setChecked(!checked);
    }

    // ── Label text ──

    public void setText(CharSequence text) { mLabel = text != null ? text : ""; }
    public CharSequence getText() { return mLabel; }

    // ── Drawing ──

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
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
        if (mLabel != null && mLabel.length() > 0) {
            float textSize = 14f; // default text size
            paint.setColor(TEXT_COLOR);
            paint.setTextSize(textSize);
            paint.setStyle(android.graphics.Paint.Style.FILL);

            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            float textX = cx + OUTER_RADIUS + CIRCLE_STROKE_WIDTH + CIRCLE_TEXT_GAP;
            float textY = cy + (-fm.ascent - fm.descent) / 2f;
            canvas.drawText(mLabel.toString(), textX, textY, paint);
        }
    }

    // ── Listener ──

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    // ── Interface ──

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioButton button, boolean isChecked);
    }
}
