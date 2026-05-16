// :launcher — minimal OhosMvpLauncher for MVP-1 (#619).
//
// Plain Java; we only want .class files we can feed to d8/dx into the
// MVP-1 launcher dex. The driver script (scripts/run-ohos-test.sh
// trivial-activity) invokes d8 on the compiled classes directory.
//
// The Android API surface (Activity, Bundle, Instrumentation) is
// resolved against android.jar; classes are NOT bundled in the launcher
// dex — at runtime they come from aosp-shim-ohos.dex on the BCP.

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // Provide Android API for compile-time only (not packaged).
    compileOnly(files("/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar"))
}

tasks.register("printClassesDir") {
    doLast {
        println(layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
    }
}
