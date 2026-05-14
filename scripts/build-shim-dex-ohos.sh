#!/bin/bash
# Build aosp-shim-ohos.dex from shim/java sources WITHOUT applying the
# scripts/framework_duplicates.txt strip list.
#
# Rationale: on OHOS dalvik-kitkat we have NO real framework.jar — the
# phone's framework.jar is dex.039 (unloadable by KitKat). So the shim
# must SHIP all the Android API classes (ContextThemeWrapper, Bundle,
# Process, ...) itself rather than rely on framework.jar.
#
# Sibling of scripts/build-shim-dex.sh (which DOES strip duplicates for
# the Android phone path). They share the same shim/java source tree.
#
# Output: ohos-deploy/aosp-shim-ohos.dex (the on-device BCP file).
#
# Provenance: this script formalizes the manual `dx --dex` invocation
# described in commit 2d00f89f (MVP-1 / #619). PF-ohos-m6-002 added it
# so adding new shim classes (e.g. com.westlake.compat.UnixSocketBridge
# for the M6 daemon client) becomes a `bash this-script.sh` step rather
# than a rediscovery exercise.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SHIM_JAVA="$REPO_ROOT/shim/java"
MOCK_JAVA="$REPO_ROOT/test-apps/mock"
BUILD_DIR="/tmp/shim-ohos-build-$$"
ANDROID_JAR="${ANDROID_JAR:-/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar}"
DX="${DX:-/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx}"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR" >&2
    exit 1
fi
if [ ! -x "$DX" ]; then
    echo "ERROR: dx not found at $DX (needed for dex.035 output;" \
        "d8 chokes on enum desugaring at min-api 13 for shim sources)" >&2
    exit 1
fi

echo "=== Building aosp-shim-ohos.dex (NON-stripped variant) ==="
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

JAVA_FILES=$(find "$SHIM_JAVA" -name "*.java")
FILE_COUNT=$(echo "$JAVA_FILES" | wc -w)
echo "Compiling $FILE_COUNT Java files..."

javac -source 1.8 -target 1.8 \
    -classpath "$ANDROID_JAR" \
    -sourcepath "$SHIM_JAVA:$MOCK_JAVA" \
    -d "$BUILD_DIR/classes" \
    $JAVA_FILES 2>&1

CLASS_COUNT=$(find "$BUILD_DIR/classes" -name "*.class" | wc -l)
echo "Javac complete: $CLASS_COUNT .class files."

# NO duplicate stripping — that's the difference from build-shim-dex.sh.
# We ship every shim class so dalvik-kitkat on OHOS sees a self-contained
# BCP slice without needing framework.jar.

echo "Running dx --dex (dex.035 for dalvik-kitkat)..."
"$DX" --dex --output="$BUILD_DIR/aosp-shim-ohos.dex" "$BUILD_DIR/classes" 2>&1

OUTPUT="$REPO_ROOT/ohos-deploy/aosp-shim-ohos.dex"
mkdir -p "$(dirname "$OUTPUT")"
cp "$BUILD_DIR/aosp-shim-ohos.dex" "$OUTPUT"

rm -rf "$BUILD_DIR"
SIZE=$(stat -c%s "$OUTPUT")
echo ""
echo "=== Done: $OUTPUT ($SIZE bytes) ==="
