#!/bin/bash
# ============================================================================
# Westlake M3 — HelloBinder.dex build helper
#
# Compiles aosp-libbinder-port/test/HelloBinder.java into a .dex that can be
# loaded by dalvikvm.  HelloBinder.java references hidden API
# android.os.ServiceManager which the public Android SDK doesn't expose,
# so we synthesize a tiny stub for compilation only — the actual class at
# runtime comes from aosp-shim.dex (via our M3 shim wiring; see
# M3_NOTES.md).
#
# Output: out/HelloBinder.dex
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/hellobinder-build-$$
OUT_DEX="$SCRIPT_DIR/out/HelloBinder.dex"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR"
    exit 1
fi
if [ ! -x "$DX" ]; then
    echo "ERROR: dx not found at $DX"
    exit 1
fi

mkdir -p "$BUILD/classes" "$BUILD/stubs/android/os"

# Stub for compile-time only — runtime uses aosp-shim.dex's ServiceManager.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
// Compile-time stub for HelloBinder.dex.  At runtime aosp-shim.dex's
// real shim/java/android/os/ServiceManager (which calls into our libbinder)
// is loaded by the classloader before HelloBinder resolves any of these
// references.  This stub MUST NOT be packaged into HelloBinder.dex.
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
    public static IBinder checkService(String name) { throw new RuntimeException("stub"); }
}
EOF

javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$SCRIPT_DIR/test/HelloBinder.java" 2>&1 | grep -v "^Note:" || true

# Package only HelloBinder.class into the dex (NOT the stub).
mkdir -p "$BUILD/dex-only"
cp "$BUILD/classes/HelloBinder.class" "$BUILD/dex-only/"

"$DX" --dex --output="$OUT_DEX" "$BUILD/dex-only" >/dev/null 2>&1

if [ ! -s "$OUT_DEX" ]; then
    echo "ERROR: $OUT_DEX not produced (or empty)"
    rm -rf "$BUILD"
    exit 1
fi

ls -lh "$OUT_DEX"
rm -rf "$BUILD"
echo "Done."
