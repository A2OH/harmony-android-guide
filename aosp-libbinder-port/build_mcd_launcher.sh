#!/bin/bash
# ============================================================================
# M8-Step1 -- McdLauncher.dex build helper (2026-05-13)
#
# Companion to scripts/run-mcd-westlake.sh; compiles aosp-libbinder-port/test/
# McdLauncher.java + the DiscoverWrapperBase pipeline classes it depends on
# into a single dex consumable by dalvikvm.
#
# Output: out/McdLauncher.dex
#
# Runtime input: /data/local/tmp/westlake/mcd.discover.properties.  All
# app-specific configuration lives there; this .dex carries only the M8
# acceptance-signal emission pipeline + generic phase orchestration.
#
# This script is a near-verbatim copy of build_mcd_discover_wrapper.sh --
# delta is just the set of .java files compiled + the output dex name.
# Kept as a separate script (vs flag) so each milestone has a clear,
# bisectable build target.
#
# See docs/engine/M8_STEP1_REPORT.md and docs/engine/CR38_M7_M8_INTEGRATION_
# SCOPING.md §5.2 for the canonical acceptance-signal contract.
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/mcdlauncher-build-$$
OUT_DEX="$SCRIPT_DIR/out/McdLauncher.dex"

mkdir -p "$BUILD/classes" \
         "$BUILD/stubs/android/os" \
         "$BUILD/stubs/dalvik/system" \
         "$BUILD/stubs/com/westlake/services"

# Compile-time stubs (NOT packaged into the dex; the runtime versions
# come from aosp-shim.dex / framework.jar respectively).  Mirrors
# build_mcd_discover_wrapper.sh's stub set verbatim -- McdLauncher
# delegates to DiscoverWrapperBase which has the same API surface.
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
    "$SCRIPT_DIR/test/McdLauncher.java" 2>&1 | grep -v "^Note:" || true

if [ ! -f "$BUILD/classes/McdLauncher.class" ]; then
    echo "ERROR: javac failed (McdLauncher.class missing)"
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

mkdir -p "$BUILD/dex-only"
# Wildcard pattern picks up inner classes
# (McdLauncher$SignalEmittingPrinter.class).
cp "$BUILD/classes/McdLauncher"*.class           "$BUILD/dex-only/"
cp "$BUILD/classes/DiscoverWrapperBase"*.class   "$BUILD/dex-only/"
cp "$BUILD/classes/AsInterfaceTest"*.class       "$BUILD/dex-only/" 2>/dev/null || true
cp "$BUILD/classes/CharsetPrimer"*.class         "$BUILD/dex-only/"

mkdir -p "$SCRIPT_DIR/out"
"$DX" --dex --output="$OUT_DEX" "$BUILD/dex-only" >/dev/null 2>&1

if [ ! -s "$OUT_DEX" ]; then
    echo "ERROR: $OUT_DEX not produced (or empty)"
    rm -rf "$BUILD"
    exit 1
fi

ls -lh "$OUT_DEX"
rm -rf "$BUILD"
echo "Done."
