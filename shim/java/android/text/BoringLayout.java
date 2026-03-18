package android.text;

/**
 * Android-compatible BoringLayout shim.
 * Models a single-line, non-styled text layout. Extends Layout.
 */
public class BoringLayout extends Layout {

    /**
     * Metrics returned by {@link #isBoring}.
     */
    public static class Metrics {
        public int width;
        public int top;
        public int ascent;
        public int descent;
        public int bottom;
        public int leading;
    }

    private final Metrics mMetrics;

    public BoringLayout(CharSequence source, TextPaint paint, int outerWidth,
                        Alignment align, float spacingMult, float spacingAdd,
                        Metrics metrics, boolean includePad) {
        super(source, paint, outerWidth, align, spacingMult, spacingAdd);
        mMetrics = metrics != null ? metrics : new Metrics();
    }

    public BoringLayout(CharSequence source, TextPaint paint, int outerWidth,
                        Alignment align, float spacingMult, float spacingAdd,
                        Metrics metrics, boolean includePad, Object ellipsize,
                        int ellipsizedWidth) {
        this(source, paint, outerWidth, align, spacingMult, spacingAdd, metrics, includePad);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint) {
        return isBoring(text, paint, (Metrics) null);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, TextDirectionHeuristic textDir) {
        return isBoring(text, paint, textDir, null);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, TextDirectionHeuristic textDir, Metrics metrics) {
        return isBoring(text, paint, metrics);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, Metrics metrics) {
        if (text == null) return null;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n' || c == '\r') return null;
            if ((c >= '\u0590' && c <= '\u08FF') || (c >= '\uFB1D' && c <= '\uFDFF')
                    || (c >= '\uFE70' && c <= '\uFEFF')) {
                return null;
            }
        }
        if (metrics == null) metrics = new Metrics();
        if (paint != null) {
            metrics.width = (int) Math.ceil(paint.measureText(text.toString()));
            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            metrics.ascent  = (int) Math.ceil(fm.ascent);
            metrics.descent = (int) Math.ceil(fm.descent);
            metrics.top     = (int) Math.ceil(fm.top);
            metrics.bottom  = (int) Math.ceil(fm.bottom);
            metrics.leading = (int) Math.ceil(fm.leading);
        }
        return metrics;
    }

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerWidth,
                                    Alignment align, float spacingMult, float spacingAdd,
                                    Metrics metrics, boolean includePad) {
        return new BoringLayout(source, paint, outerWidth, align, spacingMult, spacingAdd,
                metrics, includePad);
    }

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerWidth,
                                    Alignment align, float spacingMult, float spacingAdd,
                                    Metrics metrics, boolean includePad, Object ellipsize,
                                    int ellipsizedWidth) {
        return new BoringLayout(source, paint, outerWidth, align, spacingMult, spacingAdd,
                metrics, includePad, ellipsize, ellipsizedWidth);
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerWidth,
                                      Alignment align, float spacingMult, float spacingAdd,
                                      Metrics metrics, boolean includePad) {
        return make(source, paint, outerWidth, align, spacingMult, spacingAdd,
                metrics, includePad);
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerWidth,
                                      Alignment align, float spacingMult, float spacingAdd,
                                      Metrics metrics, boolean includePad, Object ellipsize,
                                      int ellipsizedWidth) {
        return make(source, paint, outerWidth, align, spacingMult, spacingAdd,
                metrics, includePad, ellipsize, ellipsizedWidth);
    }

    // --- Layout abstract method implementations ---

    @Override public int getLineCount()           { return 1; }
    @Override public int getLineTop(int line)      { return line == 0 ? 0 : getHeight(); }
    @Override public int getLineStart(int line)    { return 0; }
    @Override public int getLineEnd(int line)      { return getText() != null ? getText().length() : 0; }

    @Override
    public int getHeight() {
        return mMetrics.bottom - mMetrics.top;
    }

    public int getEllipsisCount(int line) { return 0; }
    public int getEllipsisStart(int line) { return 0; }
}
