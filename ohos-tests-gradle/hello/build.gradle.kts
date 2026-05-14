// :hello — plain Java single-class for HelloOhos.dex (MVP-0, #616).
//
// We only want a .class file we can feed to d8; AGP is overkill here.
// The driver script (scripts/run-ohos-test.sh hello) invokes d8 on the
// compiled classes directory directly.

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

// Convenience task: emit the classes/ directory as a sibling artifact
// path so the run script can find it deterministically.
tasks.register("printClassesDir") {
    doLast {
        println(layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
    }
}
