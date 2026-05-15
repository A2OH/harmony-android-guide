#!/bin/bash
# ============================================================================
# Westlake M4d + M4e -- batch build helper for the three new service tests:
#   - DisplayServiceTest.dex (IDisplayManager smoke)
#   - NotificationServiceTest.dex (INotificationManager smoke)
#   - InputMethodServiceTest.dex (IInputMethodManager smoke)
#
# Mirrors build_power_service_test.sh's structure: each test class is
# bundled with AsInterfaceTest (for its already-registered println /
# eprintln natives loaded by android_runtime_stub's JNI_OnLoad_binder).
#
# Output: out/DisplayServiceTest.dex
#         out/NotificationServiceTest.dex
#         out/InputMethodServiceTest.dex
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/m4de-tests-build-$$

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found at $ANDROID_JAR"
    exit 1
fi
if [ ! -x "$DX" ]; then
    echo "ERROR: dx not found at $DX"
    exit 1
fi

mkdir -p "$BUILD/classes" "$BUILD/stubs/android/os" "$SCRIPT_DIR/out"

# Compile-time stub for ServiceManager (public SDK doesn't expose addService).
# Runtime uses the real shim ServiceManager from aosp-shim.dex.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
    public static IBinder checkService(String name) { throw new RuntimeException("stub"); }
    public static void addService(String name, IBinder s) { throw new RuntimeException("stub"); }
}
EOF

# Each test reflectively constructs com.westlake.services.WestlakeXxxService;
# all of those come from aosp-shim.dex at runtime so no compile-time stub is
# needed for the services themselves.

echo "=== Compiling M4d + M4e test classes ==="
javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$SCRIPT_DIR/test/AsInterfaceTest.java" \
    "$SCRIPT_DIR/test/CharsetPrimer.java" \
    "$SCRIPT_DIR/test/DisplayServiceTest.java" \
    "$SCRIPT_DIR/test/NotificationServiceTest.java" \
    "$SCRIPT_DIR/test/InputMethodServiceTest.java" 2>&1 | grep -v "^Note:" || true

for cls in DisplayServiceTest NotificationServiceTest InputMethodServiceTest AsInterfaceTest CharsetPrimer; do
    if [ ! -f "$BUILD/classes/$cls.class" ]; then
        echo "ERROR: javac failed -- $cls.class missing"
        rm -rf "$BUILD"
        exit 1
    fi
done

# Package each test (plus AsInterfaceTest + CharsetPrimer + inner classes)
# into its own dex.  CR9: CharsetPrimer must be bundled because each
# test's main() calls CharsetPrimer.primeCharsetState() as line 1.
build_dex() {
    local name="$1"
    local out_dex="$SCRIPT_DIR/out/$name.dex"
    local dex_dir="$BUILD/dex-$name"
    mkdir -p "$dex_dir"
    cp "$BUILD/classes/$name"*.class "$dex_dir/"
    cp "$BUILD/classes/AsInterfaceTest"*.class "$dex_dir/"
    cp "$BUILD/classes/CharsetPrimer"*.class "$dex_dir/"
    "$DX" --dex --output="$out_dex" "$dex_dir" >/dev/null 2>&1
    if [ ! -s "$out_dex" ]; then
        echo "ERROR: $out_dex not produced (or empty)"
        rm -rf "$BUILD"
        exit 1
    fi
    ls -lh "$out_dex"
}

echo "=== Building DEX files ==="
build_dex DisplayServiceTest
build_dex NotificationServiceTest
build_dex InputMethodServiceTest

rm -rf "$BUILD"
echo "Done."
