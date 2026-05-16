// :inproc-app-launcher — E12 in-process app launcher (CR60 follow-up).
//
// Mirrors :launcher (OhosMvpLauncher) but with the full in-process
// pipeline wired:
//
//   1. parse argv: "<package>/<MainActivityClass>" (same convention as MVP-1)
//   2. (E12 stage 1: APK on -Xbootclasspath; system classloader does
//      Class.forName.)
//      (E12 stage 2: DexClassLoader the APK; resolve MainActivity through
//      it. Not implemented yet in stage 1.)
//   3. instantiate via no-arg ctor.
//   4. Instrumentation.callActivityOnCreate(activity, null).
//   5. Locate the drawable View on the Activity. Stage 1 reads a public
//      field 'drawView' on the Activity (macro-shim-contract-compliant —
//      no reflection setAccessible) — see :hello-color-apk MainActivity.
//   6. Allocate :red-square SoftwareCanvas (FB_W x FB_H).
//   7. view.draw(canvas) — drives onDraw, records the background color.
//   8. Materialize an int[w*h] ARGB8888 buffer by sampling the canvas.
//   9. DrmInprocessPresenter.present(argb, w, h, holdSecs) — loads
//      libdrm_inproc_bridge.so and JNI-presents the buffer on the DSI
//      panel via the same DRM/KMS pipeline E9b proved.
//  10. Instrumentation.callActivityOnDestroy for graceful unwind.
//
// Plain-java module so the gradle build emits a clean classes/ tree the
// driver can d8 into a dex. Android API surface is resolved against
// android.jar at compile time only.

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // Android API for compile-time only — not packaged. Resolution at
    // runtime is via aosp-shim-ohos.dex on the BCP (Activity,
    // Instrumentation, View, Canvas, ...).
    compileOnly(files("/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar"))
    // SoftwareCanvas is duplicated locally in this module (see
    // SoftwareCanvas.java) — gradle can't pull the class out of
    // :red-square (Android application module, not a Java library) and
    // a separate :util module is overkill for a 110-LOC value type.
    // The duplicate compiles into its own dex and is the type the E12
    // pipeline uses; :red-square continues to use its own copy
    // independently.
}

tasks.register("printClassesDir") {
    doLast {
        println(layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
    }
}
