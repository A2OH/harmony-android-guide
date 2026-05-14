// SPDX-License-Identifier: Apache-2.0
//
// SoftwareCanvas — MVP-2 software pixel buffer wrapped as an
// android.graphics.Canvas subclass (PF-ohos-mvp-003).
//
// Design notes:
// - Lives in the TEST APP, not the shim. We subclass the shim's
//   public android.graphics.Canvas — drawColor / drawRect are
//   regular Java methods we can override.
// - Memory pressure: a full 720x1280 ARGB8888 int[] = 3.6 MB
//   triggers a heap-mark GC that segfaults the dalvik-kitkat VM
//   on the OHOS aarch64 build (signal 11 at heap mark recursion).
//   Workaround: do NOT materialize the full pixel grid. Track per-
//   pixel state via *runs* of drawColor / drawRect operations.
//   The presenter then walks those runs row by row, writing a small
//   per-row scratch buffer to /dev/graphics/fb0. MVP-2 only needs
//   solid-fill rendering, so this representation is loss-free.

package com.westlake.ohostests.red;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;

public class SoftwareCanvas extends Canvas {

    private final int width;
    private final int height;

    /**
     * Background fill from the most-recent drawColor() call.
     * 0 (transparent) until drawColor is invoked.
     */
    private int backgroundARGB;
    private boolean hasBackground;

    /**
     * Optional rect overlay from the most-recent drawRect() call.
     * Only the LAST rect is recorded — MVP-2 doesn't need a full
     * display list.
     */
    private int rectColor;
    private int rectX0, rectY0, rectX1, rectY1;
    private boolean hasRect;

    public SoftwareCanvas(int width, int height) {
        super();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("bad size: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void drawColor(int color) {
        this.backgroundARGB = color;
        this.hasBackground = true;
    }

    @Override
    public void drawColor(int color, PorterDuff.Mode mode) {
        drawColor(color);
    }

    @Override
    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        if (paint == null) return;
        this.rectColor = paint.getColor();
        this.rectX0 = clampX((int) left);
        this.rectY0 = clampY((int) top);
        this.rectX1 = clampX((int) right);
        this.rectY1 = clampY((int) bottom);
        this.hasRect = true;
    }

    // ── Accessors used by Fb0Presenter ──

    public boolean hasBackground() { return hasBackground; }
    public int getBackgroundARGB() { return backgroundARGB; }

    public boolean hasRect() { return hasRect; }
    public int getRectColor() { return rectColor; }
    public int getRectX0()    { return rectX0; }
    public int getRectY0()    { return rectY0; }
    public int getRectX1()    { return rectX1; }
    public int getRectY1()    { return rectY1; }

    /** Compute the ARGB color for a given pixel by replaying recorded ops. */
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
