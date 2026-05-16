// SPDX-License-Identifier: Apache-2.0
//
// ColorView — E12 source-of-truth Android View.
//
// Contract: onDraw(Canvas c) { c.drawColor(DRAW_COLOR); } is the SOLE
// statement that decides the displayed color. Anything that ends up on
// the DAYU200's DSI panel via the in-process launcher traces back here.
//
// No per-app branches in the shim — ColorView is ordinary Android code
// using the public android.view.View / android.graphics.Canvas /
// android.graphics.Color APIs.
//
// Color is BLUE (0xFF0000FF in ARGB8888) so it's visually distinct from
// E9b's hardcoded RED in libdrm_inproc_bridge.so. If the panel goes BLUE
// during this test, the pixel demonstrably came from ColorView.onDraw —
// not from a leftover RED fill in the C bridge.

package com.westlake.ohostests.helloc;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class ColorView extends View {

    /** ARGB8888 BLUE. Public so the launcher can sanity-check after the
     *  draw (the SoftwareCanvas backbuffer should sample to this value). */
    public static final int DRAW_COLOR = 0xFF0000FF;

    public ColorView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(DRAW_COLOR);
    }
}
