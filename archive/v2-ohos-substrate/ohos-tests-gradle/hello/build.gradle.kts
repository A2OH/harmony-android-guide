// :hello — plain Java single-class for HelloOhos.dex (MVP-0, #616)
// plus HelloDlopen + HelloDlopenReal (CR60 follow-ups E7 + E9a).
//
// We only want .class files we can feed to d8; AGP is overkill here.
// The driver script (scripts/run-ohos-test.sh hello / hello-dlopen-real)
// invokes d8 on the compiled classes directory directly.

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

// HelloDlopenReal references XComponentBridge from the :xcomponent-test
// module (compile-time only; at runtime both classes are dex'd together
// by the driver). Using compileOnly keeps the produced classes
// directory small (no transitive copy).
dependencies {
    compileOnly(project(":xcomponent-test"))
}

// Convenience task: emit the classes/ directory as a sibling artifact
// path so the run script can find it deterministically.
tasks.register("printClassesDir") {
    doLast {
        println(layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
    }
}
