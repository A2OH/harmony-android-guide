#!/bin/bash
# ============================================================================
# W2-discover — McdDiscoverWrapper.dex build helper
#
# Westlake CR27 (2026-05-13): second consumer of the manifest-driven
# DiscoverWrapperBase harness.  Compiles McdDiscoverWrapper.java +
# DiscoverWrapperBase.java + CharsetPrimer.java into a .dex consumable
# by dalvikvm.
#
# Output: out/McdDiscoverWrapper.dex
#
# Runtime input: /data/local/tmp/westlake/mcd.discover.properties.
# All app-specific configuration lives there; this .dex carries only
# generic phase orchestration.
#
# See M4_DISCOVERY.md §50 (CR27) for design + delta count.
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/mcdwrap-build-$$
OUT_DEX="$SCRIPT_DIR/out/McdDiscoverWrapper.dex"

mkdir -p "$BUILD/classes" "$BUILD/stubs/android/os" "$BUILD/stubs/dalvik/system" "$BUILD/stubs/com/westlake/services"

# Compile-time stubs (must NOT be packaged into the dex; the runtime classes
# come from aosp-shim.dex / framework.jar respectively).  Identical to
# build_discover.sh's stub set -- the base class uses the same APIs.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
    public static IBinder checkService(String name) { throw new RuntimeException("stub"); }
    public static void addService(String name, IBinder s) { throw new RuntimeException("stub"); }
}
EOF

cat > "$BUILD/stubs/dalvik/system/PathClassLoader.java" <<'EOF'
package dalvik.system;
public class PathClassLoader extends ClassLoader {
    public PathClassLoader(String s, ClassLoader p) { super(p); }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException("stub");
    }
}
EOF

# M4a: ServiceRegistrar lives in aosp-shim.dex; provide compile-time stub.
cat > "$BUILD/stubs/com/westlake/services/ServiceRegistrar.java" <<'EOF'
package com.westlake.services;
public final class ServiceRegistrar {
    public static int registerAllServices() { return 0; }
}
EOF

javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$BUILD/stubs/dalvik/system/PathClassLoader.java" \
    "$BUILD/stubs/com/westlake/services/ServiceRegistrar.java" \
    "$SCRIPT_DIR/test/CharsetPrimer.java" \
    "$SCRIPT_DIR/test/AsInterfaceTest.java" \
    "$SCRIPT_DIR/test/DiscoverWrapperBase.java" \
    "$SCRIPT_DIR/test/McdDiscoverWrapper.java" 2>&1 | grep -v "^Note:" || true

if [ ! -f "$BUILD/classes/McdDiscoverWrapper.class" ]; then
    echo "ERROR: javac failed (McdDiscoverWrapper.class missing)"
    rm -rf "$BUILD"
    exit 1
fi
if [ ! -f "$BUILD/classes/DiscoverWrapperBase.class" ]; then
    echo "ERROR: javac failed (DiscoverWrapperBase.class missing)"
    rm -rf "$BUILD"
    exit 1
fi
if [ ! -f "$BUILD/classes/AsInterfaceTest.class" ]; then
    echo "ERROR: javac failed (AsInterfaceTest.class missing)"
    rm -rf "$BUILD"
    exit 1
fi
if [ ! -f "$BUILD/classes/CharsetPrimer.class" ]; then
    echo "ERROR: javac failed (CharsetPrimer.class missing)"
    rm -rf "$BUILD"
    exit 1
fi

# Package McdDiscoverWrapper + DiscoverWrapperBase + CharsetPrimer +
# AsInterfaceTest (println/eprintln native delegate) into the dex
# (NOT the stubs).
mkdir -p "$BUILD/dex-only"
# Wildcard pattern picks up inner / anonymous classes
# (McdDiscoverWrapper$1.class -- the anonymous Printer in main()).
cp "$BUILD/classes/McdDiscoverWrapper"*.class "$BUILD/dex-only/"
cp "$BUILD/classes/DiscoverWrapperBase"*.class "$BUILD/dex-only/"
cp "$BUILD/classes/AsInterfaceTest"*.class "$BUILD/dex-only/" 2>/dev/null || true
cp "$BUILD/classes/CharsetPrimer"*.class "$BUILD/dex-only/"

"$DX" --dex --output="$OUT_DEX" "$BUILD/dex-only" >/dev/null 2>&1

if [ ! -s "$OUT_DEX" ]; then
    echo "ERROR: $OUT_DEX not produced (or empty)"
    rm -rf "$BUILD"
    exit 1
fi

ls -lh "$OUT_DEX"
rm -rf "$BUILD"
echo "Done."
