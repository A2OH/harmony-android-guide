// Root build script — declare the Android Gradle Plugin so :trivial-activity
// can use `com.android.application`. The :hello module uses the bare `java`
// plugin and doesn't depend on AGP.

plugins {
    id("com.android.application") version "8.2.0" apply false
}
