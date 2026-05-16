// :hello-color-apk — E12 smoke test APK (CR60 follow-up, 2026-05-15).
//
// Goal: a tiny non-Hilt, no-resources Android APK whose MainActivity
// produces a deterministic BGRA frame via a custom View's onDraw. The
// E12 inproc-app-launcher loads this Activity (initially via
// -Xbootclasspath / system classloader; ultimately via DexClassLoader
// once that gate is open) and presents the frame on the DAYU200 DSI
// panel through the in-process DRM bridge.
//
// Color is BLUE (0xFF0000FF) so it's visually distinct from E9b's
// hardcoded RED — proves the pixel really comes from the Activity's
// onDraw, not from a leftover code path in libdrm_inproc_bridge.so.
//
// Per the brief (CR60 E12): "If you build one yourself (10-LOC), that's
// also fine — call it :hello-color-apk in ohos-tests-gradle/". This
// module mirrors :trivial-activity's gradle shape verbatim (compileSdk=28,
// no resources, no buildConfig). Java side is < 50 LOC total across the
// Activity + the ColorView subclass.

plugins {
    id("com.android.application")
}

android {
    namespace = "com.westlake.ohostests.helloc"
    compileSdk = 28

    defaultConfig {
        applicationId = "com.westlake.ohostests.helloc"
        minSdk = 24
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    // Need the InProcDrawSource interface at compile time so MainActivity
    // can `implements InProcDrawSource`. compileOnly so the launcher's
    // own classes don't get bundled into the APK — at runtime the
    // launcher is co-resident in the same dalvikvm process and the
    // interface is reachable through the same classloader.
    compileOnly(project(":inproc-app-launcher"))
}
