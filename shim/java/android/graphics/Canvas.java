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

    public static boolean isUsable(Canvas canvas) {
        return canvas != null && canvas.nativeCanvas != 0;
    }

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

    private static long ensurePenSafe(Canvas canvas, Paint paint) {
        if (canvas == null) return 0;
        if (canvas.nativePenCache == 0) canvas.nativePenCache = OHBridge.penCreate();
        if (paint != null) {
            OHBridge.penSetColor(canvas.nativePenCache, paint.getColor());
            OHBridge.penSetWidth(canvas.nativePenCache, paint.getStrokeWidth());
            OHBridge.penSetAntiAlias(canvas.nativePenCache, paint.isAntiAlias());
            OHBridge.penSetCap(canvas.nativePenCache, paint.getStrokeCap().ordinal());
            OHBridge.penSetJoin(canvas.nativePenCache, paint.getStrokeJoin().ordinal());
        }
        return canvas.nativePenCache;
    }

    private static long ensureBrushSafe(Canvas canvas, Paint paint) {
        if (canvas == null) return 0;
        if (canvas.nativeBrushCache == 0) canvas.nativeBrushCache = OHBridge.brushCreate();
        if (paint != null) {
            OHBridge.brushSetColor(canvas.nativeBrushCache, paint.getColor());
        }
        return canvas.nativeBrushCache;
    }

    private static long ensureFontSafe(Canvas canvas, Paint paint) {
        if (canvas == null) return 0;
        if (canvas.nativeFontCache == 0) canvas.nativeFontCache = OHBridge.fontCreate();
        if (paint != null) {
            OHBridge.fontSetSize(canvas.nativeFontCache, paint.getTextSize());
        }
        return canvas.nativeFontCache;
    }

    private static long penForSafe(Canvas canvas, Paint paint) {
        if (paint == null) return 0;
        Paint.Style s = paint.getStyle();
        return (s != Paint.Style.FILL) ? ensurePenSafe(canvas, paint) : 0;
    }

    private static long brushForSafe(Canvas canvas, Paint paint) {
        if (paint == null) return 0;
        Paint.Style s = paint.getStyle();
        return (s != Paint.Style.STROKE) ? ensureBrushSafe(canvas, paint) : 0;
    }

    public static void safeDrawColor(Canvas canvas, int color) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawColor(canvas.nativeCanvas, color);
    }

    public static void safeDrawRect(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawRect(canvas.nativeCanvas, left, top, right, bottom,
                penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    public static void safeDrawCircle(Canvas canvas, float cx, float cy, float radius, Paint paint) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawCircle(canvas.nativeCanvas, cx, cy, radius,
                penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    public static void safeDrawLine(Canvas canvas, float startX, float startY, float stopX, float stopY, Paint paint) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawLine(canvas.nativeCanvas, startX, startY, stopX, stopY, ensurePenSafe(canvas, paint));
    }

    public static void safeDrawRoundRect(Canvas canvas, float left, float top, float right, float bottom,
                                         float rx, float ry, Paint paint) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawRoundRect(canvas.nativeCanvas, left, top, right, bottom, rx, ry,
                penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    public static void safeDrawArc(Canvas canvas, float left, float top, float right, float bottom,
                                   float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawArc(canvas.nativeCanvas, left, top, right, bottom, startAngle, sweepAngle,
                useCenter, penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    public static void safeDrawOval(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasDrawOval(canvas.nativeCanvas, left, top, right, bottom,
                penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    public static void safeDrawPath(Canvas canvas, Path path, Paint paint) {
        if (!isUsable(canvas) || path == null) return;
        OHBridge.canvasDrawPath(canvas.nativeCanvas, path.getNativeHandle(),
                penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    // ── Draw operations ──────────────────────────────────────────────────────

    public void drawColor(int color) {
        safeDrawColor(this, color);
    }

    public void drawColor(int color, PorterDuff.Mode mode) {
        drawColor(color);
    }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        safeDrawRect(this, left, top, right, bottom, paint);
    }

    public void drawRect(Rect r, Paint paint) {
        if (r != null) drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    public void drawRect(RectF r, Paint paint) {
        if (r != null) drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        safeDrawCircle(this, cx, cy, radius, paint);
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        safeDrawLine(this, startX, startY, stopX, stopY, paint);
    }

    public void drawText(String text, float x, float y, Paint paint) {
        safeDrawText(this, text, x, y, paint);
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

    public static void safeDrawText(Canvas canvas, String text, float x, float y, Paint paint) {
        if (!isUsable(canvas) || text == null) return;
        OHBridge.canvasDrawText(canvas.nativeCanvas, text, x, y,
                ensureFontSafe(canvas, paint), penForSafe(canvas, paint), brushForSafe(canvas, paint));
    }

    public static void safeDrawText(Canvas canvas, CharSequence text, int start, int end,
                                    float x, float y, Paint paint) {
        if (text == null || start < 0 || end < start || end > text.length()) return;
        safeDrawText(canvas, text.subSequence(start, end).toString(), x, y, paint);
    }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        if (isUsable(this) && bitmap != null) {
            int bw = bitmap.getWidth(), bh = bitmap.getHeight();
            // Skip views with insane dimensions (corrupt layout)
            if (bw > 4000 || bh > 4000) return;
            if (bitmap.mPixels != null && bw * bh <= 4096) {
                // Small decoded ARGB pixels — send through OP_ARGB_BITMAP (skip large to avoid dlist overflow)
                OHBridge.canvasDrawArgbBitmap(nativeCanvas, bitmap.mPixels, left, top, bw, bh);
            } else if (bitmap.mImageData != null) {
                // Raw image file bytes — host decodes via OP_IMAGE
                OHBridge.canvasDrawImage(nativeCanvas, bitmap.mImageData, left, top, bw, bh);
            } else {
                OHBridge.canvasDrawBitmap(nativeCanvas, bitmap.getNativeHandle(), left, top);
            }
        }
    }

    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        if (nativeCanvas == 0 || bitmap == null || dst == null) return;
        if (bitmap.mPixels != null && bitmap.getWidth() * bitmap.getHeight() <= 4096) {
            OHBridge.canvasDrawArgbBitmap(nativeCanvas, bitmap.mPixels,
                    (float) dst.left, (float) dst.top, dst.width(), dst.height());
        } else if (bitmap.mImageData != null) {
            OHBridge.canvasDrawImage(nativeCanvas, bitmap.mImageData,
                    (float) dst.left, (float) dst.top, dst.width(), dst.height());
        } else {
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
        safeDrawRoundRect(this, left, top, right, bottom, rx, ry, paint);
    }

    public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
        if (rect != null) drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, rx, ry, paint);
    }

    public void drawArc(float left, float top, float right, float bottom,
                        float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        safeDrawArc(this, left, top, right, bottom, startAngle, sweepAngle, useCenter, paint);
    }
    public void drawArc(RectF oval, float startAngle, float sweepAngle,
                        boolean useCenter, Paint paint) {
        if (oval != null) drawArc(oval.left, oval.top, oval.right, oval.bottom, startAngle, sweepAngle, useCenter, paint);
    }

    public void drawOval(float left, float top, float right, float bottom, Paint paint) {
        safeDrawOval(this, left, top, right, bottom, paint);
    }
    public void drawOval(RectF oval, Paint paint) {
        if (oval != null) drawOval(oval.left, oval.top, oval.right, oval.bottom, paint);
    }

    public void drawPath(Path path, Paint paint) {
        safeDrawPath(this, path, paint);
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
        return safeSave(this);
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
        int count = safeSave(this);
        if (left != 0 || top != 0 || right != 0 || bottom != 0) {
            safeClipRect(this, left, top, right, bottom);
        }
        return count;
    }

    public int saveLayerAlpha(RectF bounds, int alpha) {
        if (bounds != null) {
            return saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha);
        }
        return saveLayerAlpha(0, 0, 0, 0, alpha);
    }

    public void restore() {
        safeRestore(this);
    }

    public int getSaveCount() { return saveDepth; }

    public void translate(float dx, float dy) {
        safeTranslate(this, dx, dy);
    }

    public void scale(float sx, float sy) {
        if (!isUsable(this)) return;
        OHBridge.canvasScale(nativeCanvas, sx, sy);
    }

    public void scale(float sx, float sy, float px, float py) {
        translate(px, py);
        scale(sx, sy);
        translate(-px, -py);
    }

    public void rotate(float degrees) {
        if (!isUsable(this)) return;
        OHBridge.canvasRotate(nativeCanvas, degrees, 0, 0);
    }

    public void rotate(float degrees, float px, float py) {
        if (!isUsable(this)) return;
        OHBridge.canvasRotate(nativeCanvas, degrees, px, py);
    }

    public void restoreToCount(int saveCount) {
        safeRestoreToCount(this, saveCount);
    }

    public void concat(Matrix matrix) {
        if (isUsable(this) && matrix != null) {
            float[] vals = new float[9];
            matrix.getValues(vals);
            OHBridge.canvasConcat(nativeCanvas, vals);
        }
    }

    public void setMatrix(Matrix matrix) {
        // Reset then concat — OH_Drawing doesn't have setMatrix directly
        if (isUsable(this)) {
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
        safeClipRect(this, left, top, right, bottom);
        return true;
    }
    public void clipRect(float left, float top, float right, float bottom) {
        safeClipRect(this, left, top, right, bottom);
    }

    public static int safeSave(Canvas canvas) {
        if (canvas == null) return 1;
        if (canvas.nativeCanvas != 0) OHBridge.canvasSave(canvas.nativeCanvas);
        int count = canvas.saveDepth;
        canvas.saveDepth++;
        return count;
    }

    public static void safeRestore(Canvas canvas) {
        if (canvas == null) return;
        if (canvas.saveDepth > 1) {
            canvas.saveDepth--;
            if (canvas.nativeCanvas != 0) OHBridge.canvasRestore(canvas.nativeCanvas);
        }
    }

    public static void safeRestoreToCount(Canvas canvas, int saveCount) {
        if (canvas == null) return;
        if (saveCount < 1) saveCount = 1;
        while (canvas.saveDepth > saveCount) {
            canvas.saveDepth--;
            if (canvas.nativeCanvas != 0) OHBridge.canvasRestore(canvas.nativeCanvas);
        }
    }

    public static void safeTranslate(Canvas canvas, float dx, float dy) {
        if (!isUsable(canvas)) return;
        OHBridge.canvasTranslate(canvas.nativeCanvas, dx, dy);
    }

    public static void safeClipRect(Canvas canvas, float left, float top, float right, float bottom) {
        if (!isUsable(canvas)) return;
        // The pipe-backed OHBridge clip stack is still unstable under real app
        // view recursion. Treat clipping as advisory so rendering survives.
    }

    public void clipPath(Path path) {
        // Clip-path rendering is not stable in the standalone bridge yet.
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
