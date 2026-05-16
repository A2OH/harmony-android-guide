// SPDX-License-Identifier: Apache-2.0
//
// M6FramePainter — generates one BGRA frame for a given color.
//
// Phase 2 RedView pattern: keep "what color goes on the panel" as a
// single source-of-truth call (drawColor(int) on a SoftwareCanvas).
// On the M6 daemon path, the consumer is the daemon's dumb-BO mmap
// rather than fb0, so we render straight into the BGRA byte[] that
// gets memfd'd over to the daemon — same byte layout
// (DRM_FORMAT_XRGB8888 little-endian = B,G,R,A per pixel) the
// daemon's --test-client uses.
//
// No per-app branches; pure pixel synthesis.

package com.westlake.ohostests.m6;

import android.graphics.Color;

public final class M6FramePainter {

    private final int width;
    private final int height;

    public M6FramePainter(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("bad size: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getByteCount() { return width * height * 4; }

    /**
     * Allocate a fresh BGRA byte[] filled with {@code argbColor}.
     *
     * {@code argbColor} is a normal {@link android.graphics.Color} value
     * (ARGB packed int — A high byte, R next, G next, B low byte).
     * We map that to BGRA bytes for DRM_FORMAT_XRGB8888 little-endian:
     * byte 0 = B, 1 = G, 2 = R, 3 = A.
     */
    public byte[] paint(int argbColor) {
        byte a = (byte) Color.alpha(argbColor);
        byte r = (byte) Color.red(argbColor);
        byte g = (byte) Color.green(argbColor);
        byte b = (byte) Color.blue(argbColor);
        byte[] out = new byte[width * height * 4];
        for (int i = 0; i < out.length; i += 4) {
            out[i    ] = b;
            out[i + 1] = g;
            out[i + 2] = r;
            out[i + 3] = a;
        }
        return out;
    }
}
