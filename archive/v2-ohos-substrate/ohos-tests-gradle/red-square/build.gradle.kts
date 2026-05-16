// :red-square — MVP-2 visible-pixel APK (PF-ohos-mvp-003).
//
// Paints a red square onto the DAYU200's HDMI display by:
//   1. Declaring a RedView whose onDraw(Canvas) calls canvas.drawColor(RED).
//   2. Passing a SoftwareCanvas (heap-int[] backed) to View.draw(canvas).
//   3. Writing the int[] to /dev/graphics/fb0 via libcore.io.Libcore.os.
//
// Mirrors :trivial-activity layout. Same min/target SDK (24/28), no resources,
// no permissions (root shell + /dev/graphics/fb0 0666 makes that moot).

plugins {
    id("com.android.application")
}

android {
    namespace = "com.westlake.ohostests.red"
    compileSdk = 28

    defaultConfig {
        applicationId = "com.westlake.ohostests.red"
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
        // No resources — same as :trivial-activity.
        buildConfig = false
    }
}
