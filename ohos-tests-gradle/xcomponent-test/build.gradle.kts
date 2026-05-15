// :xcomponent-test — CR60 follow-up: in-process OHOS NDK API call gate.
//
// Pure Java module (mirrors :hello shape; no AGP / APK). The Java side
// declares native methods, loads libxcomponent_bridge.so via
// System.loadLibrary, and reports back the tier-1..3 outcomes via
// stdout markers the driver script greps.
//
// Single class (XComponentTest with the static main entry + nested
// XComponentBridge for the native declarations). The driver d8s the
// classes/ directory into a dex.

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register("printClassesDir") {
    doLast {
        println(layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
    }
}
