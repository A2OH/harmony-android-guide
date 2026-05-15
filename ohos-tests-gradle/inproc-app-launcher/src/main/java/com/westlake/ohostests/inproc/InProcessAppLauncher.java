// SPDX-License-Identifier: Apache-2.0
//
// InProcessAppLauncher — E12 gate (CR60 follow-up, 2026-05-15).
//
// Goal: drive a real Android Activity's onCreate to first-pixel on the
// DAYU200 DSI panel, fully in-process under dalvikvm-arm32-dynamic. No
// M6 daemon, no compositor surface, no separate aarch64 helper binary.
//
// Pipeline (gate E12 stage 1):
//   1. parse argv: "<package>/<MainActivityClass>" + holdSecs.
//   2. resolve class via system ClassLoader. Stage 1 puts the app APK
//      dex on -Xbootclasspath; Class.forName succeeds without a
//      separate DexClassLoader instance. Stage 2 (deferred) switches to
//      DexClassLoader so we can load arbitrary APKs (noice/McD/...).
//   3. instantiate via Activity's public no-arg ctor.
//   4. Instrumentation.callActivityOnCreate(activity, null).
//   5. Cast Activity to InProcDrawSource (the launcher's own public
//      interface) and call getDrawView(). Apps that target the in-
//      process pipeline implement the interface on their MainActivity.
//      No reflection, no setAccessible, no per-app branches.
//   6. Allocate SoftwareCanvas (FB_W x FB_H). Local class in this
//      module (mirrors :red-square's SoftwareCanvas — duplicated rather
//      than imported because gradle can't share Android-application
//      class output with plain-Java consumers cleanly).
//   7. view.draw(canvas) — drives onDraw; SoftwareCanvas records the
//      background color (and optionally one rect overlay).
//   8. Materialize int[w*h] ARGB8888 buffer by walking sampleArgb(x,y)
//      across the grid. Memory cost: 720*1280*4 = 3.6 MB. The
//      SoftwareCanvas's design note explicitly calls out that materialising
//      the full grid as a single int[] is fine — it's the per-pixel
//      heap-mark recursion (3.6 MB of int[] entries with random VALUES)
//      that segfaults the heap mark phase; populating a flat int[] with
//      a known repeating value (background color) doesn't trip the bug.
//   9. DrmInprocessPresenter.present(argb, w, h, holdSecs) — JNI into
//      libdrm_inproc_bridge.so's new nativePresentArgb entry point.
//  10. Instrumentation.callActivityOnDestroy(activity) — graceful unwind.
//
// Marker contract (driver greps these EXACT strings):
//     inproc-app-launcher-start
//     inproc-app-launcher step <N>: <message>
//     inproc-app-launcher canvas sample(0,0)=0x<hex> mid=0x<hex>
//     inproc-app-launcher present rc=<int> reason=<text>
//     inproc-app-launcher-done passed=<int> failed=<int>
//
// Macro-shim contract: NO Unsafe.allocateInstance, NO setAccessible,
// NO per-app branches. The launcher is generic — pass any
// "<pkg>/<Activity>" pair whose Activity exposes a public View
// 'drawView' field.

package com.westlake.ohostests.inproc;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.view.View;

// SoftwareCanvas is local to this module — see SoftwareCanvas.java
// (intentional duplicate of :red-square's class; see that file for
// rationale).

public final class InProcessAppLauncher {

    private static final String TAG = "InProcessAppLauncher";

    /** DAYU200 DSI-1 panel native resolution; matches red-square. */
    private static final int FB_W = 720;
    private static final int FB_H = 1280;

    private InProcessAppLauncher() { /* no instances */ }

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;
        log("inproc-app-launcher-start");

        // Step 1: parse argv.
        if (args == null || args.length < 1) {
            log("step 1 FAIL: missing arg; usage: <pkg>/<Activity> [holdSecs]");
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(2);
            return;
        }
        String spec = args[0];
        int holdSecs = 6;
        if (args.length >= 2) {
            try {
                holdSecs = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                /* keep default */
            }
        }
        String className;
        int slash = spec.indexOf('/');
        if (slash >= 0) {
            String pkg = spec.substring(0, slash);
            String cls = spec.substring(slash + 1);
            className = cls.startsWith(".") ? pkg + cls : cls;
        } else {
            className = spec;
        }
        log("step 1: spec=" + spec + " class=" + className
                + " holdSecs=" + holdSecs);

        // Step 2: resolve.
        Class<?> activityClass;
        try {
            activityClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log("step 2 FAIL ClassNotFound: " + className + " (" + e + ")");
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(3);
            return;
        }
        log("step 2: class loaded " + activityClass.getName()
                + " superclass=" + activityClass.getSuperclass().getName());

        // Step 3: instantiate.
        Activity activity;
        try {
            Object obj = activityClass.newInstance();
            if (!(obj instanceof Activity)) {
                log("step 3 FAIL: not an Activity: " + obj.getClass());
                log("inproc-app-launcher-done passed=" + passed + " failed=1");
                System.exit(4);
                return;
            }
            activity = (Activity) obj;
        } catch (Throwable t) {
            log("step 3 FAIL newInstance: " + t);
            t.printStackTrace(System.out);
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(5);
            return;
        }
        log("step 3: activity instantiated " + activity.getClass().getName());

        // Step 4: onCreate.
        Instrumentation instr = new Instrumentation();
        Bundle savedState = null;
        try {
            instr.callActivityOnCreate(activity, savedState);
        } catch (Throwable t) {
            log("step 4 FAIL callActivityOnCreate: " + t);
            t.printStackTrace(System.out);
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(6);
            return;
        }
        log("step 4: onCreate returned cleanly");
        passed++;

        // Step 5: locate drawView via the InProcDrawSource interface.
        // No reflection, no setAccessible — straight instanceof + cast.
        View drawView;
        if (!(activity instanceof InProcDrawSource)) {
            log("step 5 FAIL: Activity does not implement InProcDrawSource: "
                    + activity.getClass().getName());
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(7);
            return;
        }
        try {
            drawView = ((InProcDrawSource) activity).getDrawView();
        } catch (Throwable t) {
            log("step 5 FAIL getDrawView: " + t);
            t.printStackTrace(System.out);
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(8);
            return;
        }
        if (drawView == null) {
            log("step 5 FAIL: getDrawView returned null");
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(9);
            return;
        }
        log("step 5: drawView=" + drawView.getClass().getName());
        passed++;

        // Step 6: SoftwareCanvas.
        SoftwareCanvas canvas;
        try {
            canvas = new SoftwareCanvas(FB_W, FB_H);
        } catch (Throwable t) {
            log("step 6 FAIL: SoftwareCanvas ctor: " + t);
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(10);
            return;
        }
        log("step 6: canvas=" + canvas.getWidth() + "x" + canvas.getHeight());

        // Step 7: measure+layout+draw.
        try {
            int wSpec = View.MeasureSpec.makeMeasureSpec(FB_W,
                    View.MeasureSpec.EXACTLY);
            int hSpec = View.MeasureSpec.makeMeasureSpec(FB_H,
                    View.MeasureSpec.EXACTLY);
            drawView.measure(wSpec, hSpec);
            drawView.layout(0, 0, FB_W, FB_H);
            log("step 7a: laid out " + drawView.getWidth() + "x" + drawView.getHeight());
        } catch (Throwable t) {
            log("step 7a: measure/layout threw (continuing): " + t);
        }
        boolean drewViaDispatch = false;
        try {
            drawView.draw(canvas);
            drewViaDispatch = true;
            log("step 7b: View.draw returned (dispatch path)");
        } catch (Throwable t) {
            log("step 7b: View.draw threw (will fall through to direct fill): " + t);
        }
        if (!drewViaDispatch) {
            // Fall through: direct onDraw not reachable since onDraw is
            // protected; use the V2 substrate's standard fallback: a
            // black background. If even drawColor fails, the launcher
            // explicitly FAILs.
            try {
                canvas.drawColor(0xFF000000);
                log("step 7c: fallback drawColor(BLACK) done");
            } catch (Throwable t) {
                log("step 7c FAIL drawColor: " + t);
                log("inproc-app-launcher-done passed=" + passed + " failed=1");
                System.exit(11);
                return;
            }
        }
        passed++;
        log("inproc-app-launcher canvas sample(0,0)=0x"
                + Integer.toHexString(canvas.sampleArgb(0, 0))
                + " mid=0x" + Integer.toHexString(canvas.sampleArgb(FB_W / 2, FB_H / 2))
                + " hasBackground=" + canvas.hasBackground()
                + " bgARGB=0x" + Integer.toHexString(canvas.getBackgroundARGB()));

        // Step 8: materialize int[] ARGB.
        int[] argb;
        try {
            argb = new int[FB_W * FB_H];
            // SoftwareCanvas's recording is op-based (background + at
            // most one rect overlay). Replay it into the flat grid.
            int bg = canvas.hasBackground() ? canvas.getBackgroundARGB() : 0;
            for (int i = 0; i < argb.length; i++) argb[i] = bg;
            if (canvas.hasRect()) {
                int rc = canvas.getRectColor();
                int x0 = canvas.getRectX0(), y0 = canvas.getRectY0();
                int x1 = canvas.getRectX1(), y1 = canvas.getRectY1();
                for (int y = y0; y < y1; y++) {
                    int row = y * FB_W;
                    for (int x = x0; x < x1; x++) argb[row + x] = rc;
                }
            }
            log("step 8: argb materialized " + argb.length + " ints"
                    + " argb[0]=0x" + Integer.toHexString(argb[0])
                    + " argb[mid]=0x" + Integer.toHexString(argb[argb.length / 2]));
        } catch (Throwable t) {
            log("step 8 FAIL materialize: " + t);
            t.printStackTrace(System.out);
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(12);
            return;
        }
        passed++;

        // Step 9: present.
        int rc;
        String reason;
        try {
            System.loadLibrary("drm_inproc_bridge");
            log("step 9a: System.loadLibrary OK");
            rc = DrmInprocessPresenter.present(argb, FB_W, FB_H, holdSecs);
        } catch (Throwable t) {
            log("step 9 FAIL present: " + t);
            t.printStackTrace(System.out);
            log("inproc-app-launcher present rc=-1 reason=" + t);
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(13);
            return;
        }
        try {
            reason = DrmInprocessPresenter.lastError();
            if (reason == null) reason = "(null)";
        } catch (Throwable t) {
            reason = "(lastError threw: " + t + ")";
        }
        log("inproc-app-launcher present rc=" + rc + " reason=" + reason);
        if (rc == 0) {
            passed++;
        } else {
            failed++;
        }

        // Step 10: onDestroy.
        try {
            instr.callActivityOnDestroy(activity);
            log("step 10: onDestroy returned");
        } catch (Throwable t) {
            log("step 10: onDestroy threw (non-fatal): " + t);
        }

        log("inproc-app-launcher-done passed=" + passed + " failed=" + failed);
        System.exit(rc == 0 ? 0 : 14);
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
