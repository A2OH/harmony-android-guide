#!/bin/bash
# APK Analysis Pipeline: build APKs, extract API usage, test resource parsing
#
# Usage: ./run-analysis.sh
#
# Prerequisites: build the shim layer first via:
#   cd test-apps && ./run-local-tests.sh headless

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
AAPT=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin/aapt
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/19/public/android.jar
DX_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/lib/dx.jar
DEXDUMP=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin/dexdump

SHIM_JAVA="$PROJECT_ROOT/shim/java"
MOCK_JAVA="$PROJECT_ROOT/test-apps/mock"
BUILD_DIR="/tmp/apk-analysis-build"

echo "================================================================="
echo "=== APK Analysis Pipeline                                     ==="
echo "================================================================="
echo ""

# Step 1: Build shim + test harness
echo "--- Step 1: Compile shim layer + ResourceParseTest ---"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

JAVA_FILES=$(find "$MOCK_JAVA" -name "*.java")
JAVA_FILES="$JAVA_FILES $(find "$SHIM_JAVA" -name "*.java" ! -path "*/ohos/shim/bridge/OHBridge.java")"
JAVA_FILES="$JAVA_FILES $SCRIPT_DIR/ResourceParseTest.java"

javac -d "$BUILD_DIR" \
    -sourcepath "$MOCK_JAVA:$SHIM_JAVA:$SCRIPT_DIR" \
    $JAVA_FILES 2>&1 | grep -v "warning:" | grep -v "^$" || true

echo "Shim compiled OK"
echo ""

# Step 2: Build APKs (if not already built)
for n in 1 2 3; do
    srcdir="$SCRIPT_DIR/apk${n}-src"
    apkfile="$SCRIPT_DIR/apk${n}.apk"

    if [ -d "$srcdir/src" ] && [ -d "$srcdir/res" ]; then
        echo "--- Step 2.$n: Building APK $n from $srcdir ---"
        workdir="/tmp/apk-build-$n"
        rm -rf "$workdir"
        mkdir -p "$workdir"
        cp -r "$srcdir"/* "$workdir/"

        cd "$workdir"
        mkdir -p gen obj classes

        $AAPT package -f -m -S res -J gen -M AndroidManifest.xml -I "$ANDROID_JAR" -F obj/resources.ap_ --auto-add-overlay
        find src gen -name "*.java" > sources.txt
        javac --release 8 -cp "$ANDROID_JAR" -d classes @sources.txt 2>&1 | grep -v "warning:" || true
        java -jar "$DX_JAR" --dex --output=classes.dex classes/ 2>&1

        python3 -c "
import zipfile, os
with zipfile.ZipFile('app.apk', 'w', zipfile.ZIP_DEFLATED) as apk:
    apk.write('classes.dex', 'classes.dex')
    if os.path.exists('obj/resources.ap_'):
        with zipfile.ZipFile('obj/resources.ap_', 'r') as res:
            for item in res.namelist():
                apk.writestr(item, res.read(item))
"
        cp app.apk "$apkfile"
        echo "  Built: $apkfile ($(wc -c < "$apkfile") bytes)"
    fi
done
echo ""

# Step 3: API analysis via dexdump
echo "--- Step 3: API class references per APK ---"
for n in 1 2 3; do
    dexfile="/tmp/apk-build-$n/classes.dex"
    if [ -f "$dexfile" ]; then
        echo ""
        echo "APK $n:"
        $DEXDUMP -f "$dexfile" 2>/dev/null | grep -oP 'L[a-z].*?;' | sort -u | grep -E '^Landroid/' | sed 's/^L//;s/;$//;s|/|.|g' | while read cls; do
            echo "    $cls"
        done
    fi
done
echo ""

# Step 4: Resource parsing test
echo "--- Step 4: Resource parsing test ---"
java -cp "$BUILD_DIR" ResourceParseTest
