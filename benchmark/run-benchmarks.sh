#!/bin/bash
# Run VM benchmarks: Dalvik, ART interpreter, ART AOT
set -e

BENCH_DIR="$(cd "$(dirname "$0")" && pwd)"
DALVIK_DIR="/home/dspfac/android-to-openharmony-migration/dalvik-port"
ART_DIR="/home/dspfac/art-universal-build"
BCP="/home/dspfac/android-to-openharmony-migration/art-boot-image"

export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin

DALVIK_DEX="$BENCH_DIR/benchmark-dalvik.dex"
ART_JAR="$BENCH_DIR/benchmark-art.jar"

echo "================================================================"
echo "  VM BENCHMARK SUITE"
echo "================================================================"
echo ""

# ---- DALVIK ----
run_dalvik() {
    echo ">>> Dalvik KitKat (portable interpreter) -- Run $1 <<<"
    $DALVIK_DIR/build/dalvikvm -Xverify:none -Xdexopt:none \
        -Xbootclasspath:$DALVIK_DIR/core-android-x86.jar \
        -classpath "$DALVIK_DEX" \
        VMBenchmark 2>&1
    echo ""
}

# ---- ART Interpreter ----
run_art_interp() {
    echo ">>> ART Interpreter (switch interp, no AOT) -- Run $1 <<<"
    export LD_LIBRARY_PATH="$ART_DIR/build/lib:/home/dspfac/aosp-android-11/prebuilts/clang/host/linux-x86/clang-r383902b1/lib64"
    $ART_DIR/build/bin/dalvikvm \
        -Xbootclasspath:$BCP/core-oj.jar:$BCP/core-libart.jar:$BCP/core-icu4j.jar \
        -Ximage:/tmp/art-boot-out/boot.art \
        -Xverify:none -Xnoimage-dex2oat -Xnorelocate \
        -Xint \
        -classpath "$ART_JAR" \
        VMBenchmark 2>&1
    echo ""
}

# ---- ART AOT ----
run_art_aot() {
    echo ">>> ART AOT (dex2oat compiled) -- Run $1 <<<"
    export LD_LIBRARY_PATH="$ART_DIR/build/lib:/home/dspfac/aosp-android-11/prebuilts/clang/host/linux-x86/clang-r383902b1/lib64"
    $ART_DIR/build/bin/dalvikvm \
        -Xbootclasspath:$BCP/core-oj.jar:$BCP/core-libart.jar:$BCP/core-icu4j.jar \
        -Ximage:/tmp/art-boot-out/boot.art \
        -Xverify:none -Xnorelocate \
        -classpath "$ART_JAR" \
        VMBenchmark 2>&1
    echo ""
}

# Try AOT compile first
compile_aot() {
    echo ">>> Compiling benchmark with dex2oat (AOT) <<<"
    export LD_LIBRARY_PATH="$ART_DIR/build/lib:/home/dspfac/aosp-android-11/prebuilts/clang/host/linux-x86/clang-r383902b1/lib64"
    mkdir -p /tmp/bench-oat
    $ART_DIR/build/bin/dex2oat \
        --dex-file="$ART_JAR" \
        --oat-file=/tmp/bench-oat/benchmark.oat \
        --boot-image=/tmp/art-boot-out/boot.art \
        --instruction-set=x86_64 \
        --compiler-filter=speed \
        --runtime-arg -Xverify:none \
        -j4 2>&1
    echo "AOT compile done"
    echo ""
}

echo "============================================================"
echo "  DALVIK RUNS"
echo "============================================================"
for i in 1 2 3; do
    run_dalvik $i
done

echo "============================================================"
echo "  ART INTERPRETER RUNS"
echo "============================================================"
for i in 1 2 3; do
    run_art_interp $i
done

echo "============================================================"
echo "  ART AOT COMPILATION"
echo "============================================================"
compile_aot || echo "AOT compilation failed, skipping AOT runs"

echo "============================================================"
echo "  ART AOT RUNS"
echo "============================================================"
for i in 1 2 3; do
    run_art_aot $i
done

echo "============================================================"
echo "  ALL RUNS COMPLETE"
echo "============================================================"
