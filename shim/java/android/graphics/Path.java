package android.graphics;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.graphics.Path
 * OH mapping: drawing.OH_Drawing_Path
 *
 * Wraps a native OH_Drawing_Path handle. Path operations are forwarded
 * to OHBridge while also tracking empty/non-empty state locally.
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
    private long nativeHandle;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Path() {
        nativeHandle = createNativePathIfAvailable();
    }

    public Path(Path src) {
        nativeHandle = createNativePathIfAvailable();
        if (src != null) {
            this.fillType = src.fillType;
            this.empty    = src.empty;
        }
    }

    private static long createNativePathIfAvailable() {
        try {
            if (!OHBridge.strictGuestPing()) {
                return 0;
            }
            return OHBridge.pathCreate();
        } catch (Throwable ignored) {
            return 0;
        }
    }

    // ── Native handle ────────────────────────────────────────────────────────

    public long getNativeHandle() { return nativeHandle; }

    // ── FillType ─────────────────────────────────────────────────────────────

    public void     setFillType(FillType ft) { this.fillType = (ft != null) ? ft : FillType.WINDING; }
    public FillType getFillType()            { return fillType; }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isEmpty() { return empty; }

    public void reset() {
        fillType = FillType.WINDING;
        empty    = true;
        if (nativeHandle != 0) OHBridge.pathReset(nativeHandle);
    }

    // ── Path operations ──────────────────────────────────────────────────────

    public void moveTo(float x, float y) {
        empty = false;
        if (nativeHandle != 0) OHBridge.pathMoveTo(nativeHandle, x, y);
    }

    public void lineTo(float x, float y) {
        empty = false;
        if (nativeHandle != 0) OHBridge.pathLineTo(nativeHandle, x, y);
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        empty = false;
        if (nativeHandle != 0) OHBridge.pathQuadTo(nativeHandle, x1, y1, x2, y2);
    }

    public void cubicTo(float x1, float y1, float x2, float y2,
                        float x3, float y3) {
        empty = false;
        if (nativeHandle != 0) OHBridge.pathCubicTo(nativeHandle, x1, y1, x2, y2, x3, y3);
    }

    public void close() {
        if (nativeHandle != 0) OHBridge.pathClose(nativeHandle);
    }

    public void addRect(RectF rect, Direction dir) {
        if (rect != null) {
            empty = false;
            if (nativeHandle != 0) OHBridge.pathAddRect(nativeHandle, rect.left, rect.top, rect.right, rect.bottom, dir == Direction.CCW ? 1 : 0);
        }
    }

    public void addRect(float left, float top, float right, float bottom, Direction dir) {
        empty = false;
        if (nativeHandle != 0) OHBridge.pathAddRect(nativeHandle, left, top, right, bottom, dir == Direction.CCW ? 1 : 0);
    }

    public void addRoundRect(RectF rect, float[] radii, Direction dir) {
        if (rect != null) {
            empty = false;
            // Approximate: draw as rect (rounded corners not supported in stub)
            addRect(rect, dir);
        }
    }

    public void addRoundRect(RectF rect, float rx, float ry, Direction dir) {
        addRoundRect(rect, new float[]{rx, ry, rx, ry, rx, ry, rx, ry}, dir);
    }

    public void addRoundRect(float left, float top, float right, float bottom, float[] radii, Direction dir) {
        addRoundRect(new RectF(left, top, right, bottom), radii, dir);
    }

    public void arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {
        empty = false;
        // stub: no native arc support, just mark non-empty
    }

    public void arcTo(RectF oval, float startAngle, float sweepAngle) {
        arcTo(oval, startAngle, sweepAngle, false);
    }

    public void addOval(RectF oval, Direction dir) {
        empty = false;
        // stub: approximate as rect
        if (oval != null) addRect(oval, dir);
    }

    public void addCircle(float x, float y, float radius, Direction dir) {
        empty = false;
        if (nativeHandle != 0) OHBridge.pathAddCircle(nativeHandle, x, y, radius, dir == Direction.CCW ? 1 : 0);
    }

    // ── Cleanup ──────────────────────────────────────────────────────────────

    public void release() {
        if (nativeHandle != 0) { OHBridge.pathDestroy(nativeHandle); nativeHandle = 0; }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    public void computeBounds(RectF bounds, boolean exact) {
        if (bounds != null) bounds.set(0, 0, 0, 0);
    }

    @Override
    public String toString() {
        return "Path(fillType=" + fillType + ", empty=" + empty + ")";
    }
}
