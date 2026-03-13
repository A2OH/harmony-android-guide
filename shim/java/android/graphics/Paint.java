package android.graphics;

/**
 * Shim: android.graphics.Paint
 * OH mapping: drawing.OH_Drawing_Pen / OH_Drawing_Brush
 *
 * Pure Java stub — stores paint attributes; no actual rendering.
 */
public class Paint {

    // ── Flags ────────────────────────────────────────────────────────────────

    public static final int ANTI_ALIAS_FLAG    = 1;
    public static final int FILTER_BITMAP_FLAG = 2;

    // ── Style ────────────────────────────────────────────────────────────────

    public enum Style {
        FILL,
        STROKE,
        FILL_AND_STROKE
    }

    // ── State ────────────────────────────────────────────────────────────────

    private int   flags;
    private int   color     = 0xFF000000; // opaque black
    private Style style     = Style.FILL;
    private float strokeWidth = 0f;
    private float textSize    = 12f;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Paint() {
        this.flags = 0;
    }

    public Paint(int flags) {
        this.flags = flags;
    }

    public Paint(Paint paint) {
        if (paint != null) {
            this.flags       = paint.flags;
            this.color       = paint.color;
            this.style       = paint.style;
            this.strokeWidth = paint.strokeWidth;
            this.textSize    = paint.textSize;
        }
    }

    // ── Color ────────────────────────────────────────────────────────────────

    public void setColor(int color) { this.color = color; }
    public int  getColor()          { return color; }

    public void setAlpha(int alpha) {
        color = (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public int getAlpha() {
        return (color >>> 24);
    }

    // ── Style ────────────────────────────────────────────────────────────────

    public void  setStyle(Style style) { this.style = (style != null) ? style : Style.FILL; }
    public Style getStyle()            { return style; }

    // ── Stroke ───────────────────────────────────────────────────────────────

    public void  setStrokeWidth(float width) { this.strokeWidth = width; }
    public float getStrokeWidth()            { return strokeWidth; }

    // ── Text ─────────────────────────────────────────────────────────────────

    public void  setTextSize(float size) { this.textSize = size; }
    public float getTextSize()           { return textSize; }

    /**
     * Rough width estimate: each character is ~0.6 em wide.
     */
    public float measureText(String text) {
        if (text == null) return 0f;
        return text.length() * textSize * 0.6f;
    }

    // ── Anti-alias ───────────────────────────────────────────────────────────

    public void    setAntiAlias(boolean aa) {
        if (aa) flags |= ANTI_ALIAS_FLAG;
        else    flags &= ~ANTI_ALIAS_FLAG;
    }
    public boolean isAntiAlias() { return (flags & ANTI_ALIAS_FLAG) != 0; }

    // ── Flags ────────────────────────────────────────────────────────────────

    public void setFlags(int flags) { this.flags = flags; }
    public int  getFlags()          { return flags; }

    // ── Shadow layer ──────────────────────────────────────────────────────

    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Paint(color=0x" + Integer.toHexString(color)
             + ", style=" + style
             + ", strokeWidth=" + strokeWidth
             + ", textSize=" + textSize + ")";
    }
}
