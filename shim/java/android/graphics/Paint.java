package android.graphics;

/**
 * Shim: android.graphics.Paint
 * OH mapping: drawing.OH_Drawing_Pen / OH_Drawing_Brush
 *
 * Pure Java stub — stores pa(int attributes; no actual rendering.
 */
public class Paint {

    // ── Flags ────────────────────────────────────────────────────────────────

    public static final int ANTI_ALIAS_FLAG    = 1;
    public static final int FILTER_BITMAP_FLAG = 2;
    public static final int FAKE_BOLD_TEXT_FLAG = 0x20;
    public static final int STRIKE_THRU_TEXT_FLAG = 0x10;
    public static final int UNDERLINE_TEXT_FLAG = 0x08;
    public static final int DEV_KERN_TEXT_FLAG = 0x100;
    public static final int SUBPIXEL_TEXT_FLAG = 0x80;
    public static final int LINEAR_TEXT_FLAG = 0x40;

    // ── Style ────────────────────────────────────────────────────────────────

    public enum Style {
        FILL,
        STROKE,
        FILL_AND_STROKE
    }

    // ── Cap ──────────────────────────────────────────────────────────────────

    public enum Cap {
        BUTT,   // 0 — OH_Drawing: LINE_FLAT_CAP
        ROUND,  // 1 — OH_Drawing: LINE_ROUND_CAP
        SQUARE  // 2 — OH_Drawing: LINE_SQUARE_CAP
    }

    // ── Join ─────────────────────────────────────────────────────────────────

    public enum Join {
        MITER,  // 0 — OH_Drawing: LINE_MITER_JOIN
        ROUND,  // 1 — OH_Drawing: LINE_ROUND_JOIN
        BEVEL   // 2 — OH_Drawing: LINE_BEVEL_JOIN
    }

    // ── Align ────────────────────────────────────────────────────────────────

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    // ── State ────────────────────────────────────────────────────────────────

    private int   flags;
    private int   color     = 0xFF000000; // opaque black
    private Style style     = Style.FILL;
    private float strokeWidth = 0f;
    private float textSize    = 12f;
    private Cap   strokeCap   = Cap.BUTT;
    private Join  strokeJoin  = Join.MITER;
    private Align textAlign   = Align.LEFT;
    private float strokeMiter = 4f;
    private boolean fakeBoldText = false;

    // ── Cached AWT FontMetrics (host JVM only) ─────────────────────────
    private transient Object cachedAwtMetrics;  // java.awt.FontMetrics
    private float cachedMetricsSize = -1;
    private int   cachedMetricsStyle = -1;

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
            this.strokeCap   = paint.strokeCap;
            this.strokeJoin  = paint.strokeJoin;
            this.textAlign   = paint.textAlign;
            this.strokeMiter = paint.strokeMiter;
            this.fakeBoldText = paint.fakeBoldText;
            this.mTypeface = paint.mTypeface;
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

    public void setStrokeCap(Cap cap) { this.strokeCap = (cap != null) ? cap : Cap.BUTT; }
    public Cap  getStrokeCap()        { return strokeCap; }

    public void setStrokeJoin(Join join) { this.strokeJoin = (join != null) ? join : Join.MITER; }
    public Join getStrokeJoin()          { return strokeJoin; }

    public void  setStrokeMiter(float miter) { this.strokeMiter = miter; }
    public float getStrokeMiter()            { return strokeMiter; }

    // ── Text ─────────────────────────────────────────────────────────────────

    public void  setTextSize(float size) { this.textSize = size; this.cachedAwtMetrics = null; }
    public float getTextSize()           { return textSize; }

    public void    setFakeBoldText(boolean bold) { this.fakeBoldText = bold; this.cachedAwtMetrics = null; }
    public boolean isFakeBoldText()              { return fakeBoldText; }

    public void  setTextAlign(Align align) { this.textAlign = (align != null) ? align : Align.LEFT; }
    public Align getTextAlign()            { return textAlign; }

    /**
     * Returns java.awt.Font style constant (PLAIN/BOLD), or 0 if AWT unavailable.
     */
    private int getAwtFontStyle() {
        int style = 0; // java.awt.Font.PLAIN
        if (fakeBoldText) style |= 1; // java.awt.Font.BOLD
        return style;
    }

    /**
     * Returns a cached java.awt.FontMetrics for the current text size and style,
     * or null if java.awt is not available (e.g. on Dalvik).
     */
    private Object getAwtFontMetrics() {
        int style = getAwtFontStyle();
        if (cachedAwtMetrics != null && cachedMetricsSize == textSize && cachedMetricsStyle == style) {
            return cachedAwtMetrics;
        }
        try {
            int sz = Math.max(1, (int) textSize);
            java.awt.Font f = new java.awt.Font("SansSerif", style, sz);
            java.awt.Graphics g = new java.awt.image.BufferedImage(1, 1,
                java.awt.image.BufferedImage.TYPE_INT_ARGB).getGraphics();
            cachedAwtMetrics = g.getFontMetrics(f);
            cachedMetricsSize = textSize;
            cachedMetricsStyle = style;
            return cachedAwtMetrics;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Measures text width using Java2D on host JVM, with fallback for Dalvik.
     */
    public float measureText(String text) {
        if (text == null || text.length() == 0) return 0f;
        try {
            Object obj = getAwtFontMetrics();
            if (obj != null) {
                java.awt.FontMetrics awtFm = (java.awt.FontMetrics) obj;
                return awtFm.stringWidth(text);
            }
        } catch (Throwable t) {
            // fall through
        }
        return text.length() * textSize * 0.6f;
    }

    public float measureText(char[] text, int index, int count) {
        if (text == null || count <= 0) return 0f;
        return measureText(new String(text, index, count));
    }

    public float measureText(String text, int start, int end) {
        if (text == null) return 0f;
        return measureText(text.substring(start, end));
    }

    /**
     * Fills bounds with the bounding rectangle of the text.
     */
    public void getTextBounds(String text, int start, int end, Rect bounds) {
        if (bounds == null) return;
        if (text == null || start >= end) {
            bounds.set(0, 0, 0, 0);
            return;
        }
        String sub = text.substring(start, end);
        float width = measureText(sub);
        FontMetrics fm = getFontMetrics();
        bounds.left = 0;
        bounds.top = (int) fm.ascent;
        bounds.right = (int) width;
        bounds.bottom = (int) fm.descent;
    }

    public void getTextBounds(char[] text, int index, int count, Rect bounds) {
        if (bounds == null) return;
        if (text == null || count <= 0) {
            bounds.set(0, 0, 0, 0);
            return;
        }
        getTextBounds(new String(text, index, count), 0, count, bounds);
    }

    // ── FontMetrics ──────────────────────────────────────────────────────────

    public static class FontMetrics {
        public float top;
        public float ascent;
        public float descent;
        public float bottom;
        public float leading;
    }

    public static class FontMetricsInt {
        public int top;
        public int ascent;
        public int descent;
        public int bottom;
        public int leading;
    }

    public FontMetrics getFontMetrics() {
        FontMetrics fm = new FontMetrics();
        try {
            Object obj = getAwtFontMetrics();
            if (obj != null) {
                java.awt.FontMetrics awtFm = (java.awt.FontMetrics) obj;
                fm.ascent  = -awtFm.getAscent();     // Android ascent is negative
                fm.descent = awtFm.getDescent();
                fm.leading = awtFm.getLeading();
                fm.top     = fm.ascent - 2;
                fm.bottom  = fm.descent + 1;
                return fm;
            }
        } catch (Throwable t) {
            // fall through to approximation
        }
        fm.top     = -textSize * 1.08f;
        fm.ascent  = -textSize * 0.93f;
        fm.descent =  textSize * 0.24f;
        fm.bottom  =  textSize * 0.28f;
        fm.leading = 0;
        return fm;
    }

    public float getFontMetrics(FontMetrics metrics) {
        if (metrics == null) metrics = new FontMetrics();
        FontMetrics real = getFontMetrics();
        metrics.top     = real.top;
        metrics.ascent  = real.ascent;
        metrics.descent = real.descent;
        metrics.bottom  = real.bottom;
        metrics.leading = real.leading;
        return metrics.descent - metrics.ascent;
    }

    public FontMetricsInt getFontMetricsInt() {
        FontMetricsInt fm = new FontMetricsInt();
        FontMetrics real = getFontMetrics();
        fm.top     = Math.round(real.top);
        fm.ascent  = Math.round(real.ascent);
        fm.descent = Math.round(real.descent);
        fm.bottom  = Math.round(real.bottom);
        fm.leading = Math.round(real.leading);
        return fm;
    }

    public float getFontSpacing() {
        FontMetrics fm = getFontMetrics();
        return fm.descent - fm.ascent + fm.leading;
    }

    // ── Anti-alias ───────────────────────────────────────────────────────────

    public void    setAntiAlias(boolean aa) {
        if (aa) flags |= ANTI_ALIAS_FLAG;
        else    flags &= ~ANTI_ALIAS_FLAG;
    }
    public boolean isAntiAlias() { return (flags & ANTI_ALIAS_FLAG) != 0; }

    // ── Typeface ─────────────────────────────────────────────────────────────

    private Typeface mTypeface;

    public Typeface setTypeface(Typeface typeface) {
        mTypeface = typeface;
        cachedAwtMetrics = null;
        return typeface;
    }
    public Typeface getTypeface() { return mTypeface; }

    // ── Flags ────────────────────────────────────────────────────────────────

    public void setFlags(int flags) { this.flags = flags; }
    public int  getFlags()          { return flags; }

    // ── Shadow layer ──────────────────────────────────────────────────────

    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) { /* no-op */ }

    // ── Shader/Xfermode/Dither ───────────────────────────────────────────
    public Shader setShader(Shader shader) { return shader; }
    public Shader getShader() { return null; }
    public Object setXfermode(Object xfermode) { return xfermode; }
    public void setDither(boolean dither) { /* no-op */ }
    public void setTextSkewX(float skewX) { /* no-op */ }

    // ── Text measurement (advanced) ──
    public int getFontMetricsInt(FontMetricsInt fmi) {
        FontMetricsInt src = getFontMetricsInt();
        if (fmi != null) {
            fmi.top = src.top;
            fmi.ascent = src.ascent;
            fmi.descent = src.descent;
            fmi.bottom = src.bottom;
            fmi.leading = src.leading;
        }
        return src.descent - src.ascent;
    }
    public int getTextWidths(char[] text, int index, int count, float[] widths) { return 0; }
    public int getTextWidths(CharSequence text, int start, int end, float[] widths) { return 0; }
    public float getTextRunAdvances(char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, float[] advances, int advancesIndex) { return 0; }
    public int getTextRunCursor(char[] text, int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt) { return offset; }

    // ── PorterDuffXfermode ──
    public PorterDuffXfermode setXfermode(PorterDuffXfermode xfermode) { return xfermode; }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Paint(color=0x" + Integer.toHexString(color)
             + ", style=" + style
             + ", strokeWidth=" + strokeWidth
             + ", textSize=" + textSize + ")";
    }
}
