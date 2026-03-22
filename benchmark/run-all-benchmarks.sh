#!/bin/bash
# Comprehensive VM Benchmark Runner
# Runs: ART Interpreter, ART Mixed (AOT boot + interp app), ART AOT, Host JVM
set -e

BENCH_DIR="/home/dspfac/android-to-openharmony-migration/benchmark"
ART_DIR="/home/dspfac/art-universal-build"
BCP="/home/dspfac/android-to-openharmony-migration/art-boot-image"
ART_JAR="$BENCH_DIR/benchmark-art.jar"

export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache/x86_64 $ANDROID_ROOT/bin

ART_BCP="$BCP/core-oj.jar:$BCP/core-libart.jar:$BCP/core-icu4j.jar:$BCP/bouncycastle.jar:$BCP/okhttp.jar:$BCP/apache-xml.jar"
ART_IMAGE="/tmp/art-boot-full/boot.art"

run_art_interp() {
    local run=$1
    echo ">>> ART Interpreter -- Run $run <<<"
    export LD_LIBRARY_PATH="$ART_DIR/build/lib:/home/dspfac/aosp-android-11/prebuilts/clang/host/linux-x86/clang-r383902b1/lib64"
    $ART_DIR/build/bin/dalvikvm \
        -Xbootclasspath:$ART_BCP \
        -Ximage:$ART_IMAGE \
        -Xverify:none -Xnoimage-dex2oat -Xnorelocate \
        -Xint \
        -classpath "$ART_JAR" \
        VMBenchmark 2>&1 | grep -E "^(METHOD|VIRTUAL|FIELD|FIBONACCI|TIGHT|OBJECT|ARRAY|STRING|HASHMAP)"
    echo ""
}

run_art_aot() {
    local run=$1
    echo ">>> ART AOT -- Run $run <<<"
    export LD_LIBRARY_PATH="$ART_DIR/build/lib:/home/dspfac/aosp-android-11/prebuilts/clang/host/linux-x86/clang-r383902b1/lib64"
    $ART_DIR/build/bin/dalvikvm \
        -Xbootclasspath:$ART_BCP \
        -Ximage:$ART_IMAGE \
        -Xverify:none -Xnorelocate \
        -classpath "$ART_JAR" \
        VMBenchmark 2>&1 | grep -E "^(METHOD|VIRTUAL|FIELD|FIBONACCI|TIGHT|OBJECT|ARRAY|STRING|HASHMAP)"
    echo ""
}

run_host_jvm() {
    local run=$1
    echo ">>> Host JVM -- Run $run <<<"
    java -cp "$BENCH_DIR/classes" VMBenchmark 2>&1 | grep -E "^(METHOD|VIRTUAL|FIELD|FIBONACCI|TIGHT|OBJECT|ARRAY|STRING|HASHMAP)"
    echo ""
}

echo "================================================================"
echo "  VM BENCHMARK SUITE -- $(date)"
echo "================================================================"
echo ""

echo "ART version:"
export LD_LIBRARY_PATH="$ART_DIR/build/lib:/home/dspfac/aosp-android-11/prebuilts/clang/host/linux-x86/clang-r383902b1/lib64"
$ART_DIR/build/bin/dalvikvm \
    -Xbootclasspath:$ART_BCP \
    -Ximage:$ART_IMAGE \
    -Xverify:none -Xnoimage-dex2oat -Xnorelocate \
    -Xint \
    -classpath "$ART_JAR" \
    -showversion VMBenchmark 2>&1 | grep -i "vm\." | head -3
echo ""

echo "Host JVM version:"
java -version 2>&1
echo ""

echo "============================================================"
echo "  ART INTERPRETER RUNS (pure switch interpreter)"
echo "============================================================"
for i in 1 2 3; do
    run_art_interp $i
done

echo "============================================================"
echo "  ART AOT RUNS (dex2oat compiled benchmark + boot image)"
echo "============================================================"
for i in 1 2 3; do
    run_art_aot $i
done

echo "============================================================"
echo "  HOST JVM RUNS (OpenJDK JIT reference)"
echo "============================================================"
for i in 1 2 3; do
    run_host_jvm $i
done

echo "============================================================"
echo "  ALL RUNS COMPLETE"
echo "============================================================"
