#!/bin/bash
# Build aosp-shim.dex from the shim/java sources.
#
# This compiles all Java sources under shim/java/ (the AOSP shim layer)
# plus the mock OHBridge, then converts to DEX format using d8.
#
# Output: aosp-shim.dex in the repo root (and copies to deploy dirs)

set -e

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SHIM_JAVA="$REPO_ROOT/shim/java"
MOCK_JAVA="$REPO_ROOT/test-apps/mock"
BUILD_DIR="/tmp/shim-build-$$"
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR"
    exit 1
fi

echo "=== Building aosp-shim.dex ==="
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

# Collect all shim Java files (excluding real OHBridge since we use the mock for compilation)
echo "Collecting sources..."
JAVA_FILES=$(find "$SHIM_JAVA" -name "*.java" ! -path "*/ohos/shim/bridge/OHBridge.java")
# Add mock OHBridge for compilation
if [ -d "$MOCK_JAVA" ]; then
    JAVA_FILES="$JAVA_FILES $(find "$MOCK_JAVA" -name "*.java")"
fi

FILE_COUNT=$(echo "$JAVA_FILES" | wc -w)
echo "Compiling $FILE_COUNT Java files..."

javac -source 1.8 -target 1.8 \
    -classpath "$ANDROID_JAR" \
    -sourcepath "$SHIM_JAVA:$MOCK_JAVA" \
    -d "$BUILD_DIR/classes" \
    $JAVA_FILES 2>&1

echo "Javac complete."

# Convert to DEX
echo "Running dx..."
CLASS_COUNT=$(find "$BUILD_DIR/classes" -name "*.class" | wc -l)
echo "  $CLASS_COUNT class files"

$DX --dex --output="$BUILD_DIR/aosp-shim.dex" "$BUILD_DIR/classes" 2>&1

# Copy to output locations
echo "Copying to output locations..."
cp "$BUILD_DIR/aosp-shim.dex" "$REPO_ROOT/aosp-shim.dex"
echo "  -> $REPO_ROOT/aosp-shim.dex"

if [ -d "$REPO_ROOT/ohos-deploy" ]; then
    cp "$BUILD_DIR/aosp-shim.dex" "$REPO_ROOT/ohos-deploy/aosp-shim.dex"
    echo "  -> $REPO_ROOT/ohos-deploy/aosp-shim.dex"
fi

ASSETS_DIR="$REPO_ROOT/westlake-host-gradle/app/src/main/assets"
if [ -d "$ASSETS_DIR" ]; then
    cp "$BUILD_DIR/aosp-shim.dex" "$ASSETS_DIR/aosp-shim.dex"
    echo "  -> $ASSETS_DIR/aosp-shim.dex"
fi

# Cleanup
rm -rf "$BUILD_DIR"

SIZE=$(stat -c%s "$REPO_ROOT/aosp-shim.dex")
echo ""
echo "=== Done: aosp-shim.dex ($SIZE bytes) ==="
