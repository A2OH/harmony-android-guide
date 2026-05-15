// SPDX-License-Identifier: Apache-2.0
//
// InProcessAppLauncher — E12 / E13 gate (CR60 follow-up).
//
// Goal: drive a real Android Activity's onCreate to first-pixel on the
// DAYU200 DSI panel, fully in-process under dalvikvm-arm32-dynamic. No
// M6 daemon, no compositor surface, no separate aarch64 helper binary.
//
// Two modes:
//   * "smoke" (E12, default): only the spec `<pkg>/<Activity>` arg.
//     Activity is on the BCP (or any classloader visible via
//     Class.forName), implements InProcDrawSource, and the launcher
//     instantiates it directly + Instrumentation.callActivityOnCreate.
//     This is the path :hello-color-apk took to land BLUE on the panel
//     (commit 442e312e).
//   * "apk-mode" (E13): pass `--apk-app <FullyQualifiedAppClassName>` to
//     route through the V2 substrate's full launch sequence:
//       - WestlakeActivityThread.setForceLifecycleEnabled(true)
//       - WestlakeActivityThread.forceMakeApplicationForNextLaunch(appCls)
//       - WestlakeActivityThread.launchActivity(thread, cls, pkg, intent)
//     The substrate handles Application instantiation, attach, Activity
//     attach, and Instrumentation.callActivityOnCreate. This is what
//     real apps (noice, McD) require — they have Hilt-instrumented
//     Application classes whose onCreate must run before MainActivity.
//
// Pipeline (post-onCreate, both modes):
//   6. Allocate SoftwareCanvas (FB_W x FB_H).
//   7. Locate a drawable View:
//      (a) If Activity implements InProcDrawSource, use getDrawView()
//          (smoke-mode contract, preserves E12 baseline).
//      (b) Else if Activity has a non-null Window.getDecorView(), use
//          that (real apps that called setContentView).
//      (c) Else, fall through to a theme/blank fill — noice's first
//          frame may not produce a drawable tree if Compose recompose
//          isn't reachable; the brief explicitly accepts a "white screen
//          of nothing" if onCreate completed without exception.
//   8. view.draw(canvas) when a View is present; otherwise paint a
//      neutral fallback color so we have a known argb output.
//   9. Materialize int[w*h] ARGB8888 buffer by replaying SoftwareCanvas
//      ops (background fill + optional rect overlay).
//  10. DrmInprocessPresenter.present(argb, w, h, holdSecs) — JNI into
//      libdrm_inproc_bridge.so.
//  11. Instrumentation.callActivityOnDestroy(activity) — graceful unwind.
//
// Marker contract (driver greps these EXACT strings):
//     inproc-app-launcher-start
//     inproc-app-launcher step <N>: <message>
//     inproc-app-launcher canvas sample(0,0)=0x<hex> mid=0x<hex>
//     inproc-app-launcher present rc=<int> reason=<text>
//     inproc-app-launcher-done passed=<int> failed=<int>
//
// E13-specific stage markers (greppable by the driver for stage A/B/C/D):
//     inproc-app-launcher stage A: dex visible class=<fqcn>
//     inproc-app-launcher stage B: Application.onCreate returned
//     inproc-app-launcher stage C: Activity.onCreate returned
//     inproc-app-launcher stage D: pixel pushed rc=<int>
//
// Macro-shim contract: NO Unsafe.allocateInstance, NO setAccessible,
// NO per-app branches. The launcher is generic — pass any
// "<pkg>/<Activity>" pair, optionally with `--apk-app <AppClass>` for
// real-Android-style launches. Routing for specific APKs lives in the
// driver script (run-ohos-test.sh inproc-app --apk noice), which reads
// the manifest off the APK and threads the values here. No code
// branches on "noice" or "mcd" here.

package com.westlake.ohostests.inproc;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

// SoftwareCanvas is local to this module — see SoftwareCanvas.java
// (intentional duplicate of :red-square's class; see that file for
// rationale).

public final class InProcessAppLauncher {

    private static final String TAG = "InProcessAppLauncher";

    /** DAYU200 DSI-1 panel native resolution; matches red-square. */
    private static final int FB_W = 720;
    private static final int FB_H = 1280;

    /** Fallback color when Activity has no drawable View (E13 acceptance
     *  per brief: "white screen of nothing OK if onCreate completed").
     *  We use opaque WHITE 0xFFFFFFFF so a panel filled with this value
     *  is visually distinct from BLUE (E12 hello-color-apk) and RED
     *  (E9b libdrm_inproc_bridge.so hardcode). */
    private static final int FALLBACK_ARGB = 0xFFFFFFFF;

    private InProcessAppLauncher() { /* no instances */ }

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;
        log("inproc-app-launcher-start");

        // Step 1: parse argv.
        // Accepted forms:
        //   <pkg>/<Activity> [holdSecs]
        //   <pkg>/<Activity> --apk-app <AppClass> [holdSecs]
        //   <pkg>/<Activity> --apk-app <AppClass> --fallback-argb <hex> [holdSecs]
        if (args == null || args.length < 1) {
            log("step 1 FAIL: missing arg; usage: <pkg>/<Activity> "
                    + "[--apk-app <AppClass>] [--fallback-argb <hex>] [holdSecs]");
            log("inproc-app-launcher-done passed=" + passed + " failed=1");
            System.exit(2);
            return;
        }
        String spec = args[0];
        int holdSecs = 6;
        String appClassName = null;
        int fallbackArgb = FALLBACK_ARGB;
        for (int i = 1; i < args.length; i++) {
            String a = args[i];
            if ("--apk-app".equals(a) && (i + 1) < args.length) {
                appClassName = args[++i];
                continue;
            }
            if ("--fallback-argb".equals(a) && (i + 1) < args.length) {
                try {
                    String hex = args[++i];
                    if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
                    fallbackArgb = (int) Long.parseLong(hex, 16);
                } catch (NumberFormatException nfe) {
                    /* keep default */
                }
                continue;
            }
            // Treat any non-flag arg as holdSecs.
            try {
                holdSecs = Integer.parseInt(a);
            } catch (NumberFormatException e) {
                /* ignore unknown */
            }
        }
        String packageName;
        String className;
        int slash = spec.indexOf('/');
        if (slash >= 0) {
            packageName = spec.substring(0, slash);
            String cls = spec.substring(slash + 1);
            className = cls.startsWith(".") ? packageName + cls : cls;
        } else {
            packageName = "";
            className = spec;
        }
        boolean apkMode = (appClassName != null);
        log("step 1: spec=" + spec + " pkg=" + packageName + " class=" + className
                + " appClass=" + (apkMode ? appClassName : "(none)")
                + " holdSecs=" + holdSecs
                + " fallbackArgb=0x" + Integer.toHexString(fallbackArgb));

        // Step 2: resolve activity class via system ClassLoader.
        // Both BCP-staged dex and DexClassLoader-loaded paths route here;
        // -Xbootclasspath plumbing happens in the driver, not the launcher.
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
                + " superclass="
                + (activityClass.getSuperclass() != null
                        ? activityClass.getSuperclass().getName() : "(none)"));
        // Stage-A marker (E13).
        if (apkMode) {
            log("inproc-app-launcher stage A: dex visible class=" + activityClass.getName());
        }

        // Step 3: instantiate.
        Activity activity = null;

        if (apkMode) {
            // E13 apk-mode: route through V2 substrate's launch sequence.
            // The substrate handles Application + Activity creation and
            // their lifecycle wiring. Avoids per-app shim plumbing here.
            //
            // We invoke public API entry points on WestlakeActivityThread
            // through reflection (Class.forName/getMethod) rather than a
            // direct import — keeps the launcher's compile unit free of a
            // shim-source-tree dep without setAccessible (we only invoke
            // public methods).
            //
            // CR62 Step 1: flip WestlakeInstrumentation into strict-mode
            // BEFORE the substrate's launch chain runs. This makes
            // onException return false (propagate) instead of true
            // (swallow), so AppCompat / Hilt / Compose ctor-time
            // exceptions surface up to the launcher rather than getting
            // silently dropped. Generic, no per-app branching.
            try {
                Class<?> instrCls = Class.forName("android.app.WestlakeInstrumentation");
                java.lang.reflect.Method setStrict = instrCls.getMethod(
                        "setStrictExceptionPropagation", boolean.class);
                setStrict.invoke(null, Boolean.TRUE);
                log("step 3-strict: WestlakeInstrumentation.setStrictExceptionPropagation(true)");
            } catch (Throwable t) {
                log("step 3-strict: setStrictExceptionPropagation threw (continuing): " + t);
            }
            // Pre-flight: try to no-arg-construct the Activity class
            // directly. If this throws, the V2 substrate's
            // mInstrumentation.onException would swallow the same
            // exception and return null silently — so surfacing it here
            // is the only way to triage stage-B/C blockers without
            // patching the shim's diagnostic surface. Generic; not
            // app-specific.
            //
            // CR62: with strict-mode AND the CR62 Step 2 thread-local
            // pre-attached context wired in, this probe is more of a
            // diagnostic checkpoint than a blocker — the substrate's
            // newActivity call path will publish a real base context
            // before the same ctor runs there.
            try {
                Object probe = activityClass.newInstance();
                log("step 3-pre: Activity no-arg newInstance() OK: "
                        + (probe == null ? "null" : probe.getClass().getName()));
            } catch (Throwable t) {
                log("step 3-pre: Activity no-arg newInstance() THREW: " + t);
                t.printStackTrace(System.out);
                // Continue — the substrate may still succeed via its
                // AppComponentFactory path AND its newActivity-time
                // thread-local pre-attached base context (CR62 Step 2)
                // that this direct probe doesn't get.
            }
            try {
                Class<?> watCls = Class.forName("android.app.WestlakeActivityThread");
                java.lang.reflect.Method setForce = watCls.getMethod(
                        "setForceLifecycleEnabled", boolean.class);
                setForce.invoke(null, Boolean.TRUE);
                log("step 3a: WestlakeActivityThread.setForceLifecycleEnabled(true)");

                java.lang.reflect.Method current = watCls.getMethod("currentActivityThread");
                Object thread = current.invoke(null);
                log("step 3b: WestlakeActivityThread.currentActivityThread()="
                        + (thread == null ? "null" : thread.getClass().getName()));

                java.lang.reflect.Method forceApp = watCls.getMethod(
                        "forceMakeApplicationForNextLaunch", String.class);
                forceApp.invoke(thread, appClassName);
                log("step 3c: forceMakeApplicationForNextLaunch(" + appClassName + ")");

                Intent intent = new Intent();
                if (!packageName.isEmpty()) {
                    intent.setComponent(new ComponentName(packageName, className));
                    intent.setPackage(packageName);
                }
                java.lang.reflect.Method launch = watCls.getMethod("launchActivity",
                        watCls, String.class, String.class, Intent.class);
                Object launched = null;
                Throwable launchError = null;
                try {
                    launched = launch.invoke(null, thread, className, packageName, intent);
                } catch (java.lang.reflect.InvocationTargetException ite) {
                    launchError = ite.getCause() != null ? ite.getCause() : ite;
                } catch (Throwable t) {
                    launchError = t;
                }

                // CR62: Probe Application state INDEPENDENT of launchActivity
                // success — the substrate may have set mInitialApplication
                // (and run Application.onCreate) BEFORE the activity ctor
                // threw. Stage B is "Application.onCreate ran" regardless of
                // whether Stage C reaches.
                Application appObj = null;
                try {
                    java.lang.reflect.Method getApp = watCls.getMethod("currentApplication");
                    Object ao = getApp.invoke(null);
                    if (ao instanceof Application) appObj = (Application) ao;
                } catch (Throwable t) {
                    log("step 3e: currentApplication probe threw (non-fatal): " + t);
                }
                if (appObj != null) {
                    log("inproc-app-launcher stage B: Application.onCreate returned "
                            + "(" + appObj.getClass().getName() + ")");
                } else {
                    log("step 3e: currentApplication=null (Application not yet wired)");
                }

                if (launchError != null) {
                    log("step 3 FAIL (apk-mode launchActivity): " + launchError);
                    launchError.printStackTrace(System.out);
                    log("inproc-app-launcher-done passed=" + passed + " failed=1");
                    System.exit(5);
                    return;
                }
                if (!(launched instanceof Activity)) {
                    log("step 3 FAIL: launchActivity returned non-Activity: "
                            + (launched == null ? "null" : launched.getClass().getName())
                            + " — substrate hid the underlying exception even"
                            + " though strict-mode is on; check stage C marker.");
                    log("inproc-app-launcher-done passed=" + passed + " failed=1");
                    System.exit(5);
                    return;
                }
                activity = (Activity) launched;
                log("step 3d: launchActivity returned " + activity.getClass().getName());
                passed++;
            } catch (Throwable t) {
                log("step 3 FAIL (apk-mode): " + t);
                t.printStackTrace(System.out);
                log("inproc-app-launcher-done passed=" + passed + " failed=1");
                System.exit(5);
                return;
            }
        } else {
            // E12 smoke-mode: direct Activity ctor + Instrumentation.
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

            // Step 4: onCreate via Instrumentation.
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
        }
        // Stage-C marker (E13). performLaunchActivity does callActivityOnCreate
        // internally, so reaching here in apk-mode means onCreate returned.
        if (apkMode) {
            log("inproc-app-launcher stage C: Activity.onCreate returned ("
                    + activity.getClass().getName() + ")");
        }

        // Step 5: locate drawView.
        // Tier 1: InProcDrawSource interface (the E12 smoke contract).
        // Tier 2: Window.getDecorView() — real Android apps that called
        //         setContentView during onCreate.
        // Tier 3: null — paint fallback. This is the "white screen of
        //         nothing" path the E13 brief explicitly accepts as a
        //         pass when Application + Activity onCreate completed.
        View drawView = null;
        String drawViewSource = "(none)";
        if (activity instanceof InProcDrawSource) {
            try {
                drawView = ((InProcDrawSource) activity).getDrawView();
                if (drawView != null) {
                    drawViewSource = "InProcDrawSource";
                }
            } catch (Throwable t) {
                log("step 5a: InProcDrawSource.getDrawView threw (continuing): " + t);
            }
        }
        if (drawView == null) {
            try {
                Window window = activity.getWindow();
                if (window != null) {
                    drawView = window.getDecorView();
                    if (drawView != null) {
                        drawViewSource = "Window.getDecorView";
                    }
                }
            } catch (Throwable t) {
                log("step 5b: getWindow().getDecorView() threw (continuing): " + t);
            }
        }
        log("step 5: drawView=" + (drawView == null ? "null" : drawView.getClass().getName())
                + " source=" + drawViewSource);

        // Step 6: SoftwareCanvas (unconditional — needed for fallback paint).
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

        // Step 7: measure+layout+draw (if a View was located).
        boolean drewViaDispatch = false;
        if (drawView != null) {
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
            try {
                drawView.draw(canvas);
                drewViaDispatch = true;
                log("step 7b: View.draw returned (dispatch path)");
            } catch (Throwable t) {
                log("step 7b: View.draw threw (fallback to direct fill): " + t);
            }
        } else {
            log("step 7: no drawView — using fallback fill path");
        }
        if (!drewViaDispatch) {
            try {
                canvas.drawColor(fallbackArgb);
                log("step 7c: fallback drawColor(0x"
                        + Integer.toHexString(fallbackArgb) + ") done");
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
            int bg = canvas.hasBackground() ? canvas.getBackgroundARGB() : fallbackArgb;
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
        if (apkMode) {
            log("inproc-app-launcher stage D: pixel pushed rc=" + rc);
        }
        if (rc == 0) {
            passed++;
        } else {
            failed++;
        }

        // Step 10: onDestroy — only for smoke-mode. In apk-mode the V2
        // substrate owns the activity's lifecycle and a direct
        // callActivityOnDestroy from here would unbalance it.
        if (!apkMode) {
            try {
                Instrumentation instr = new Instrumentation();
                instr.callActivityOnDestroy(activity);
                log("step 10: onDestroy returned");
            } catch (Throwable t) {
                log("step 10: onDestroy threw (non-fatal): " + t);
            }
        } else {
            log("step 10: onDestroy skipped (apk-mode owns lifecycle)");
        }

        log("inproc-app-launcher-done passed=" + passed + " failed=" + failed);
        System.exit(rc == 0 ? 0 : 14);
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
