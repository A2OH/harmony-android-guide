#!/bin/bash
# Build and deploy the UI mockup test app.
#
# Usage:
#   ./build.sh              # build + deploy + run on device
#   ./build.sh build        # build only
#   ./build.sh headless     # build + run locally in headless mode (no device needed)
#   ./build.sh deploy       # deploy to OHOS device
#   ./build.sh run          # run on device with ArkUI rendering

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
SHIM_JAVA="$PROJECT_ROOT/shim/java"
BUILD_DIR="$SCRIPT_DIR/build"
DEVICE_DIR="/data/local/tmp/shim_ui_test"

build() {
    echo "=== Building UI mockup test ==="

    rm -rf "$BUILD_DIR"
    mkdir -p "$BUILD_DIR/classes"

    JAVA_FILES=$(find "$SHIM_JAVA" -name "*.java")
    JAVA_FILES="$JAVA_FILES $SCRIPT_DIR/src/UITestApp.java"

    echo "Compiling $(echo "$JAVA_FILES" | wc -w) Java files..."
    javac -source 8 -target 8 \
        -d "$BUILD_DIR/classes" \
        -sourcepath "$SHIM_JAVA:$SCRIPT_DIR/src" \
        $JAVA_FILES 2>&1 || {
        echo "ERROR: javac failed"
        exit 1
    }

    echo "Compiled successfully."

    if command -v d8 &>/dev/null; then
        echo "Converting to DEX..."
        d8 --output "$BUILD_DIR" $(find "$BUILD_DIR/classes" -name "*.class")
        echo "DEX created."
    elif command -v dx &>/dev/null; then
        dx --dex --output="$BUILD_DIR/classes.dex" "$BUILD_DIR/classes"
    else
        echo "WARNING: d8/dx not found — run with 'java' instead"
    fi
}

deploy() {
    echo "=== Deploying to device ==="
    hdc shell mkdir -p "$DEVICE_DIR"

    if [ -f "$BUILD_DIR/classes.dex" ]; then
        hdc file send "$BUILD_DIR/classes.dex" "$DEVICE_DIR/classes.dex"
    fi

    RUST_LIB="$PROJECT_ROOT/shim/bridge/rust/target/aarch64-unknown-linux-ohos/release/liboh_bridge.so"
    CPP_LIB="$PROJECT_ROOT/shim/bridge/cpp/build/liboh_cpp_shim.so"

    [ -f "$RUST_LIB" ] && hdc file send "$RUST_LIB" "$DEVICE_DIR/liboh_bridge.so"
    [ -f "$CPP_LIB" ] && hdc file send "$CPP_LIB" "$DEVICE_DIR/liboh_cpp_shim.so"
    echo "Deployed to $DEVICE_DIR"
}

run() {
    echo "=== Running UI test on device ==="
    hdc shell "cd $DEVICE_DIR && \
        export LD_LIBRARY_PATH=$DEVICE_DIR:\$LD_LIBRARY_PATH && \
        app_process -Djava.library.path=$DEVICE_DIR \
            -cp $DEVICE_DIR/classes.dex \
            / UITestApp"
}

run_headless() {
    echo "=== Running HEADLESS (local JVM, no device) ==="
    echo ""
    if [ -d "$BUILD_DIR/classes" ]; then
        java -cp "$BUILD_DIR/classes" UITestApp --headless 2>&1 || true
    else
        echo "Build first: $0 build"
        exit 1
    fi
}

case "${1:-all}" in
    build)    build ;;
    deploy)   deploy ;;
    run)      run ;;
    headless) build && run_headless ;;
    all)      build && deploy && run ;;
    *)        echo "Usage: $0 {build|deploy|run|headless|all}"; exit 1 ;;
esac
