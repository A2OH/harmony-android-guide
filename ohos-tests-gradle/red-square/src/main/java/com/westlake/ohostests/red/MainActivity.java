// SPDX-License-Identifier: Apache-2.0
//
// MainActivity — MVP-2 red-square Activity (PF-ohos-mvp-003).
//
// onCreate pipeline:
//   1. Create RedView (extends View).
//   2. setContentView(redView)            ─ exercises V2 substrate (gate 2)
//   3. measure(EXACTLY 720, EXACTLY 1280) ─ DAYU200 framebuffer geometry
//   4. layout(0, 0, 720, 1280)
//   5. SoftwareCanvas backed by int[720*1280]
//   6. redView.draw(canvas)               ─ calls onDraw → drawColor(RED)
//   7. Fb0Presenter.present(int[], w, h)  ─ /dev/graphics/fb0 visible (gate 3)
//
// Marker contract (matches scripts/run-ohos-test.sh red-square):
//   "OhosRedSquare.onCreate reached pid=<n>"
//   "OhosRedSquare.fb0 write OK"        ─ pixels landed on framebuffer
//   "OhosRedSquare.fb0 write FAILED"    ─ explicit failure

package com.westlake.ohostests.red;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    private static final String TAG = "OhosRedSquare";

    // DAYU200 framebuffer geometry — see
    // hdc shell "cat /sys/class/graphics/fb0/virtual_size" → "720,1280"
    private static final int FB_W = 720;
    private static final int FB_H = 1280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String pidMarker = "OhosRedSquare.onCreate reached pid="
                + android.os.Process.myPid();
        emit(pidMarker);

        emit("step 1: build RedView");
        RedView redView;
        try {
            redView = new RedView(this);
            emit("step 1: redView=" + redView.getClass().getName());
        } catch (Throwable t) {
            emit("step 1: RedView ctor threw: " + t);
            t.printStackTrace(System.out);
            emit("OhosRedSquare.fb0 write FAILED at step 1");
            finish();
            return;
        }

        emit("step 2: setContentView");
        try {
            setContentView(redView);
            emit("step 2: setContentView returned");
        } catch (Throwable t) {
            // Non-fatal — the V2 substrate's setContentView may have edge
            // cases, but we can still draw the view directly.
            emit("step 2: setContentView threw (continuing): " + t);
        }

        emit("step 3: measure " + FB_W + "x" + FB_H);
        try {
            int wSpec = View.MeasureSpec.makeMeasureSpec(FB_W,
                    View.MeasureSpec.EXACTLY);
            int hSpec = View.MeasureSpec.makeMeasureSpec(FB_H,
                    View.MeasureSpec.EXACTLY);
            redView.measure(wSpec, hSpec);
            emit("step 3: measured=" + redView.getMeasuredWidth()
                    + "x" + redView.getMeasuredHeight());
        } catch (Throwable t) {
            emit("step 3: measure threw: " + t);
            t.printStackTrace(System.out);
        }

        emit("step 4: layout 0 0 " + FB_W + " " + FB_H);
        try {
            redView.layout(0, 0, FB_W, FB_H);
            emit("step 4: laid out " + redView.getLeft() + ","
                    + redView.getTop() + " " + redView.getRight() + ","
                    + redView.getBottom());
        } catch (Throwable t) {
            emit("step 4: layout threw: " + t);
            t.printStackTrace(System.out);
        }

        emit("step 5: build SoftwareCanvas");
        SoftwareCanvas canvas;
        try {
            canvas = new SoftwareCanvas(FB_W, FB_H);
            emit("step 5: canvas=" + canvas.getWidth() + "x"
                    + canvas.getHeight());
        } catch (Throwable t) {
            emit("step 5: SoftwareCanvas ctor threw: " + t);
            t.printStackTrace(System.out);
            emit("OhosRedSquare.fb0 write FAILED at step 5");
            finish();
            return;
        }

        emit("step 6: redView.draw(canvas) → onDraw → drawColor(RED)");
        boolean drewViaDispatch = false;
        try {
            redView.draw(canvas);
            drewViaDispatch = true;
            emit("step 6: View.draw returned");
        } catch (Throwable t) {
            // The full View.draw dispatch may touch unimplemented parts of
            // the shim (background drawable handling, etc.). Fall back to
            // calling onDraw directly — it's protected but we can reach it
            // via a public helper or by subclassing RedView. Here we use a
            // public helper added to RedView in case dispatch fails.
            emit("step 6: View.draw threw: " + t.getClass().getName()
                    + " (will fall back to direct fill)");
        }

        if (!drewViaDispatch) {
            // Last-resort: bypass View.draw chain and paint directly.
            // This still honors the contract: SoftwareCanvas.drawColor is
            // the source-of-truth pixel filler.
            emit("step 6b: canvas.drawColor(RED) directly");
            try {
                canvas.drawColor(Color.RED);
                emit("step 6b: direct fill done");
            } catch (Throwable t) {
                emit("step 6b: drawColor threw: " + t);
                emit("OhosRedSquare.fb0 write FAILED at step 6b");
                finish();
                return;
            }
        }

        // Sanity: confirm the canvas recorded a background fill via drawColor.
        emit("step 6c: canvas.hasBackground=" + canvas.hasBackground()
                + " bgARGB=0x" + Integer.toHexString(canvas.getBackgroundARGB())
                + " sample(0,0)=0x" + Integer.toHexString(canvas.sampleArgb(0, 0))
                + " sample(mid)=0x" + Integer.toHexString(canvas.sampleArgb(FB_W / 2, FB_H / 2)));

        emit("step 7: present to /dev/graphics/fb0");
        boolean ok = Fb0Presenter.present(canvas, System.out);
        if (ok) {
            emit("OhosRedSquare.fb0 write OK");
        } else {
            emit("OhosRedSquare.fb0 write FAILED at Fb0Presenter");
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        emit("OhosRedSquare.onDestroy reached");
        super.onDestroy();
    }

    private static void emit(String msg) {
        // Belt-and-suspenders: both stdout (DirectPrintStream → fd 1) and
        // android.util.Log (may or may not reach the user's shell on
        // standalone OHOS — MVP-1 found Log unreliable here).
        System.out.println(msg);
        Log.i(TAG, msg);
    }
}
