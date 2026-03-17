package android.widget;

public class EditText extends TextView {
    private static final int NODE_TYPE_TEXT_INPUT = 7;
    private static final int ATTR_TEXT_INPUT_CONTENT = 7000;
    private static final int ATTR_TEXT_INPUT_HINT = 7002;

    // Cursor and underline colors
    private static final int CURSOR_COLOR = 0xFF1976D2;  // blue cursor
    private static final int UNDERLINE_COLOR = 0xFF757575; // gray underline
    private static final int UNDERLINE_FOCUSED_COLOR = 0xFF1976D2; // blue when focused
    private static final float CURSOR_WIDTH = 2f;
    private static final float UNDERLINE_HEIGHT = 2f;

    private boolean mCursorVisible = true;

    public EditText() {
        super(NODE_TYPE_TEXT_INPUT);
    }

    public EditText(android.content.Context context) {
        this();
    }

    public EditText(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public EditText(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    @Override
    public void setText(CharSequence text) {
        super.setText(text);
        if (nativeHandle != 0 && text != null) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrString(nativeHandle, ATTR_TEXT_INPUT_CONTENT, text.toString());
        }
    }

    @Override
    public void setHint(CharSequence hint) {
        super.setHint(hint);
        if (nativeHandle != 0 && hint != null) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrString(nativeHandle, ATTR_TEXT_INPUT_HINT, hint.toString());
        }
    }

    @Override
    public CharSequence getText() { return super.getText(); }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        // Step 1: Draw text content (inherited from TextView)
        super.onDraw(canvas);

        float textSize = getTextSize() > 0 ? getTextSize() : 14f;
        android.graphics.Paint paint = new android.graphics.Paint();

        // Step 2: Draw cursor line (thin rect at text end position)
        if (mCursorVisible) {
            CharSequence text = super.getText();
            float cursorX = getPaddingLeft();
            if (text != null && text.length() > 0) {
                // Estimate cursor position based on text width
                paint.setTextSize(textSize);
                cursorX += paint.measureText(text.toString());
            }

            paint.setColor(CURSOR_COLOR);
            paint.setStyle(android.graphics.Paint.Style.FILL);

            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            float cursorTop = getPaddingTop();
            float cursorBottom = getPaddingTop() + textSize * 1.2f;

            canvas.drawRect(cursorX, cursorTop, cursorX + CURSOR_WIDTH,
                cursorBottom, paint);
        }

        // Step 3: Draw underline decoration (bottom border line)
        int w = getWidth();
        int h = getHeight();
        if (w > 0 && h > 0) {
            paint.setColor(isFocused() ? UNDERLINE_FOCUSED_COLOR : UNDERLINE_COLOR);
            paint.setStyle(android.graphics.Paint.Style.FILL);
            float underlineY = h - getPaddingBottom() - UNDERLINE_HEIGHT;
            canvas.drawRect(getPaddingLeft(), underlineY,
                w - getPaddingRight(), underlineY + UNDERLINE_HEIGHT, paint);
        }
    }

    public void setCursorVisible(boolean visible) {
        mCursorVisible = visible;
    }

    public void extendSelection(Object p0) {}
    public void selectAll() {}
    public void setSelection(Object p0, Object p1) {}
    public void setSelection(Object p0) {}
}
