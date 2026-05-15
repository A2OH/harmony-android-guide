#!/bin/bash
# ============================================================================
# Westlake M3++ — AsInterfaceTest.dex build helper
#
# Compiles aosp-libbinder-port/test/AsInterfaceTest.java into a .dex.  Same
# strategy as build_hello.sh — see that file for the longer comment.
#
# Output: out/AsInterfaceTest.dex
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/asinterface-build-$$
OUT_DEX="$SCRIPT_DIR/out/AsInterfaceTest.dex"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR"
    exit 1
fi
if [ ! -x "$DX" ]; then
    echo "ERROR: dx not found at $DX"
    exit 1
fi

mkdir -p "$BUILD/classes" "$BUILD/stubs/android/os"

# Stub for compile-time only — runtime uses aosp-shim.dex's real ServiceManager
# + Binder.  Public SDK android.jar doesn't expose addService / etc. so we
# synthesize a tiny stub class set for javac.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
    public static IBinder checkService(String name) { throw new RuntimeException("stub"); }
    public static void addService(String name, IBinder s) { throw new RuntimeException("stub"); }
}
EOF

# Note: android.jar already has android.os.Binder + android.os.IInterface +
# android.os.IBinder so we don't need to stub those.

javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$SCRIPT_DIR/test/AsInterfaceTest.java" 2>&1 | grep -v "^Note:" || true

# Package only AsInterfaceTest + inner classes into the dex (NOT the stubs).
mkdir -p "$BUILD/dex-only"
cp "$BUILD/classes/AsInterfaceTest"*.class "$BUILD/dex-only/"

"$DX" --dex --output="$OUT_DEX" "$BUILD/dex-only" >/dev/null 2>&1

if [ ! -s "$OUT_DEX" ]; then
    echo "ERROR: $OUT_DEX not produced (or empty)"
    rm -rf "$BUILD"
    exit 1
fi

ls -lh "$OUT_DEX"
rm -rf "$BUILD"
echo "Done."
