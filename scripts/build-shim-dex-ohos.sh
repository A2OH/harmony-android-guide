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

# Selective duplicate stripping for AndroidX classes whose AOSP-named member
# signatures COLLIDE with R8-minified versions inevitably shipped by modern
# AndroidX apps. Two failure modes, both encountered live (CR63, CR64):
#
# 1. Enum-constant rename — `Lifecycle$State` / `Lifecycle$Event`: every
#    R8-shrunk app ships its own private copy of these enums with the field
#    NAMES renamed to alphabet letters (e.g. m/n/o/p/q for noice's State,
#    instead of DESTROYED/INITIALIZED/CREATED/STARTED/RESUMED). When our
#    shim's same-descriptor class wins bootclasspath resolution, the app's
#    `sget-object Lifecycle$State;->n` fails with NoSuchFieldError because
#    the shim version only has the AOSP names. CR63 hit this on noice's
#    LifecycleRegistry (`Landroidx/lifecycle/y;`) constructor.
#
# 2. Method-signature rename — `ComponentActivity` etc.: the app's bytecode
#    expects R8-renamed return types (e.g. `getSavedStateRegistry()Lu2/c;`),
#    while our shim ships the AOSP-named return type
#    (`getSavedStateRegistry()Landroidx/savedstate/SavedStateRegistry;`).
#    Dalvik method lookup uses name+descriptor so this returns
#    NoSuchMethodError. CR64 hit this on noice's
#    `Landroidx/fragment/app/d0;.<init>` calling
#    `ComponentActivity.getSavedStateRegistry()Lu2/c;`.
#
# Fix discovered CR63/CR64 (2026-05-15, agent 22): strip the colliding
# classes so the app's own R8-minified copy resolves at runtime. The enum
# `name()` strings are still "INITIALIZED" etc (set via Enum.<init> in the
# app's <clinit>), so any host-side reflective lookup via Enum.valueOf
# still works. Stripped outer `Lifecycle.class` defensively: no shim or
# AndroidX-shipping app references it directly (R8 maps the abstract base
# to `Landroidx/lifecycle/p;`).
#
# Not per-app: this is universal R8 behaviour across the AndroidX ecosystem
# since AGP 7.0+; every non-trivial APK trips the same collisions. Test
# apps that DON'T ship AndroidX (MVP-0 hello, MVP-1 trivial-activity,
# E12 hello-color-apk) extend `android.app.Activity` directly and never
# reference these classes, so the strips are regression-safe.
STRIP_CLASSES=(
    "androidx/lifecycle/Lifecycle.class"
    "androidx/lifecycle/Lifecycle\$State.class"
    "androidx/lifecycle/Lifecycle\$Event.class"
    "androidx/activity/ComponentActivity.class"
)
for c in "${STRIP_CLASSES[@]}"; do
    f="$BUILD_DIR/classes/$c"
    if [ -f "$f" ]; then
        rm -f "$f"
        echo "  stripped duplicate: $c"
    fi
done

POSTSTRIP_COUNT=$(find "$BUILD_DIR/classes" -name "*.class" | wc -l)
echo "Post-strip: $POSTSTRIP_COUNT .class files (-$((CLASS_COUNT - POSTSTRIP_COUNT)))."

echo "Running dx --dex (dex.035 for dalvik-kitkat)..."
"$DX" --dex --output="$BUILD_DIR/aosp-shim-ohos.dex" "$BUILD_DIR/classes" 2>&1

OUTPUT="$REPO_ROOT/ohos-deploy/aosp-shim-ohos.dex"
mkdir -p "$(dirname "$OUTPUT")"
cp "$BUILD_DIR/aosp-shim-ohos.dex" "$OUTPUT"

rm -rf "$BUILD_DIR"
SIZE=$(stat -c%s "$OUTPUT")
echo ""
echo "=== Done: $OUTPUT ($SIZE bytes) ==="
