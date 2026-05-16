// :m6-test — M6 DRM daemon Java client + driving Activity
// (PF-ohos-m6-002).
//
// Builds:
//   com.westlake.ohostests.m6.M6DrmClient
//     — wraps UnixSocketBridge into a frame-submission API.
//   com.westlake.ohostests.m6.M6ClientTestActivity
//     — onCreate spawns a producer thread that submits ≥120 BGRA
//       frames (alternating RED/BLUE) to the daemon via M6DrmClient.
//   com.westlake.ohostests.m6.M6FramePainter
//     — fills a 720x1280 BGRA buffer via SoftwareCanvas (same
//       drawColor pattern as RedView).
//
// Mirrors :trivial-activity / :red-square layout. UnixSocketBridge
// lives on the BCP (shim) so compileOnly against android.jar +
// classpath reference to shim/java/ resolves it at compile time.
//
// We attach an extra source set fragment for the shim class because
// android.jar doesn't have com.westlake.compat.UnixSocketBridge —
// it ships in aosp-shim-ohos.dex on the board.

plugins {
    id("com.android.application")
}

android {
    namespace = "com.westlake.ohostests.m6"
    compileSdk = 28

    defaultConfig {
        applicationId = "com.westlake.ohostests.m6"
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

    sourceSets {
        getByName("main") {
            // Pull in the bridge class as compile-time reference. At
            // runtime UnixSocketBridge is resolved from the BCP-side
            // aosp-shim-ohos.dex, NOT bundled in this APK. This source
            // set entry is purely so javac can see the type signatures.
            //
            // src/m6-bridge-stub/ contains a single-file copy of the
            // UnixSocketBridge.java *interface only* (native method
            // declarations) so javac resolves the type at build time.
            // Keeping the stub in-tree under the test module avoids
            // dragging the whole shim/java/com/westlake/compat/ dir
            // into the test app's classpath.
            java.srcDirs("src/main/java", "src/m6-bridge-stub/java")
        }
    }
}
