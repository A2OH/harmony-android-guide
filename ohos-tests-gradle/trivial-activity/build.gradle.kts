// :trivial-activity — single-Activity Android APK (MVP-1, #619).
//
// Per #619: "Single-Activity APK that logs marker in onCreate then
// finishes. Mirrors multiproc-test-gradle pattern. < 100KB APK."
//
// targetSdk = 28 per the MVP brief (lowest stable Android-on-Westlake
// surface; matches what dalvik-port currently boots).

plugins {
    id("com.android.application")
}

android {
    namespace = "com.westlake.ohostests.trivial"
    compileSdk = 28

    defaultConfig {
        applicationId = "com.westlake.ohostests.trivial"
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
        // No resources — keep APK minimal per #619.
        buildConfig = false
    }
}
