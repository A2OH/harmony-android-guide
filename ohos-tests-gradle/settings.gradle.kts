// ohos-tests-gradle — Phase 2 OHOS MVP test artifacts.
//
// Modules:
//   :hello                — single-class Java for HelloOhos.dex (MVP-0, #616)
//   :trivial-activity     — single-Activity APK with no resources (MVP-1, #619)
//   :launcher             — OhosMvpLauncher (Activity-driver for MVP-1/2)
//   :red-square           — MVP-2 visible-pixels APK (PF-ohos-mvp-003)
//   :m6-test              — M6 DRM daemon Java client + driver (PF-ohos-m6-002)
//   :xcomponent-test      — CR60 follow-up: in-process OHOS NDK API call
//                           (proves dlopen produces working function pointers,
//                           not just resolved symbols). Plain-Java module
//                           whose static main is run under dalvikvm-arm32-dyn.
//   :hello-color-apk      — CR60-followup E12 smoke APK (2026-05-15): tiny
//                           Activity exposing a ColorView via the
//                           InProcDrawSource interface so the in-process
//                           launcher can drive its onDraw → BGRA → DRM.
//   :inproc-app-launcher  — CR60-followup E12 in-process Activity launcher
//                           (DexClassLoader-shaped; stage 1 keeps APK on
//                           -Xbootclasspath until the DexClassLoader gate
//                           is open). Hosts InProcDrawSource interface +
//                           DrmInprocessPresenter JNI shim.
//
// Mirrors multiproc-test-gradle/ layout; see /scripts/run-ohos-test.sh
// for the driver that compiles + pushes + runs these artifacts on the
// OHOS board (rk3568 dd011a414436314130101250040eac00).

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "OhosTests"
include(":hello")
include(":trivial-activity")
include(":launcher")
include(":red-square")
include(":m6-test")
include(":xcomponent-test")
include(":hello-color-apk")
include(":inproc-app-launcher")
