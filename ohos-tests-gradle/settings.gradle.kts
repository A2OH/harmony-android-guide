// ohos-tests-gradle — Phase 2 OHOS MVP test artifacts.
//
// Modules:
//   :hello             — single-class Java for HelloOhos.dex (MVP-0, #616)
//   :trivial-activity  — single-Activity APK with no resources (MVP-1, #619)
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
