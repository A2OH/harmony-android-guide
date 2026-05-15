// SPDX-License-Identifier: Apache-2.0
//
// SoftwareCanvas — local copy for the E12 in-process launcher.
// Mirrors com.westlake.ohostests.red.SoftwareCanvas verbatim (op-based
// recording: background drawColor + last rect drawRect). Duplicated
// rather than imported because :red-square is an Android application
// module and gradle can't expose its class output to a plain-Java
// consumer cleanly. A separate :util module would be overkill for a
// 110-LOC value type; both copies compile into their own dex and don't
// collide at runtime (different package).
//
// CR-AA-diag (2026-05-15): instrumented with an op histogram and
// first-N method-call log so we can determine empirically whether
// noice's REAL View tree is being walked (drawText/drawBitmap signals)
// or the substrate's recovery FrameLayout fallback (drawColor/drawRect
// only). Generic — no per-app branches.
//
// Macro-shim contract: no Unsafe, no setAccessible, no per-app
// branches.

package com.westlake.ohostests.inproc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SoftwareCanvas extends Canvas {

    private final int width;
    private final int height;

    private int backgroundARGB;
    private boolean hasBackground;

    private int rectColor;
    private int rectX0, rectY0, rectX1, rectY1;
    private boolean hasRect;

    // CR-AA-diag instrumentation
    private final Map<String, Integer> opHistogram = new LinkedHashMap<>();
    private final List<String> firstOps = new ArrayList<>();
    private int totalOps;
    private static final int FIRST_N = 20;

    public SoftwareCanvas(int width, int height) {
        super();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("bad size: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    private void recordOp(String op, String detail) {
        totalOps++;
        Integer cur = opHistogram.get(op);
        opHistogram.put(op, cur == null ? 1 : cur + 1);
        if (firstOps.size() < FIRST_N) {
            firstOps.add(op + "(" + detail + ")");
        }
    }

    public int getTotalOps() { return totalOps; }
    public Map<String, Integer> getOpHistogram() { return opHistogram; }
    public List<String> getFirstOps() { return firstOps; }

    @Override
    public void drawColor(int color) {
        recordOp("drawColor", hex(color));
        this.backgroundARGB = color;
        this.hasBackground = true;
    }

    @Override
    public void drawColor(int color, PorterDuff.Mode mode) {
        recordOp("drawColor_mode", hex(color));
        // delegate path also bumps drawColor — keep histogram entries distinct
        this.backgroundARGB = color;
        this.hasBackground = true;
    }

    @Override
    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        int color = paint == null ? 0 : paint.getColor();
        recordOp("drawRect", ((int) left) + "," + ((int) top) + "," + ((int) right)
                + "," + ((int) bottom) + " color=" + hex(color));
        if (paint == null) return;
        this.rectColor = color;
        this.rectX0 = clampX((int) left);
        this.rectY0 = clampY((int) top);
        this.rectX1 = clampX((int) right);
        this.rectY1 = clampY((int) bottom);
        this.hasRect = true;
    }

    /** ARGB int -> "0xAARRGGBB" without going through Formatter
     *  (Formatter on this dalvik-kitkat BCP can't init libcore.icu.LocaleData
     *  and crashes the draw walk). */
    private static String hex(int v) {
        String h = Integer.toHexString(v);
        // Pad to 8 chars without String.format
        StringBuilder sb = new StringBuilder("0x");
        for (int i = h.length(); i < 8; i++) sb.append('0');
        sb.append(h);
        return sb.toString();
    }

    @Override
    public void drawRect(Rect r, Paint paint) {
        if (r == null) {
            recordOp("drawRect_rect", "null");
            return;
        }
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    @Override
    public void drawRect(RectF r, Paint paint) {
        if (r == null) {
            recordOp("drawRect_rectf", "null");
            return;
        }
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    @Override
    public void drawRoundRect(float left, float top, float right, float bottom,
                              float rx, float ry, Paint paint) {
        int color = paint == null ? 0 : paint.getColor();
        recordOp("drawRoundRect", ((int) left) + "," + ((int) top) + "," + ((int) right)
                + "," + ((int) bottom) + " rx=" + ((int) rx) + " color=" + hex(color));
    }

    @Override
    public void drawText(String text, float x, float y, Paint paint) {
        String preview = text == null ? "null"
                : (text.length() > 24 ? text.substring(0, 24) + "..." : text);
        recordOp("drawText", "\"" + preview + "\" x=" + x + " y=" + y);
    }

    @Override
    public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
        String preview = text == null ? "null"
                : text.subSequence(start, Math.min(end, start + 24)).toString();
        recordOp("drawText_cs", "\"" + preview + "\" x=" + x + " y=" + y);
    }

    @Override
    public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
        String preview = text == null ? "null"
                : new String(text, index, Math.min(count, 24));
        recordOp("drawText_arr", "\"" + preview + "\" x=" + x + " y=" + y);
    }

    @Override
    public void drawText(String text, int start, int end, float x, float y, Paint paint) {
        String preview = text == null ? "null"
                : text.substring(start, Math.min(end, start + 24));
        recordOp("drawText_se", "\"" + preview + "\" x=" + x + " y=" + y);
    }

    @Override
    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        int bw = bitmap == null ? 0 : bitmap.getWidth();
        int bh = bitmap == null ? 0 : bitmap.getHeight();
        recordOp("drawBitmap", bw + "x" + bh + " at " + left + "," + top);
    }

    @Override
    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        recordOp("drawBitmap_rr", "src=" + src + " dst=" + dst);
    }

    @Override
    public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
        recordOp("drawBitmap_rrf", "src=" + src + " dst=" + dst);
    }

    @Override
    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        recordOp("drawBitmap_m", "matrix");
    }

    @Override
    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        recordOp("drawCircle", "cx=" + cx + " cy=" + cy + " r=" + radius);
    }

    @Override
    public void drawPath(Path path, Paint paint) {
        recordOp("drawPath", path == null ? "null" : path.toString());
    }

    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        recordOp("drawLine", startX + "," + startY + "->" + stopX + "," + stopY);
    }

    @Override
    public void drawArc(float left, float top, float right, float bottom,
                        float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        recordOp("drawArc", "rect=" + left + "," + top + "," + right + "," + bottom);
    }

    @Override
    public void drawOval(float left, float top, float right, float bottom, Paint paint) {
        recordOp("drawOval", left + "," + top + "," + right + "," + bottom);
    }

    @Override
    public boolean clipRect(int left, int top, int right, int bottom) {
        recordOp("clipRect_i", left + "," + top + "," + right + "," + bottom);
        return true;
    }

    // NOTE: clipRect(float,float,float,float) and clipPath(Path) deliberately
    // NOT overridden. They have a return-type descriptor mismatch between
    // android.jar (API 30, boolean) and our runtime shim Canvas (void).
    // Compile-against-30 would force `boolean`; runtime dispatch needs
    // `void`. A mismatch on either side breaks one of compile or run. Since
    // View.drawBackground -> ColorDrawable.draw does NOT call these, dropping
    // them costs us nothing for the diagnostic. The OHOS-shim Canvas's own
    // clipRect/clipPath are no-op stubs anyway (see Canvas.safeClipRect).

    @Override
    public boolean clipRect(Rect rect) {
        recordOp("clipRect_rect", rect == null ? "null" : rect.toString());
        return true;
    }

    @Override
    public boolean clipRect(RectF rect) {
        recordOp("clipRect_rectf", rect == null ? "null" : rect.toString());
        return true;
    }

    @Override
    public int save() {
        recordOp("save", "");
        return super.save();
    }

    @Override
    public void restore() {
        recordOp("restore", "");
        super.restore();
    }

    @Override
    public void translate(float dx, float dy) {
        recordOp("translate", dx + "," + dy);
        super.translate(dx, dy);
    }

    @Override
    public void scale(float sx, float sy) {
        recordOp("scale", sx + "," + sy);
        super.scale(sx, sy);
    }

    @Override
    public void rotate(float degrees) {
        recordOp("rotate", String.valueOf(degrees));
        super.rotate(degrees);
    }

    @Override
    public void concat(Matrix matrix) {
        recordOp("concat", matrix == null ? "null" : "matrix");
    }

    @Override
    public void setMatrix(Matrix matrix) {
        recordOp("setMatrix", matrix == null ? "null" : "matrix");
    }

    public boolean hasBackground() { return hasBackground; }
    public int getBackgroundARGB() { return backgroundARGB; }

    public boolean hasRect() { return hasRect; }
    public int getRectColor() { return rectColor; }
    public int getRectX0()    { return rectX0; }
    public int getRectY0()    { return rectY0; }
    public int getRectX1()    { return rectX1; }
    public int getRectY1()    { return rectY1; }

    public int sampleArgb(int x, int y) {
        int c = hasBackground ? backgroundARGB : 0;
        if (hasRect && x >= rectX0 && x < rectX1 && y >= rectY0 && y < rectY1) {
            c = rectColor;
        }
        return c;
    }

    private int clampX(int v) { return Math.max(0, Math.min(width,  v)); }
    private int clampY(int v) { return Math.max(0, Math.min(height, v)); }
}
