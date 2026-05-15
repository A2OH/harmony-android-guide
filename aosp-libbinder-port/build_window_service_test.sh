#!/bin/bash
# ============================================================================
# Westlake M4b -- WindowServiceTest.dex build helper
#
# Compiles aosp-libbinder-port/test/WindowServiceTest.java plus the
# AsInterfaceTest.java helper (used for its already-registered
# println/eprintln natives) into one .dex.  Same strategy as
# build_power_service_test.sh -- see that file for the longer comment.
#
# Output: out/WindowServiceTest.dex
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
SHIM_JAVA=/home/dspfac/android-to-openharmony-migration/shim/java
BUILD=/tmp/windowservicetest-build-$$
OUT_DEX="$SCRIPT_DIR/out/WindowServiceTest.dex"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR"
    exit 1
fi
if [ ! -x "$DX" ]; then
    echo "ERROR: dx not found at $DX"
    exit 1
fi

mkdir -p "$BUILD/classes" "$BUILD/stubs/android/os"

# Compile-time stub for ServiceManager (public SDK doesn't expose
# addService).  Runtime uses the real shim ServiceManager from
# aosp-shim.dex on the bootclasspath.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
    public static IBinder checkService(String name) { throw new RuntimeException("stub"); }
    public static void addService(String name, IBinder s) { throw new RuntimeException("stub"); }
}
EOF

# The test reflectively constructs com.westlake.services.WestlakeWindowManagerService
# and com.westlake.services.ServiceRegistrar; both come from aosp-shim.dex at
# runtime, so no compile-time stub is needed for them.
#
# It also references the shim @hide IWindowSessionCallback / IWindowManager
# types for compile-time -- these come from the shim sourcepath.

javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -sourcepath "$SHIM_JAVA" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$SCRIPT_DIR/test/AsInterfaceTest.java" \
    "$SCRIPT_DIR/test/WindowServiceTest.java" 2>&1 | grep -v "^Note:" || true

if [ ! -f "$BUILD/classes/WindowServiceTest.class" ]; then
    echo "ERROR: javac failed (WindowServiceTest.class missing)"
    rm -rf "$BUILD"
    exit 1
fi
if [ ! -f "$BUILD/classes/AsInterfaceTest.class" ]; then
    echo "ERROR: javac failed (AsInterfaceTest.class missing)"
    rm -rf "$BUILD"
    exit 1
fi

# Package WindowServiceTest + AsInterfaceTest (and their inner classes) into
# the dex; the stubs above stay out.
mkdir -p "$BUILD/dex-only"
cp "$BUILD/classes/WindowServiceTest"*.class "$BUILD/dex-only/"
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
