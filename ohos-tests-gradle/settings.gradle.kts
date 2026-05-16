// ohos-tests-gradle — Phase 2 OHOS MVP test artifacts.
//
// [SUPERSEDED-V3 2026-05-16] Most modules archived per W13 to
// archive/v2-ohos-substrate/ohos-tests-gradle/. See
// docs/engine/V3-W11-CARRYFORWARD-AUDIT.md §1.2 and
// docs/engine/V3-W12-CR61.1-CODE-DISPOSITION.md §2.5.
// Only :xcomponent-test remains active (CR60-followup; HBC-integration
// forensic test, independent of CR61 prohibition).
//
// Active modules:
//   :xcomponent-test      — CR60 follow-up: in-process OHOS NDK API call
//                           (proves dlopen produces working function pointers,
//                           not just resolved symbols). Plain-Java module
//                           whose static main is run under dalvikvm-arm32-dyn.
//
// Archived modules (V2-OHOS substrate; superseded by HBC framework.jar
// + real libhwui under V3 — see V3-ARCHITECTURE.md §4):
//   :hello                — MVP-0 single-class Java marker
//   :trivial-activity     — MVP-1 single-Activity APK
//   :launcher             — OhosMvpLauncher (replaced by HBC `aa start <bundle>`)
//   :red-square           — MVP-2 visible-pixels APK
//   :m6-test              — M6 DRM daemon Java client + driver
//   :hello-color-apk      — CR60-followup E12 smoke APK
//   :inproc-app-launcher  — CR60-followup E12 in-process Activity launcher
//                           (SoftwareCanvas + DrmInprocessPresenter)
//
// To un-archive a module: `git mv archive/v2-ohos-substrate/ohos-tests-gradle/<mod>
// ohos-tests-gradle/<mod>` and uncomment the corresponding include() below.

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
// Archived per W13 (2026-05-16):
// include(":hello")
// include(":trivial-activity")
// include(":launcher")
// include(":red-square")
// include(":m6-test")
// include(":hello-color-apk")
// include(":inproc-app-launcher")
include(":xcomponent-test")
