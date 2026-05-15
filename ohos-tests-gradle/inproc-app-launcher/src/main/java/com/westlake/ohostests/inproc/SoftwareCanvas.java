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
// Macro-shim contract: no Unsafe, no setAccessible, no per-app
// branches.

package com.westlake.ohostests.inproc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;

public class SoftwareCanvas extends Canvas {

    private final int width;
    private final int height;

    private int backgroundARGB;
    private boolean hasBackground;

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
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

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
