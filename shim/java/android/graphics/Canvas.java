package android.graphics;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.graphics.Canvas
 * OH mapping: drawing.OH_Drawing_Canvas
 *
 * Routes draw calls through OHBridge to OH_Drawing native API.
 * In mock/JVM testing, OHBridge records draw calls in-memory.
 */
public class Canvas {

    private final Bitmap bitmap;
    // AOSP Canvas save count starts at 1 (initial state counts as one save)
    private int saveDepth = 1;

    // Native handles
    private long nativeCanvas;
    private long nativePenCache;
    private long nativeBrushCache;
    private long nativeFontCache;

    // Surface-backed canvas dimensions (when bitmap is null)
    private int surfaceWidth;
    private int surfaceHeight;
    private boolean ownedCanvas = true; // false for surface-wrapped canvases

    // ── Constructors ─────────────────────────────────────────────────────────

    public Canvas() {
        this.bitmap = null;
    }

    public Canvas(Bitmap bitmap) {
        if (bitmap == null) throw new NullPointerException("bitmap must not be null");
        this.bitmap = bitmap;
        this.nativeCanvas = OHBridge.canvasCreate(bitmap.getNativeHandle());
    }

    /**
     * Wrap an existing native OH_Drawing_Canvas handle (e.g. from a surface).
     * The canvas is NOT owned by this wrapper — release() won't destroy it.
     */
    public Canvas(long nativeCanvasHandle, int width, int height) {
        this.bitmap = null;
        this.nativeCanvas = nativeCanvasHandle;
        this.surfaceWidth = width;
        this.surfaceHeight = height;
        this.ownedCanvas = false;
    }

    // ── Dimensions ───────────────────────────────────────────────────────────

    public int getWidth()  { return (bitmap != null) ? bitmap.getWidth()  : surfaceWidth; }
    public int getHeight() { return (bitmap != null) ? bitmap.getHeight() : surfaceHeight; }
    public int getDensity() { return 160; }
    public void setDensity(int density) { /* no-op */ }

    // ── Native handle access ─────────────────────────────────────────────────

    public long getNativeHandle() { return nativeCanvas; }

    // ── Pen/Brush/Font sync from Paint ───────────────────────────────────────

    private long ensurePen(Paint paint) {
        if (nativePenCache == 0) nativePenCache = OHBridge.penCreate();
        if (paint != null) {
            OHBridge.penSetColor(nativePenCache, paint.getColor());
            OHBridge.penSetWidth(nativePenCache, paint.getStrokeWidth());
            OHBridge.penSetAntiAlias(nativePenCache, paint.isAntiAlias());
            OHBridge.penSetCap(nativePenCache, paint.getStrokeCap().ordinal());
            OHBridge.penSetJoin(nativePenCache, paint.getStrokeJoin().ordinal());
        }
        return nativePenCache;
    }

    private long ensureBrush(Paint paint) {
        if (nativeBrushCache == 0) nativeBrushCache = OHBridge.brushCreate();
        if (paint != null) {
            OHBridge.brushSetColor(nativeBrushCache, paint.getColor());
        }
        return nativeBrushCache;
    }

    private long ensureFont(Paint paint) {
        if (nativeFontCache == 0) nativeFontCache = OHBridge.fontCreate();
        if (paint != null) {
            OHBridge.fontSetSize(nativeFontCache, paint.getTextSize());
        }
        return nativeFontCache;
    }

    private long penFor(Paint paint) {
        if (paint == null) return 0;
        Paint.Style s = paint.getStyle();
        return (s != Paint.Style.FILL) ? ensurePen(paint) : 0;
    }

    private long brushFor(Paint paint) {
        if (paint == null) return 0;
        Paint.Style s = paint.getStyle();
        return (s != Paint.Style.STROKE) ? ensureBrush(paint) : 0;
    }

    // ── Draw operations ──────────────────────────────────────────────────────

    public void drawColor(int color) {
        if (nativeCanvas != 0) OHBridge.canvasDrawColor(nativeCanvas, color);
    }

    public void drawColor(int color, PorterDuff.Mode mode) {
        drawColor(color);
    }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        if (nativeCanvas != 0) {
            OHBridge.canvasDrawRect(nativeCanvas, left, top, right, bottom, penFor(paint), brushFor(paint));
        }
    }

    public void drawRect(Rect r, Paint paint) {
        if (r != null) drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    public void drawRect(RectF r, Paint paint) {
        if (r != null) drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        if (nativeCanvas != 0) {
            OHBridge.canvasDrawCircle(nativeCanvas, cx, cy, radius, penFor(paint), brushFor(paint));
        }
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        if (nativeCanvas != 0) {
            OHBridge.canvasDrawLine(nativeCanvas, startX, startY, stopX, stopY, ensurePen(paint));
        }
    }

    public void drawText(String text, float x, float y, Paint paint) {
        if (nativeCanvas != 0 && text != null) {
            OHBridge.canvasDrawText(nativeCanvas, text, x, y, ensureFont(paint), penFor(paint), brushFor(paint));
        }
    }

    public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
        if (text == null) return;
        drawText(text.toString().substring(start, end), x, y, paint);
    }

    public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
        if (text == null) return;
        drawText(new String(text, index, count), x, y, paint);
    }

    public void drawText(String text, int start, int end, float x, float y, Paint paint) {
        if (text == null) return;
        drawText(text.substring(start, end), x, y, paint);
    }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        if (nativeCanvas != 0 && bitmap != null) {
            OHBridge.canvasDrawBitmap(nativeCanvas, bitmap.getNativeHandle(), left, top);
        }
    }

    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        if (nativeCanvas == 0 || bitmap == null || dst == null) return;
        // Save, translate+scale to map src→dst, draw at origin, restore
        save();
        translate(dst.left, dst.top);
        float dstW = dst.width();
        float dstH = dst.height();
        float srcW = (src != null) ? src.width() : bitmap.getWidth();
        float srcH = (src != null) ? src.height() : bitmap.getHeight();
        if (srcW > 0 && srcH > 0) {
            scale(dstW / srcW, dstH / srcH);
        }
        float srcX = (src != null) ? -src.left : 0;
        float srcY = (src != null) ? -src.top : 0;
        OHBridge.canvasDrawBitmap(nativeCanvas, bitmap.getNativeHandle(), srcX, srcY);
        restore();
    }

    public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
        if (dst == null) return;
        drawBitmap(bitmap, src, new Rect((int) dst.left, (int) dst.top, (int) dst.right, (int) dst.bottom), paint);
    }

    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        if (nativeCanvas == 0 || bitmap == null || matrix == null) return;
        save();
        concat(matrix);
        OHBridge.canvasDrawBitmap(nativeCanvas, bitmap.getNativeHandle(), 0, 0);
        restore();
    }

    public void drawRoundRect(float left, float top, float right, float bottom,
                              float rx, float ry, Paint paint) {
        if (nativeCanvas != 0) {
            OHBridge.canvasDrawRoundRect(nativeCanvas, left, top, right, bottom, rx, ry, penFor(paint), brushFor(paint));
        }
    }

    public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
        if (rect != null) drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, rx, ry, paint);
    }

    public void drawArc(float left, float top, float right, float bottom,
                        float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        if (nativeCanvas != 0) {
            OHBridge.canvasDrawArc(nativeCanvas, left, top, right, bottom, startAngle, sweepAngle, useCenter, penFor(paint), brushFor(paint));
        }
    }
    public void drawArc(RectF oval, float startAngle, float sweepAngle,
                        boolean useCenter, Paint paint) {
        if (oval != null) drawArc(oval.left, oval.top, oval.right, oval.bottom, startAngle, sweepAngle, useCenter, paint);
    }

    public void drawOval(float left, float top, float right, float bottom, Paint paint) {
        if (nativeCanvas != 0) {
            OHBridge.canvasDrawOval(nativeCanvas, left, top, right, bottom, penFor(paint), brushFor(paint));
        }
    }
    public void drawOval(RectF oval, Paint paint) {
        if (oval != null) drawOval(oval.left, oval.top, oval.right, oval.bottom, paint);
    }

    public void drawPath(Path path, Paint paint) {
        if (nativeCanvas != 0 && path != null) {
            OHBridge.canvasDrawPath(nativeCanvas, path.getNativeHandle(), penFor(paint), brushFor(paint));
        }
    }

    public void drawPicture(Picture picture) { /* no-op */ }

    public void drawLines(float[] pts, Paint paint) { /* no-op stub */ }
    public void drawLines(float[] pts, int offset, int count, Paint paint) { /* no-op stub */ }

    // ── Reorder barriers (for RenderNode-based drawing) ──────────────────────
    public void insertReorderBarrier() { /* no-op stub */ }
    public void insertInorderBarrier() { /* no-op stub */ }

    /** Returns true if this canvas is recording for the given RenderNode. */
    public boolean isRecordingFor(Object displayList) { return false; }

    /** @deprecated Use save() instead. */
    public static final int CLIP_SAVE_FLAG = 0x02;
    public static boolean sCompatibilityRestore = false;
    public static boolean sCompatibilitySetBitmap = false;

    public static void setCompatibilityVersion(int apiLevel) {}
    public boolean isHardwareAccelerated() { return false; }
    public boolean quickReject(int left, int top, int right, int bottom) { return false; }
    public boolean quickReject(float left, float top, float right, float bottom, EdgeType type) { return false; }
    public int saveUnclippedLayer(int left, int top, int right, int bottom) { return save(); }
    public void restoreUnclippedLayer(int saveCount, Paint paint) { restoreToCount(saveCount); }
    public void setBitmap(Bitmap bm) { /* no-op */ }

    public enum EdgeType { BW, AA }

    // ── Transform stack ──────────────────────────────────────────────────────

    public int save() {
        if (nativeCanvas != 0) OHBridge.canvasSave(nativeCanvas);
        // AOSP returns the save count before the save (used by restoreToCount)
        int count = saveDepth;
        saveDepth++;
        return count;
    }

    public int save(int saveFlags) {
        return save();
    }

    public int saveLayer(float left, float top, float right, float bottom, Paint paint) {
        return saveLayerAlpha(left, top, right, bottom, paint != null ? paint.getAlpha() : 255);
    }

    public int saveLayer(RectF bounds, Paint paint) {
        if (bounds != null) return saveLayer(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
        return saveLayer(0, 0, getWidth(), getHeight(), paint);
    }

    public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
        return saveLayer(bounds, paint);
    }

    public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha) {
        // OH_Drawing doesn't have native saveLayerAlpha.
        // We approximate by doing a save + clipRect. The alpha parameter is stored
        // for potential use but true blending requires OH_Drawing_CanvasSaveLayer.
        if (nativeCanvas != 0) {
            OHBridge.canvasSave(nativeCanvas);
            if (left != 0 || top != 0 || right != 0 || bottom != 0) {
                OHBridge.canvasClipRect(nativeCanvas, left, top, right, bottom);
            }
        }
        int count = saveDepth;
        saveDepth++;
        return count;
    }

    public int saveLayerAlpha(RectF bounds, int alpha) {
        if (bounds != null) {
            return saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha);
        }
        return saveLayerAlpha(0, 0, 0, 0, alpha);
    }

    public void restore() {
        if (saveDepth > 1) {
            saveDepth--;
            if (nativeCanvas != 0) OHBridge.canvasRestore(nativeCanvas);
        }
    }

    public int getSaveCount() { return saveDepth; }

    public void translate(float dx, float dy) {
        if (nativeCanvas != 0) OHBridge.canvasTranslate(nativeCanvas, dx, dy);
    }

    public void scale(float sx, float sy) {
        if (nativeCanvas != 0) OHBridge.canvasScale(nativeCanvas, sx, sy);
    }

    public void scale(float sx, float sy, float px, float py) {
        translate(px, py);
        scale(sx, sy);
        translate(-px, -py);
    }

    public void rotate(float degrees) {
        if (nativeCanvas != 0) OHBridge.canvasRotate(nativeCanvas, degrees, 0, 0);
    }

    public void rotate(float degrees, float px, float py) {
        if (nativeCanvas != 0) OHBridge.canvasRotate(nativeCanvas, degrees, px, py);
    }

    public void restoreToCount(int saveCount) {
        if (saveCount < 1) saveCount = 1; // can't restore past initial state
        while (saveDepth > saveCount) {
            saveDepth--;
            if (nativeCanvas != 0) OHBridge.canvasRestore(nativeCanvas);
        }
    }

    public void concat(Matrix matrix) {
        if (nativeCanvas != 0 && matrix != null) {
            float[] vals = new float[9];
            matrix.getValues(vals);
            OHBridge.canvasConcat(nativeCanvas, vals);
        }
    }

    public void setMatrix(Matrix matrix) {
        // Reset then concat — OH_Drawing doesn't have setMatrix directly
        if (nativeCanvas != 0) {
            // Restore to base state then apply
            if (matrix != null && !matrix.isIdentity()) {
                float[] vals = new float[9];
                matrix.getValues(vals);
                OHBridge.canvasConcat(nativeCanvas, vals);
            }
        }
    }

    public Matrix getMatrix() {
        // Return identity — real matrix tracking would need native support
        return new Matrix();
    }

    public boolean clipRect(Rect rect) { return true; }
    public boolean clipRect(Rect rect, Region.Op op) { return true; }
    public boolean clipRect(RectF rect) { return true; }
    public boolean clipRect(int left, int top, int right, int bottom) {
        if (nativeCanvas != 0) OHBridge.canvasClipRect(nativeCanvas, (float) left, (float) top, (float) right, (float) bottom);
        return true;
    }
    public void clipRect(float left, float top, float right, float bottom) {
        if (nativeCanvas != 0) OHBridge.canvasClipRect(nativeCanvas, left, top, right, bottom);
    }

    public void clipPath(Path path) {
        if (nativeCanvas != 0 && path != null) OHBridge.canvasClipPath(nativeCanvas, path.getNativeHandle());
    }

    // ── Cleanup ──────────────────────────────────────────────────────────────

    public void release() {
        if (nativePenCache != 0) { OHBridge.penDestroy(nativePenCache); nativePenCache = 0; }
        if (nativeBrushCache != 0) { OHBridge.brushDestroy(nativeBrushCache); nativeBrushCache = 0; }
        if (nativeFontCache != 0) { OHBridge.fontDestroy(nativeFontCache); nativeFontCache = 0; }
        if (nativeCanvas != 0 && ownedCanvas) { OHBridge.canvasDestroy(nativeCanvas); nativeCanvas = 0; }
    }

    // ── Clip query ──────────────────────────────────────────────────────────

    public boolean getClipBounds(Rect bounds) {
        if (bounds != null) {
            bounds.set(0, 0, getWidth(), getHeight());
        }
        return true;
    }

    public Rect getClipBounds() {
        Rect r = new Rect();
        getClipBounds(r);
        return r;
    }

    public void clipRectUnion(Rect rect) {
        // no-op stub
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Canvas(" + getWidth() + "x" + getHeight()
             + ", saveDepth=" + saveDepth + ")";
    }
}
