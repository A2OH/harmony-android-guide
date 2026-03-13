#!/bin/bash
# Build and deploy the headless CLI test to an OHOS device.
#
# Prerequisites:
#   - hdc (OHOS device connector) in PATH
#   - javac (JDK 8+)
#   - d8 (Android build tools) or dx for dex conversion
#   - liboh_bridge.so + liboh_cpp_shim.so built and available
#
# Usage:
#   ./build.sh          # build + deploy + run
#   ./build.sh build    # build only
#   ./build.sh deploy   # deploy to device only
#   ./build.sh run      # run on device only

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
SHIM_JAVA="$PROJECT_ROOT/shim/java"
BUILD_DIR="$SCRIPT_DIR/build"
DEVICE_DIR="/data/local/tmp/shim_test"

# ── Build ──

build() {
    echo "=== Building headless test ==="

    rm -rf "$BUILD_DIR"
    mkdir -p "$BUILD_DIR/classes"

    # Collect all Java source files (shim + test)
    JAVA_FILES=$(find "$SHIM_JAVA" -name "*.java")
    JAVA_FILES="$JAVA_FILES $SCRIPT_DIR/src/HeadlessTest.java"

    echo "Compiling $(echo "$JAVA_FILES" | wc -w) Java files..."
    javac -source 8 -target 8 \
        -d "$BUILD_DIR/classes" \
        -sourcepath "$SHIM_JAVA:$SCRIPT_DIR/src" \
        $JAVA_FILES 2>&1 || {
        echo "ERROR: javac failed"
        exit 1
    }

    echo "Compiled successfully."

    # Convert to DEX (if d8 is available)
    if command -v d8 &>/dev/null; then
        echo "Converting to DEX with d8..."
        d8 --output "$BUILD_DIR" \
            $(find "$BUILD_DIR/classes" -name "*.class")
        echo "DEX created: $BUILD_DIR/classes.dex"
    elif command -v dx &>/dev/null; then
        echo "Converting to DEX with dx..."
        dx --dex --output="$BUILD_DIR/classes.dex" "$BUILD_DIR/classes"
        echo "DEX created: $BUILD_DIR/classes.dex"
    else
        echo "WARNING: d8/dx not found — skipping DEX conversion."
        echo "  Classes are at: $BUILD_DIR/classes/"
        echo "  You can run directly with: java -cp $BUILD_DIR/classes HeadlessTest"
    fi
}

# ── Deploy to device ──

deploy() {
    echo "=== Deploying to OHOS device ==="

    if ! command -v hdc &>/dev/null; then
        echo "ERROR: hdc not found in PATH"
        exit 1
    fi

    hdc shell mkdir -p "$DEVICE_DIR"

    # Push DEX or class files
    if [ -f "$BUILD_DIR/classes.dex" ]; then
        hdc file send "$BUILD_DIR/classes.dex" "$DEVICE_DIR/classes.dex"
        echo "Pushed classes.dex"
    fi

    # Push native libraries
    RUST_LIB="$PROJECT_ROOT/shim/bridge/rust/target/aarch64-unknown-linux-ohos/release/liboh_bridge.so"
    CPP_LIB="$PROJECT_ROOT/shim/bridge/cpp/build/liboh_cpp_shim.so"

    if [ -f "$RUST_LIB" ]; then
        hdc file send "$RUST_LIB" "$DEVICE_DIR/liboh_bridge.so"
        echo "Pushed liboh_bridge.so"
    else
        echo "WARNING: liboh_bridge.so not found at $RUST_LIB"
    fi

    if [ -f "$CPP_LIB" ]; then
        hdc file send "$CPP_LIB" "$DEVICE_DIR/liboh_cpp_shim.so"
        echo "Pushed liboh_cpp_shim.so"
    else
        echo "WARNING: liboh_cpp_shim.so not found at $CPP_LIB"
    fi

    echo "Deployed to $DEVICE_DIR"
}

# ── Run on device ──

run() {
    echo "=== Running headless test on device ==="

    if ! command -v hdc &>/dev/null; then
        echo "ERROR: hdc not found in PATH"
        exit 1
    fi

    # Run via app_process (Dalvik/ART on OHOS)
    hdc shell "cd $DEVICE_DIR && \
        export LD_LIBRARY_PATH=$DEVICE_DIR:\$LD_LIBRARY_PATH && \
        app_process -Djava.library.path=$DEVICE_DIR \
            -cp $DEVICE_DIR/classes.dex \
            / HeadlessTest"
}

# ── Run locally (JVM, without native libs — pure Java tests only) ──

run_local() {
    echo "=== Running LOCAL (JVM) — pure Java tests only ==="
    echo "(Native JNI calls will fail — testing pure Java shim classes)"
    echo ""

    if [ -d "$BUILD_DIR/classes" ]; then
        java -cp "$BUILD_DIR/classes" HeadlessTest 2>&1 || true
    else
        echo "ERROR: build first with: $0 build"
        exit 1
    fi
}

# ── Main ──

case "${1:-all}" in
    build)   build ;;
    deploy)  deploy ;;
    run)     run ;;
    local)   build && run_local ;;
    all)     build && deploy && run ;;
    *)
        echo "Usage: $0 {build|deploy|run|local|all}"
        exit 1
        ;;
esac
