#!/bin/bash
# ============================================================================
# Westlake M4a -- ActivityServiceTest.dex build helper
#
# Compiles aosp-libbinder-port/test/ActivityServiceTest.java into a .dex.
# Same strategy as build_asinterface.sh -- see that file for the longer
# comment.
#
# Output: out/ActivityServiceTest.dex
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
SHIM_JAVA=/home/dspfac/android-to-openharmony-migration/shim/java
BUILD=/tmp/activity-svc-test-build-$$
OUT_DEX="$SCRIPT_DIR/out/ActivityServiceTest.dex"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR"
    exit 1
fi
if [ ! -x "$DX" ]; then
    echo "ERROR: dx not found at $DX"
    exit 1
fi

mkdir -p "$BUILD/classes" "$BUILD/stubs/android/os"

# Compile-time stubs for ServiceManager + the @hide IActivityManager surface.
# At runtime aosp-shim.dex provides ServiceManager + IActivityManager, and
# framework.jar provides the real abstract Stub; this stub only needs to make
# javac happy.

# ServiceManager: same shape as build_asinterface.sh's stub.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
    public static IBinder checkService(String name) { throw new RuntimeException("stub"); }
    public static void addService(String name, IBinder s) { throw new RuntimeException("stub"); }
}
EOF

# Compile the test class against shim sources for the @hide IActivityManager /
# IProcessObserver / WestlakeActivityManagerService chain.
javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -sourcepath "$SHIM_JAVA" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$SCRIPT_DIR/test/ActivityServiceTest.java" 2>&1 | grep -v "^Note:" || true

# Verify compile produced the class.
if [ ! -f "$BUILD/classes/ActivityServiceTest.class" ]; then
    echo "ERROR: ActivityServiceTest.class not produced (compile errors above)"
    rm -rf "$BUILD"
    exit 1
fi

# Package only ActivityServiceTest + inner classes into the dex.  NOT the
# stub ServiceManager nor any shim classes -- those live in aosp-shim.dex.
mkdir -p "$BUILD/dex-only"
cp "$BUILD/classes/ActivityServiceTest"*.class "$BUILD/dex-only/"

"$DX" --dex --output="$OUT_DEX" "$BUILD/dex-only" >/dev/null 2>&1

if [ ! -s "$OUT_DEX" ]; then
    echo "ERROR: $OUT_DEX not produced (or empty)"
    rm -rf "$BUILD"
    exit 1
fi

ls -lh "$OUT_DEX"
rm -rf "$BUILD"
echo "Done."
