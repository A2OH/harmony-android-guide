// SPDX-License-Identifier: Apache-2.0
//
// OhosMvpLauncher — MVP-1 minimal Activity launcher for OHOS (#619).
//
// Goal: prove an Android Activity's onCreate() runs on the OHOS board
// via Westlake's dalvik-kitkat VM + V2 substrate (aosp-shim-ohos.dex
// on BCP).
//
// Contract (memory/feedback_macro_shim_contract.md):
//   - ZERO Unsafe.allocateInstance
//   - ZERO Field/Method.setAccessible(true)
//   - ZERO per-app branches
//   - Only calls public API methods on classes we own (Activity,
//     Instrumentation, Bundle).
//
// Approach: keep it as flat as the AOSP launch path allows.
//
//   1. Parse argv: "<package>/<class>" (matches `adb am start` form).
//   2. Resolve class via ClassLoader (BCP has aosp-shim-ohos.dex which
//      provides Activity; the app dex is also on BCP for MVP-1, so the
//      system classloader can find it without DexClassLoader gymnastics).
//   3. Instantiate via Activity's public no-arg constructor.
//   4. Drive onCreate via Instrumentation.callActivityOnCreate(Activity,
//      Bundle) — Instrumentation is in android.app so it has same-package
//      access to Activity.onCreate (which is protected).
//   5. Print breadcrumbs at every step so the next agent has a clear log
//      of where any future blocker manifested.
//
// Marker contract: do NOT depend on this class to print the marker.
// MainActivity.onCreate prints it via android.util.Log / System.out.

package com.westlake.ohostests.launcher;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;

public final class OhosMvpLauncher {

    private static final String TAG = "OhosMvpLauncher";

    public static void main(String[] args) {
        log("step 0: enter; argc=" + (args == null ? -1 : args.length));

        // ---- Step 1: parse args --------------------------------------
        if (args == null || args.length < 1) {
            log("step 0.5: missing arg; usage: <package>/<MainActivityClass>");
            System.exit(2);
            return;
        }
        String spec = args[0];
        String className = spec;
        int slash = spec.indexOf('/');
        if (slash >= 0) {
            String pkg = spec.substring(0, slash);
            String cls = spec.substring(slash + 1);
            // Allow short form (".MainActivity") for shell-friendly spec.
            if (cls.startsWith(".")) {
                className = pkg + cls;
            } else {
                className = cls;
            }
            log("step 1: package=" + pkg + " class=" + className);
        } else {
            log("step 1: bare-class spec; class=" + className);
        }

        // ---- Step 2: resolve Activity class --------------------------
        Class<?> activityClass;
        try {
            activityClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log("step 2: ClassNotFoundException for '" + className + "': " + e);
            System.exit(3);
            return;
        }
        log("step 2: class loaded: " + activityClass.getName()
                + " superclass=" + activityClass.getSuperclass().getName());

        // ---- Step 3: instantiate via no-arg ctor ---------------------
        // Activity declares a public no-arg constructor; this is a normal
        // newInstance call — no setAccessible required.
        Activity activity;
        try {
            Object obj = activityClass.newInstance();
            if (!(obj instanceof Activity)) {
                log("step 3: class is not an android.app.Activity: " + obj.getClass());
                System.exit(4);
                return;
            }
            activity = (Activity) obj;
        } catch (Throwable t) {
            log("step 3: newInstance failed: " + t);
            t.printStackTrace();
            System.exit(5);
            return;
        }
        log("step 3: activity instantiated: " + activity.getClass().getName());

        // ---- Step 4: drive onCreate via Instrumentation -------------
        Instrumentation instr = new Instrumentation();
        Bundle savedState = null;  // Cold start — no saved instance state.
        try {
            log("step 4: calling Instrumentation.callActivityOnCreate(...)");
            instr.callActivityOnCreate(activity, savedState);
        } catch (Throwable t) {
            log("step 4: callActivityOnCreate threw: " + t);
            t.printStackTrace();
            System.exit(6);
            return;
        }
        log("step 4: callActivityOnCreate returned cleanly");

        // ---- Step 5: drive onDestroy (graceful unwind) ---------------
        try {
            log("step 5: calling Instrumentation.callActivityOnDestroy(...)");
            instr.callActivityOnDestroy(activity);
        } catch (Throwable t) {
            log("step 5: callActivityOnDestroy threw (non-fatal): " + t);
        }

        log("step 6: launcher complete; exit 0");
        System.exit(0);
    }

    private static void log(String msg) {
        // Use System.out which is wired to DirectPrintStream(fd=1) by
        // dvmInitMain — bypasses the broken System.<clinit> path.
        System.out.println("[" + TAG + "] " + msg);
    }
}
