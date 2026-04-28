#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
GEN_DIR="$BUILD_DIR/gen"
STUB_DIR="$BUILD_DIR/stubs"
CLASSES_DIR="$BUILD_DIR/classes"
OBJ_DIR="$BUILD_DIR/obj"
DIST_DIR="$BUILD_DIR/dist"

TOOLS_DIR="/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin"
AAPT="$TOOLS_DIR/aapt"
ZIPALIGN="$TOOLS_DIR/zipalign"
DX_JAR="/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/lib/dx.jar"
APKSIGNER_JAR="/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/lib/apksigner.jar"
ANDROID_JAR="/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar"
KEY_ALIAS="${KEY_ALIAS:-androiddebugkey}"
KEY_PASS="${KEY_PASS:-android}"
APK_NAME="westlake-material-xml-probe-debug.apk"
UNSIGNED_APK="$DIST_DIR/westlake-material-xml-probe-unsigned.apk"
ALIGNED_APK="$DIST_DIR/westlake-material-xml-probe-aligned.apk"
SIGNED_APK="$DIST_DIR/$APK_NAME"
KEYSTORE_DEFAULT="$BUILD_DIR/debug.keystore"
KEYSTORE="${KEYSTORE:-$KEYSTORE_DEFAULT}"

rm -rf "$GEN_DIR" "$STUB_DIR" "$CLASSES_DIR" "$OBJ_DIR" "$DIST_DIR"
mkdir -p "$GEN_DIR" "$STUB_DIR" "$CLASSES_DIR" "$OBJ_DIR" "$DIST_DIR"

if [ ! -x "$AAPT" ] || [ ! -x "$ZIPALIGN" ]; then
    echo "ERROR: Missing Android build tools under $TOOLS_DIR" >&2
    exit 1
fi
if [ ! -f "$ANDROID_JAR" ] || [ ! -f "$DX_JAR" ] || [ ! -f "$APKSIGNER_JAR" ]; then
    echo "ERROR: Missing Android SDK dependency under /home/dspfac/aosp-android-11" >&2
    exit 1
fi

if ! keytool -list -keystore "$KEYSTORE" -storepass "$KEY_PASS" -alias "$KEY_ALIAS" >/dev/null 2>&1; then
    rm -f "$KEYSTORE"
    keytool -genkeypair \
        -keystore "$KEYSTORE" \
        -storepass "$KEY_PASS" \
        -keypass "$KEY_PASS" \
        -alias "$KEY_ALIAS" \
        -dname "CN=Android Debug,O=Android,C=US" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 >/dev/null 2>&1
fi

echo "=== Building Westlake Material XML probe APK ==="

"$AAPT" package -f -m \
    -S "$SCRIPT_DIR/res" \
    -J "$GEN_DIR" \
    -M "$SCRIPT_DIR/AndroidManifest.xml" \
    -I "$ANDROID_JAR" \
    -F "$OBJ_DIR/resources.ap_" \
    --auto-add-overlay

find "$SCRIPT_DIR/src" "$GEN_DIR" -name "*.java" | sort > "$BUILD_DIR/sources.txt"

mkdir -p "$STUB_DIR/com/westlake/engine"
mkdir -p "$STUB_DIR/com/google/android"
cp -R "$REPO_ROOT/shim/java/com/google/android/material" "$STUB_DIR/com/google/android/"
cat > "$STUB_DIR/com/westlake/engine/WestlakeLauncher.java" <<'JAVA'
package com.westlake.engine;

public final class WestlakeLauncher {
    private WestlakeLauncher() {}
    public static boolean appendCutoffCanaryMarker(String message) {
        return false;
    }
}
JAVA

javac --release 8 \
    -cp "$ANDROID_JAR" \
    -sourcepath "$SCRIPT_DIR/src:$GEN_DIR:$STUB_DIR" \
    -d "$CLASSES_DIR" \
    @"$BUILD_DIR/sources.txt" \
    "$STUB_DIR/com/westlake/engine/WestlakeLauncher.java"

rm -rf "$CLASSES_DIR/com/westlake/engine" "$CLASSES_DIR/com/google/android/material"

java -jar "$DX_JAR" --dex --output="$BUILD_DIR/classes.dex" "$CLASSES_DIR"

export SCRIPT_DIR UNSIGNED_APK
python3 - <<'PY'
import os
import zipfile

script_dir = os.environ["SCRIPT_DIR"]
unsigned_apk = os.environ["UNSIGNED_APK"]
classes_dex = os.path.join(script_dir, "build", "classes.dex")
resources_ap = os.path.join(script_dir, "build", "obj", "resources.ap_")

with zipfile.ZipFile(unsigned_apk, "w", zipfile.ZIP_DEFLATED) as apk:
    apk.write(classes_dex, "classes.dex")
    with zipfile.ZipFile(resources_ap, "r") as res:
        for name in res.namelist():
            apk.writestr(name, res.read(name))
PY

"$ZIPALIGN" -f 4 "$UNSIGNED_APK" "$ALIGNED_APK"
java -jar "$APKSIGNER_JAR" sign \
    --ks "$KEYSTORE" \
    --ks-key-alias "$KEY_ALIAS" \
    --ks-pass "pass:$KEY_PASS" \
    --key-pass "pass:$KEY_PASS" \
    --out "$SIGNED_APK" \
    "$ALIGNED_APK"

if ! java -jar "$APKSIGNER_JAR" verify "$SIGNED_APK" >/dev/null 2>&1; then
    echo "WARNING: apksigner verify failed on this host toolchain; signed APK kept anyway." >&2
fi

apk_sha256="$(sha256sum "$SIGNED_APK")"
printf "%s\n" "$apk_sha256" > "$SIGNED_APK.sha256"
echo "Built APK:"
echo "  $SIGNED_APK"
echo "SHA256:"
echo "  $apk_sha256"
