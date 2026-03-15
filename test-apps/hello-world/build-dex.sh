#!/bin/bash
#
# Build hello-world.dex from shim classes + HelloWorld Activity.
#
# Produces: /tmp/hello-world.dex (Dalvik DEX format)
#
# Requires: javac (JDK 8+), dx.jar (from AOSP)

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

DX_JAR="/home/dspfac/aosp-android-11/prebuilts/build-tools/common/framework/dx.jar"
SHIM_JAVA="$PROJECT_ROOT/shim/java"
MOCK_JAVA="$PROJECT_ROOT/test-apps/mock"
BUILD_DIR="/tmp/hello-world-j8"
OUTPUT="/tmp/hello-world.dex"

echo "═══ Building Hello World DEX ═══"

# Step 1: Compile to .class with Java 8 target (required by dx)
echo "Compiling Java sources (--release 8)..."
rm -rf "$BUILD_DIR" && mkdir -p "$BUILD_DIR"

javac -d "$BUILD_DIR" --release 8 \
    -sourcepath "$MOCK_JAVA:$SHIM_JAVA:$SCRIPT_DIR/src" \
    "$SCRIPT_DIR/src/HelloWorldRunner.java" \
    "$SCRIPT_DIR/src/HelloWorldActivity.java" \
    $(find "$MOCK_JAVA" -name "*.java") \
    $(find "$SHIM_JAVA" -name "*.java" ! -path "*/ohos/shim/bridge/OHBridge.java") \
    2>&1 | grep -v "^Note:" || true

echo "Compiled $(find "$BUILD_DIR" -name "*.class" | wc -l) class files."

# Step 2: Convert to DEX
echo "Converting to DEX..."
java -jar "$DX_JAR" \
    --dex --min-sdk-version=26 --output="$OUTPUT" \
    "$BUILD_DIR"

echo ""
echo "Output: $OUTPUT ($(ls -lh "$OUTPUT" | awk '{print $5}'))"
echo ""

# Step 3: Quick verification
echo "Verifying DEX..."
file "$OUTPUT"
echo "Done!"
