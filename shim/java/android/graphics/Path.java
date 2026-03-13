package android.graphics;

/**
 * Shim: android.graphics.Path
 * OH mapping: drawing.OH_Drawing_Path
 *
 * Pure Java stub — tracks path state (empty/non-empty) without storing
 * actual segments.  Direction and FillType are preserved for round-trip
 * compatibility but have no rendering effect in this shim.
 */
public class Path {

    // ── FillType ─────────────────────────────────────────────────────────────

    public enum FillType {
        WINDING,
        EVEN_ODD,
        INVERSE_WINDING,
        INVERSE_EVEN_ODD
    }

    // ── Direction ────────────────────────────────────────────────────────────

    public enum Direction {
        CW,
        CCW
    }

    // ── State ────────────────────────────────────────────────────────────────

    private FillType fillType = FillType.WINDING;
    private boolean  empty    = true;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Path() {}

    public Path(Path src) {
        if (src != null) {
            this.fillType = src.fillType;
            this.empty    = src.empty;
        }
    }

    // ── FillType ─────────────────────────────────────────────────────────────

    public void     setFillType(FillType ft) { this.fillType = (ft != null) ? ft : FillType.WINDING; }
    public FillType getFillType()            { return fillType; }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isEmpty() { return empty; }

    public void reset() {
        fillType = FillType.WINDING;
        empty    = true;
    }

    // ── Path operations ──────────────────────────────────────────────────────

    public void moveTo(float x, float y)                                       { empty = false; }
    public void lineTo(float x, float y)                                       { empty = false; }
    public void quadTo(float x1, float y1, float x2, float y2)                { empty = false; }
    public void cubicTo(float x1, float y1, float x2, float y2,
                        float x3, float y3)                                    { empty = false; }
    public void close()                                                        { /* no-op */ }

    public void addRect(RectF rect, Direction dir)                             { empty = false; }
    public void addCircle(float x, float y, float radius, Direction dir)      { empty = false; }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Path(fillType=" + fillType + ", empty=" + empty + ")";
    }
}
