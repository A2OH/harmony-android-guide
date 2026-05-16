// SPDX-License-Identifier: Apache-2.0
//
// RedView — MVP-2 source-of-truth Android View (PF-ohos-mvp-003).
//
// Contract: onDraw(Canvas c) { c.drawColor(Color.RED); } is the SOLE
// statement that decides the displayed color. Anything that ends up on
// the DAYU200's HDMI display traces back to this method.
//
// No per-app branches in the shim — RedView is ordinary Android code
// using the public android.view.View / android.graphics.Canvas /
// android.graphics.Color APIs.

package com.westlake.ohostests.red;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class RedView extends View {

    public RedView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // The single source of truth: paint everything red.
        canvas.drawColor(Color.RED);
    }
}
